import { Duration } from "aws-cdk-lib";
import { IVpc, SecurityGroup, Vpc } from "aws-cdk-lib/aws-ec2";
import { Cluster, FargateService, FargateServiceProps, Protocol, TaskDefinition } from "aws-cdk-lib/aws-ecs";
import {
  ApplicationListener,
  ApplicationProtocol,
  ApplicationTargetGroup,
  ListenerCondition,
} from "aws-cdk-lib/aws-elasticloadbalancingv2";
import { Construct } from "constructs";
import env, { isProd } from "../env";

type FargateServiceConstructProps = {
  vpcId: string;
  clusterArn: string;
  listenerArn: string;
  taskDefinition: TaskDefinition;
};

const environment = env.environment;
const port = env.port;
const { id, name } = env.stack;

export class FargateServiceConstruct extends FargateService {
  constructor(scope: Construct, props: FargateServiceConstructProps) {
    const { vpcId, clusterArn, listenerArn, taskDefinition, ...rest } = props;
    const vpc = Vpc.fromLookup(scope, `${id}Vpc-${environment}`, {
      vpcId,
    });
    const securityGroup = SecurityGroup.fromLookupByName(
      scope,
      `${id}SecurityGroup-${environment}`,
      `api-gateway-${environment}`,
      vpc
    );
    const cluster = Cluster.fromClusterAttributes(scope, `${id}Cluster-${environment}`, {
      clusterName: `api-cluster-${environment}`,
      clusterArn,
      vpc,
      securityGroups: [securityGroup],
    });
    const config: FargateServiceProps = {
      ...rest,
      cluster,
      serviceName: name,
      securityGroups: [securityGroup],
      taskDefinition,
      healthCheckGracePeriod: Duration.seconds(isProd() ? 20 : 300),
      minHealthyPercent: 100,
      maxHealthyPercent: 200,
      desiredCount: 1,
      circuitBreaker: {
        rollback: true,
      },
    };

    super(scope, `${id}Service-${environment}`, config);

    this.setScaling();
    this.setTarget(scope, vpc, listenerArn);
  }

  private setScaling() {
    const scalableTarget = this.autoScaleTaskCount({
      minCapacity: 1,
      maxCapacity: 10,
    });
    scalableTarget.scaleOnMemoryUtilization(`${id}ScaleByMemory-${environment}`, {
      policyName: "ScaleOn70PercentMemory",
      targetUtilizationPercent: 70,
    });
    scalableTarget.scaleOnCpuUtilization(`${id}ScaleByCpu-${environment}`, {
      policyName: "ScaleOn50PercentCpu",
      targetUtilizationPercent: 50,
    });
  }

  private setTarget(scope: Construct, vpc: IVpc, listenerArn: string) {
    const groupId = `${id}TargetGroup-${environment}`;
    const groupName = `${name}-${environment}`;
    const target = this.loadBalancerTarget({
      containerName: name,
      containerPort: port,
      protocol: Protocol.TCP,
    });
    const targetGroup = new ApplicationTargetGroup(scope, groupId, {
      vpc,
      targets: [target],
      targetGroupName: groupName,
      protocol: ApplicationProtocol.HTTP,
      port,
      healthCheck: {
        enabled: true,
        path: "/actuator/health",
      },
    });

    const listener = ApplicationListener.fromLookup(scope, `${id}Listener-${environment}`, { listenerArn });
    listener.addTargetGroups(groupId, {
      targetGroups: [targetGroup],
      conditions: [ListenerCondition.pathPatterns(["/*"])],
      priority: 1,
    });
  }
}
