export default {
  account: process.env.CDK_DEFAULT_ACCOUNT!!,
  region: process.env.CDK_DEFAULT_REGION!!,
  environment: process.env.ENVIRONMENT!!,
  stackId: process.env.STACK_ID!!,
  stackName: process.env.STACK_NAME!!,
  vpcId: process.env.VPC_ID!!,
  listenerPath: process.env.LISTENER_PATH!!,
  imageTag: process.env.IMAGE_TAG!!,
};
