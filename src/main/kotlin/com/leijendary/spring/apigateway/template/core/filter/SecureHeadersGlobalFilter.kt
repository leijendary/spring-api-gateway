package com.leijendary.spring.apigateway.template.core.filter

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.cloud.gateway.filter.factory.SecureHeadersGatewayFilterFactory
import org.springframework.cloud.gateway.filter.factory.SecureHeadersProperties
import org.springframework.core.Ordered
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class SecureHeadersGlobalFilter(secureHeadersProperties: SecureHeadersProperties) :
    SecureHeadersGatewayFilterFactory(secureHeadersProperties), GlobalFilter, Ordered {
    private val config = Config().withDefaults(secureHeadersProperties)

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        return super
            .apply(config)
            .filter(exchange, chain)
    }

    override fun getOrder() = LOWEST_PRECEDENCE
}
