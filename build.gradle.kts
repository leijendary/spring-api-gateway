import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.10"
    kotlin("kapt") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
}

group = "com.leijendary.spring.apigateway"
version = "1.0.0"
description = "Spring Boot API Gateway Template for the Microservice Architecture or general purpose"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

kapt {
    arguments {
        arg("mapstruct.defaultComponentModel", "spring")
    }
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway:3.1.1")
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer:3.1.1")
    implementation("org.springframework.boot:spring-boot-starter-actuator:2.6.5")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive:2.6.5")
    implementation("org.springframework.security:spring-security-oauth2-jose:5.6.2")
    implementation("org.springdoc:springdoc-openapi-ui:1.6.6")
    implementation("org.springdoc:springdoc-openapi-security:1.6.6")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.github.ben-manes.caffeine:caffeine:3.0.6")
    developmentOnly("org.springframework.boot:spring-boot-devtools:2.6.5")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:2.6.5")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.6.5")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2021.0.1")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.processResources {
    filesMatching("application.yaml") {
        expand(project.properties)
    }
}
