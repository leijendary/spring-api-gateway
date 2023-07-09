package com.leijendary.spring.template.apigateway.core.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory.BackoffConfig

@ConfigurationProperties(prefix = "retry")
class RetryProperties {
    var retries: Int = 3
    var backoff: BackoffConfig = BackoffConfig()
}
