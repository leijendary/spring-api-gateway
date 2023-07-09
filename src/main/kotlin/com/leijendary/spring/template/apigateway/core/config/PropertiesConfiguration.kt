package com.leijendary.spring.template.apigateway.core.config

import com.leijendary.spring.template.apigateway.core.config.properties.AuthProperties
import com.leijendary.spring.template.apigateway.core.config.properties.InfoProperties
import com.leijendary.spring.template.apigateway.core.config.properties.RequestProperties
import com.leijendary.spring.template.apigateway.core.config.properties.RetryProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(
    AuthProperties::class,
    InfoProperties::class,
    RequestProperties::class,
    RetryProperties::class,
)
class PropertiesConfiguration 
