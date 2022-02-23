package com.leijendary.spring.boot.apigateway.core.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "auth")
class AuthProperties {
    var audience: String = ""
    var jwkSetUri: String = ""
}