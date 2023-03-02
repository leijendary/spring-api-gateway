const environment = process.env.ENVIRONMENT!!;

export const isProd = () => environment === "prod";

export default {
  account: process.env.CDK_DEFAULT_ACCOUNT!!,
  region: process.env.CDK_DEFAULT_REGION!!,
  environment,
  port: 80,
  stack: {
    id: process.env.STACK_ID!!,
    name: process.env.STACK_NAME!!,
  },
  vpcId: process.env.VPC_ID!!,
  listenerPath: process.env.LISTENER_PATH!!,
  imageTag: process.env.IMAGE_TAG!!,
};
