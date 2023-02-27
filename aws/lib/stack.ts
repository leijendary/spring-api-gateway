import { Stack, StackProps } from "aws-cdk-lib";
import { Construct } from "constructs";
import { FargateServiceConstruct } from "./../resource/fargate-service.construct";
import { TaskDefinitionConstruct } from "./../resource/task-definition.construct";

type Config = {
  repositoryArn: string;
  clusterArn: string;
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
    const { repositoryArn, listenerArn, clusterArn } = getConfig(account!!, region!!);
    const taskDefinition = new TaskDefinitionConstruct(this, {
      ...props,
      repositoryArn,
    });

    new FargateServiceConstruct(this, {
      ...props,
      taskDefinition,
      listenerArn,
      clusterArn,
    });
  }
}

const getConfig = (account: string, region: string) => {
  const configMap: ConfigMap = {
    dev: {
      repositoryArn: `arn:aws:ecr:${region}:${account}:repository/${name}`,
      clusterArn: `arn:aws:ecs:${region}:${account}:cluster/api-cluster-dev`,
      listenerArn: `arn:aws:elasticloadbalancing:${region}:${account}:listener/app/api-loadbalancer-dev/00c4f3aed575b71d/01388fd3730ed00b`,
    },
  };

  return configMap[environment];
};
