package com.leijendary.spring.apigateway.template.core.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.util.unit.DataSize
import org.springframework.util.unit.DataSize.ofMegabytes

@ConfigurationProperties(prefix = "request")
class RequestProperties {
    var maxSize: DataSize = ofMegabytes(100)
}