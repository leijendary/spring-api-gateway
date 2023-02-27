import { RemovalPolicy } from "aws-cdk-lib";
import { IRepository, Repository } from "aws-cdk-lib/aws-ecr";
import {
  Compatibility,
  ContainerImage,
  CpuArchitecture,
  LogDriver,
  OperatingSystemFamily,
  Protocol,
  TaskDefinition,
  TaskDefinitionProps,
} from "aws-cdk-lib/aws-ecs";
import { PolicyDocument, PolicyStatement, Role, ServicePrincipal } from "aws-cdk-lib/aws-iam";
import { LogGroup, RetentionDays } from "aws-cdk-lib/aws-logs";
import { Construct } from "constructs";

type TaskDefinitionConstructProps = {
  repositoryArn: string;
};

const environment = process.env.ENVIRONMENT!!;
const imageTag = process.env.IMAGE_TAG!!;
const id = process.env.STACK_ID!!;
const name = process.env.STACK_NAME!!;
const family = `${name}-${environment}`;
const repositoryId = `${id}Repository-${environment}`;
const constructId = `${id}TaskDefinition-${environment}`;
const containerId = `${id}Container-${environment}`;
const assumedBy = new ServicePrincipal("ecs-tasks.amazonaws.com");
const logPrefix = "/ecs/fargate";

export class TaskDefinitionConstruct extends TaskDefinition {
  constructor(scope: Construct, props: TaskDefinitionConstructProps) {
    const { repositoryArn } = props;
    const memoryMiB = environment === "prod" ? "2 GB" : "0.5 GB";
    const cpu = environment === "prod" ? "1 vCPU" : "0.25 vCPU";
    const repository = Repository.fromRepositoryArn(scope, repositoryId, repositoryArn);
    const image = ContainerImage.fromEcrRepository(repository, imageTag);
    const logGroup = createLogGroup(scope);
    const taskRole = createTaskRole(scope);
    const executionRole = createExecutionRole(scope, logGroup, repository);
    const config: TaskDefinitionProps = {
      family,
      compatibility: Compatibility.FARGATE,
      memoryMiB,
      cpu,
      runtimePlatform: {
        cpuArchitecture: CpuArchitecture.ARM64,
        operatingSystemFamily: OperatingSystemFamily.LINUX,
      },
      taskRole,
      executionRole,
    };

    super(scope, constructId, config);

    this.container(image, logGroup);
    this.trustPolicy(taskRole, executionRole);
  }

  private container(image: ContainerImage, logGroup: LogGroup) {
    this.addContainer(containerId, {
      containerName: name,
      image,
      logging: LogDriver.awsLogs({
        streamPrefix: logPrefix,
        logGroup,
      }),
      portMappings: [
        {
          containerPort: 443,
          hostPort: 443,
          protocol: Protocol.TCP,
        },
      ],
    });
  }

  private trustPolicy(taskRole: Role, executionRole: Role) {
    const trustPolicy = new PolicyStatement({
      actions: ["sts:AssumeRole"],
      resources: [this.taskDefinitionArn],
    });

    taskRole.addToPolicy(trustPolicy);
    executionRole.addToPolicy(trustPolicy);
  }
}

const createLogGroup = (scope: Construct) => {
  return new LogGroup(scope, `${id}LogGroup-${environment}`, {
    logGroupName: `${logPrefix}/${family}`,
    removalPolicy: RemovalPolicy.DESTROY,
    retention: RetentionDays.ONE_MONTH,
  });
};

const createTaskRole = (scope: Construct) => {
  return new Role(scope, `${id}TaskRole-${environment}`, {
    roleName: `${id}TaskRole-${environment}`,
    assumedBy,
  });
};

const createExecutionRole = (scope: Construct, logGroup: LogGroup, repository: IRepository) => {
  return new Role(scope, `${id}ExecutionRole-${environment}`, {
    roleName: `${id}ExecutionRole-${environment}`,
    assumedBy,
    inlinePolicies: {
      [`${id}ExecutionRolePolicy-${environment}`]: new PolicyDocument({
        statements: [
          new PolicyStatement({
            actions: [
              "ecr:BatchCheckLayerAvailability",
              "ecr:BatchGetImage",
              "ecr:GetAuthorizationToken",
              "ecr:GetDownloadUrlForLayer",
            ],
            resources: [repository.repositoryArn],
          }),
          new PolicyStatement({
            actions: ["logs:CreateLogStream", "logs:PutLogEvents"],
            resources: [logGroup.logGroupArn],
          }),
        ],
      }),
    },
  });
};