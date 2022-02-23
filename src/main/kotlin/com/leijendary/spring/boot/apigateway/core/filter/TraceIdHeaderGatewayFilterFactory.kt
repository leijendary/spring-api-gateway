package com.leijendary.spring.boot.apigateway.core.filter

import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory.NameConfig
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono.fromRunnable
import java.util.UUID.randomUUID

@Component
class TraceIdHeaderGatewayFilterFactory : GatewayFilterFactory<NameConfig> {
    companion object {
        const val HEADER_TRACE_ID = "X-Trace-ID"
    }

    override fun apply(config: NameConfig): GatewayFilter = GatewayFilter { exchange, chain ->
        val name = config.name
        val traceId = randomUUID().toString()
        val request = exchange.request.mutate()
            .headers { it[name] = traceId }
            .build()

        chain.filter(exchange.mutate().request(request).build())
            .then(fromRunnable { exchange.response.headers[name] = traceId })
    }

    override fun getConfigClass(): Class<NameConfig> = NameConfig::class.java

    override fun newConfig(): NameConfig = NameConfig()
}