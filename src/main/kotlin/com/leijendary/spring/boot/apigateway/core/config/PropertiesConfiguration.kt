package com.leijendary.spring.boot.apigateway.core.config

import com.leijendary.spring.boot.apigateway.core.config.properties.*
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(
    AuthProperties::class,
    InfoProperties::class,
    RequestProperties::class,
    RetryProperties::class,
    ServiceProperties::class
)
class PropertiesConfiguration 