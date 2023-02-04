# Spring Boot API Gateway Template

- This api gateway template is intended for the microservice architecture
- Sample classes are included

# Technologies Used:

- Kotlin
- Spring Boot 3
- Spring Actuator
- Spring Cloud Gateway
- Spring Cloud LoadBalancer
- Spring Configuration Processor
- Spring Data Redis Reactive
- Spring Devtools
- Spring Security OAuth2 JOSE
- SpringDoc OpenAPI
- Caffeine
- Docker
- JUnit
- Kubernetes
- Micrometer Tracing
- Prometheus
- Zipkin

# Spring API Gateway Template

### To run the code:

`./gradlew bootRun`

### To run tests:

`./gradlew test`

### To build a JAR file:

`./gradlew bootJar -x test`

### To generate a certificate:

`keytool -genkeypair -alias spring-boot -keyalg RSA -keysize 2048 -validity 3650 -keypass spring-boot -storetype PKCS12 -keystore keystore.p12`
