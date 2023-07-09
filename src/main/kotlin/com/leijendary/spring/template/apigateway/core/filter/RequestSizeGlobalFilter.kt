package com.leijendary.spring.template.apigateway.core.filter

import com.leijendary.spring.template.apigateway.core.config.properties.RequestProperties
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.cloud.gateway.filter.factory.RequestSizeGatewayFilterFactory
import org.springframework.core.Ordered
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class RequestSizeGlobalFilter(requestProperties: RequestProperties) :
    RequestSizeGatewayFilterFactory(), GlobalFilter, Ordered {
    private val config = RequestSizeConfig().apply {
        maxSize = requestProperties.maxSize
    }

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        return super
            .apply(config)
            .filter(exchange, chain)
    }

    override fun getOrder() = HIGHEST_PRECEDENCE + 1
}
