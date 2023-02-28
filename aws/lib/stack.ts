import { Stack, StackProps } from "aws-cdk-lib";
import { Construct } from "constructs";
import { FargateServiceConstruct } from "./../resource/fargate-service.construct";
import { TaskDefinitionConstruct } from "./../resource/task-definition.construct";

type Config = {
  vpcId: string;
  clusterArn: string;
  repositoryArn: string;
  listenerArn: string;
};

type ConfigMap = {
  [key: string]: Config;
};

const environment = process.env.ENVIRONMENT!!;
const id = process.env.STACK_ID!!;
const name = process.env.STACK_NAME!!;

export class ApplicationStack extends Stack {
  constructor(scope: Construct, props: StackProps) {
    super(scope, `${id}Stack-${environment}`, props);

    const { account, region } = props.env!!;
    const { vpcId, clusterArn, repositoryArn, listenerArn } = getConfig(account!!, region!!);
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

const getConfig = (account: string, region: string) => {
  const configMap: ConfigMap = {
    dev: {
      vpcId: "vpc-0540bcf99b32c65b0",
      repositoryArn: `arn:aws:ecr:${region}:${account}:repository/${name}`,
      clusterArn: `arn:aws:ecs:${region}:${account}:cluster/api-cluster-dev`,
      listenerArn: `arn:aws:elasticloadbalancing:${region}:${account}:listener/app/api-loadbalancer-dev/88e13810e5c8ed1c/b36c961d43a6f4a6`,
    },
  };

  return configMap[environment];
};
