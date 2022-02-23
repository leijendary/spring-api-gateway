package com.leijendary.spring.boot.apigateway.core.resolver

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.justOrEmpty

@Component
class RemoteAddressKeyResolver : KeyResolver {
    override fun resolve(exchange: ServerWebExchange): Mono<String> {
        return justOrEmpty(exchange.request.remoteAddress?.toString())
    }
}