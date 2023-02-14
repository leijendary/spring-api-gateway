plugins {
    id("org.springframework.boot") version "3.0.2"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.barfuin.gradle.jacocolog") version "2.0.0"
    kotlin("jvm") version "1.7.22"
    kotlin("kapt") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
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
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))

    // Spring Boot Starter
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")

    // Spring Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Spring Security
    implementation("org.springframework.security:spring-security-oauth2-jose")
    testImplementation("org.springframework.security:spring-security-test")

    // Spring Cloud Starter
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")

    // Cache
    implementation("com.github.ben-manes.caffeine:caffeine")

    // OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.0.2")

    // Devtools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    // Tracing
    implementation("com.github.loki4j:loki-logback-appender:1.4.0-m1")
    implementation("io.micrometer:micrometer-observation")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.opentelemetry:opentelemetry-exporter-zipkin")
}

dependencyManagement {
    imports {
        mavenBom("io.micrometer:micrometer-tracing-bom:1.0.1")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2022.0.1")
    }
}

tasks {
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all")
            jvmTarget = "17"
        }
    }

    bootJar {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    jar {
        enabled = false
    }

    test {
        jvmArgs = listOf("-XX:+AllowRedefinitionToAddDeleteMethods")
        useJUnitPlatform()
        finalizedBy(jacocoTestReport)
    }

    jacocoTestReport {
        dependsOn(test)
    }

    processResources {
        filesMatching("application.yaml") {
            expand(project.properties)
        }
    }
}
