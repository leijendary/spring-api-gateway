import { Duration } from "aws-cdk-lib";
import { ISecurityGroup, IVpc, SecurityGroup, Vpc } from "aws-cdk-lib/aws-ec2";
import { Cluster, FargateService, FargateServiceProps, TaskDefinition } from "aws-cdk-lib/aws-ecs";
import {
  ApplicationListener,
  ApplicationProtocol,
  ApplicationProtocolVersion,
  ApplicationTargetGroup,
  ListenerCondition,
} from "aws-cdk-lib/aws-elasticloadbalancingv2";
import { INamespace, PrivateDnsNamespace } from "aws-cdk-lib/aws-servicediscovery";
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
const { arn: namespaceArn, id: namespaceId, name: namespaceName } = env.namespace;

export class FargateServiceConstruct extends FargateService {
  constructor(scope: Construct, props: FargateServiceConstructProps) {
    const { vpcId, clusterArn, listenerArn, taskDefinition, ...rest } = props;
    const vpc = getVpc(scope, vpcId);
    const securityGroup = getSecurityGroup(scope, vpc);
    const namespace = getNamespace(scope);
    const cluster = getCluster(scope, clusterArn, vpc, securityGroup, namespace);
    const config: FargateServiceProps = {
      ...rest,
      cluster,
      serviceName: name,
      securityGroups: [securityGroup],
      taskDefinition,
      healthCheckGracePeriod: Duration.seconds(isProd() ? 0 : 200),
      minHealthyPercent: 100,
      maxHealthyPercent: 200,
      desiredCount: 1,
      circuitBreaker: {
        rollback: true,
      },
      serviceConnectConfiguration: {
        services: [
          {
            portMappingName: name,
          },
        ],
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
    });
    const targetGroup = new ApplicationTargetGroup(scope, groupId, {
      vpc,
      targets: [target],
      targetGroupName: groupName,
      protocol: ApplicationProtocol.HTTP,
      protocolVersion: ApplicationProtocolVersion.HTTP2,
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

const getVpc = (scope: Construct, vpcId: string) => {
  return Vpc.fromLookup(scope, `${id}Vpc-${environment}`, {
    vpcId,
  });
};

const getSecurityGroup = (scope: Construct, vpc: IVpc) => {
  return SecurityGroup.fromLookupByName(scope, `${id}SecurityGroup-${environment}`, `api-gateway-${environment}`, vpc);
};

const getNamespace = (scope: Construct) => {
  return PrivateDnsNamespace.fromPrivateDnsNamespaceAttributes(scope, `${id}Namespace-${environment}`, {
    namespaceArn,
    namespaceId,
    namespaceName,
  });
};

const getCluster = (
  scope: Construct,
  clusterArn: string,
  vpc: IVpc,
  securityGroup: ISecurityGroup,
  namespace: INamespace
) => {
  return Cluster.fromClusterAttributes(scope, `${id}Cluster-${environment}`, {
    clusterName: `api-cluster-${environment}`,
    clusterArn,
    vpc,
    securityGroups: [securityGroup],
    defaultCloudMapNamespace: namespace,
  });
};
