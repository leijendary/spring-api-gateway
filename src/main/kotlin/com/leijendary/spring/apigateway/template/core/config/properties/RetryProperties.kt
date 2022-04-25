package com.leijendary.spring.apigateway.template.core.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "retry")
class RetryProperties {
    var retries: Int = 3
    var backoff: Backoff = Backoff()

    inner class Backoff {
        var firstBackoff: Long = 5
        var maxBackoff: Long = 20
        var factor: Int = 2
    }
}