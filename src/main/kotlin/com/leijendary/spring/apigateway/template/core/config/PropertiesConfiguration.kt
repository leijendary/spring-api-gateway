package com.leijendary.spring.apigateway.template.core.config

import com.leijendary.spring.apigateway.template.core.config.properties.AuthProperties
import com.leijendary.spring.apigateway.template.core.config.properties.InfoProperties
import com.leijendary.spring.apigateway.template.core.config.properties.ServiceProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(AuthProperties::class, InfoProperties::class, ServiceProperties::class)
class PropertiesConfiguration 
