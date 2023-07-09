package com.leijendary.spring.template.apigateway.core.filter

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory
import org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory.Config
import org.springframework.core.Ordered
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class RequestRateLimiterGlobalFilter(private val requestRateLimiter: RequestRateLimiterGatewayFilterFactory) :
    GlobalFilter, Ordered {
    private val config = Config()

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        return requestRateLimiter
            .apply(config)
            .filter(exchange, chain)
    }

    override fun getOrder() = HIGHEST_PRECEDENCE
}
