package com.leijendary.spring.apigateway.template.core.resolver

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
import org.springframework.cloud.gateway.support.ipresolver.XForwardedRemoteAddressResolver.maxTrustedIndex
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.just

@Component
class RemoteAddressKeyResolver : KeyResolver {
    private val resolver = maxTrustedIndex(1)

    override fun resolve(exchange: ServerWebExchange): Mono<String> {
        val socketAddress = resolver.resolve(exchange)
        val hostAddress = socketAddress.address.hostAddress

        return just(hostAddress)
    }
}
