import { App } from "aws-cdk-lib";
import "source-map-support/register";
import { ApplicationStack } from "../lib/stack";

const app = new App();

new ApplicationStack(app, {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: process.env.CDK_DEFAULT_REGION,
  },
});

app.synth();
