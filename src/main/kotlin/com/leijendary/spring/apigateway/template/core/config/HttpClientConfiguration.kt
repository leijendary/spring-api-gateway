package com.leijendary.spring.apigateway.template.core.config

import org.springframework.cloud.gateway.config.HttpClientCustomizer
import org.springframework.context.annotation.Configuration
import reactor.netty.http.HttpProtocol.H2C
import reactor.netty.http.HttpProtocol.HTTP11
import reactor.netty.http.client.HttpClient

@Configuration
class HttpClientConfiguration : HttpClientCustomizer {
    override fun customize(httpClient: HttpClient): HttpClient {
        return httpClient.protocol(HTTP11, H2C)
    }
}
