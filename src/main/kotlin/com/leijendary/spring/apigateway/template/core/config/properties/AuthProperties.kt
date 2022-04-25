package com.leijendary.spring.apigateway.template.core.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "auth")
class AuthProperties {
    var audience: String = ""
    var jwkSetUri: String = ""
}