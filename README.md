# Spring Boot API Gateway Template

- This api gateway template is intended for the microservice architecture
- Sample classes are included

# Technologies Used:

- Spring Cloud Gateway
- Spring Cloud LoadBalancer
- Spring Security OAuth2 JOSE
- Spring Actuator
- Spring Configuration Processor
- Spring Autoconfigure Processor
- Spring Devtools
- Spring Data Redis Reactive
- Swagger
- Caffeine
- Docker
- JUnit

# Spring API Gateway Template

### To run the code:

`./gradlew bootRun`

### To run tests:

`./gradlew test`

### To build a JAR file:

`./gradlew bootJar -x test`

### To generate a certificate:

`keytool -genkeypair -alias spring-boot -keyalg RSA -keysize 2048 -validity 3650 -keypass spring-boot -storetype PKCS12 -keystore keystore.p12`
