import { Stack, StackProps } from "aws-cdk-lib";
import { Construct } from "constructs";
import env from "../env";
import { FargateServiceConstruct } from "./../resource/fargate-service.construct";
import { TaskDefinitionConstruct } from "./../resource/task-definition.construct";

const environment = env.environment;
const id = env.stackId;
const name = env.stackName;
const vpcId = env.vpcId;
const listenerPath = env.listenerPath;

export class ApplicationStack extends Stack {
  constructor(scope: Construct, props: StackProps) {
    super(scope, `${id}Stack-${environment}`, props);

    const { account, region } = props.env!!;
    const repositoryArn = `arn:aws:ecr:${region}:${account}:repository/${name}`;
    const clusterArn = `arn:aws:ecs:${region}:${account}:cluster/api-cluster-${environment}`;
    const listenerArn = `arn:aws:elasticloadbalancing:${region}:${account}:listener/${listenerPath}`;
    const taskDefinition = new TaskDefinitionConstruct(this, {
      ...props,
      repositoryArn,
    });

    new FargateServiceConstruct(this, {
      ...props,
      vpcId,
      clusterArn,
      taskDefinition,
      listenerArn,
    });
  }
}
