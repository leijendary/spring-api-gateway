package com.leijendary.spring.apigateway.template.core.filter

import com.leijendary.spring.apigateway.template.core.resolver.RemoteAddressKeyResolver
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter
import org.springframework.core.Ordered
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class RequestRateLimiterGlobalFilter(
    redisRateLimiter: RedisRateLimiter,
    remoteAddressKeyResolver: RemoteAddressKeyResolver
) : RequestRateLimiterGatewayFilterFactory(redisRateLimiter, remoteAddressKeyResolver), GlobalFilter, Ordered {
    private val config = Config()

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        return super
            .apply(config)
            .filter(exchange, chain)
    }

    override fun getOrder() = HIGHEST_PRECEDENCE
}
