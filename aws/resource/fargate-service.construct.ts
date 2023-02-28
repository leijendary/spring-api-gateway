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

type FargateServiceConstructProps = {
  clusterArn: string;
  listenerArn: string;
  taskDefinition: TaskDefinition;
};

const environment = process.env.ENVIRONMENT!!;
const id = process.env.STACK_ID!!;
const name = process.env.STACK_NAME!!;
const path = "";
const constructId = `${id}Service-${environment}`;
const clusterId = `${id}Cluster-${environment}`;
const clusterName = `api-cluster-${environment}`;
const vpcId = `${id}Vpc-${environment}`;
const vpcName = `app-vpc-${environment}`;
const securityGroupId = `${id}SecurityGroup-${environment}`;
const securityGroupName = `api-sg-${environment}`;
const listenerId = `${id}Listener-${environment}`;

export class FargateServiceConstruct extends FargateService {
  constructor(scope: Construct, props: FargateServiceConstructProps) {
    const { clusterArn, listenerArn, taskDefinition, ...rest } = props;
    const vpc = Vpc.fromLookup(scope, vpcId, {
      vpcName,
    });
    const securityGroup = SecurityGroup.fromLookupByName(scope, securityGroupId, securityGroupName, vpc);
    const cluster = Cluster.fromClusterAttributes(scope, clusterId, {
      clusterName,
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
      healthCheckGracePeriod: environment !== "prod" ? Duration.minutes(5) : undefined,
      minHealthyPercent: 100,
      maxHealthyPercent: 200,
      desiredCount: 1,
      circuitBreaker: {
        rollback: true,
      },
    };

    super(scope, constructId, config);

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
    const groupName = `${id}-tg-${environment}`;
    const target = this.loadBalancerTarget({
      containerName: name,
      containerPort: 443,
      protocol: Protocol.TCP,
    });
    const targetGroup = new ApplicationTargetGroup(scope, groupId, {
      vpc,
      targets: [target],
      targetGroupName: groupName,
      protocol: ApplicationProtocol.HTTPS,
      port: 443,
      healthCheck: {
        enabled: true,
        path: `${path}/actuator/health`,
      },
    });

    const listener = ApplicationListener.fromLookup(scope, listenerId, { listenerArn });
    listener.addTargetGroups(groupId, {
      targetGroups: [targetGroup],
      conditions: [ListenerCondition.pathPatterns([`${path}/*`])],
      priority: 1,
    });
  }
}
