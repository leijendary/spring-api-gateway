package com.leijendary.spring.boot.apigateway.core.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "service")
class ServiceProperties : HashMap<String, ServiceProperties.Service>() {

    inner class Service {
        var uri: String = ""
    }
}