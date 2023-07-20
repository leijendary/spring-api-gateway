package com.leijendary.spring.template.apigateway.core.filter

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class RemoveRequestHeaderGlobalFilter : GlobalFilter, Ordered {
    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request = exchange.request
            .mutate()
            .headers {
                it.remove(HEADER_USER_ID)
                it.remove(HEADER_SCOPE)
            }
            .build()
        val mutated = exchange
            .mutate()
            .request(request)
            .build()

        return chain.filter(mutated)
    }

    override fun getOrder() = HIGHEST_PRECEDENCE + 2
}
