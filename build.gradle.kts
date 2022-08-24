import org.gradle.api.file.DuplicatesStrategy.INCLUDE

plugins {
    id("org.springframework.boot") version "2.7.3"
    id("io.spring.dependency-management") version "1.0.13.RELEASE"
    id("org.barfuin.gradle.jacocolog") version "2.0.0"
    kotlin("jvm") version "1.6.21"
    kotlin("kapt") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

group = "com.leijendary.spring"
version = "1.0.0"
description = "Spring Boot API Gateway Template for the Microservice Architecture or general purpose"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    maven("https://repo.spring.io/snapshot")
    maven("https://repo.spring.io/milestone")
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator:2.7.3")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive:2.7.3")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway:3.1.3")
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer:3.1.3")
    implementation("org.springframework.cloud:spring-cloud-sleuth-otel-autoconfigure")
    implementation("org.springframework.cloud:spring-cloud-starter-sleuth:3.1.3") {
        configurations {
            all {
                exclude("org.springframework.cloud", "spring-cloud-sleuth-brave")
                exclude("io.zipkin.brave")
            }
        }
    }
    implementation("org.springframework.security:spring-security-oauth2-jose:5.7.3")
    implementation("org.springdoc:springdoc-openapi-ui:1.6.10")
    implementation("org.springdoc:springdoc-openapi-security:1.6.10")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.1")
    implementation("io.opentelemetry:opentelemetry-extension-trace-propagators:1.17.0")
    implementation("io.opentelemetry:opentelemetry-exporter-jaeger:1.17.0")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp-common:1.17.0")
    developmentOnly("org.springframework.boot:spring-boot-devtools:2.7.3")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:2.7.3")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.3")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2021.0.3")
        mavenBom("org.springframework.cloud:spring-cloud-sleuth-otel-dependencies:1.1.0-M6")
    }
}

tasks.compileKotlin {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all")
        jvmTarget = "17"
    }
}

tasks.bootJar {
    duplicatesStrategy = INCLUDE
}

tasks.jar {
    enabled = false
}

tasks.test {
    jvmArgs = listOf("-XX:+AllowRedefinitionToAddDeleteMethods")
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

tasks.processResources {
    filesMatching("application.yaml") {
        expand(project.properties)
    }
}