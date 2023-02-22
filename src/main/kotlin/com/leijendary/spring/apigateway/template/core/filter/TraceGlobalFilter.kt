package com.leijendary.spring.apigateway.template.core.filter

import com.leijendary.spring.apigateway.template.core.util.Tracing
import io.micrometer.observation.Observation
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor.KEY
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.just

const val HEADER_TRACE_ID = "X-Trace-ID"

@Component
class TraceFilter : GlobalFilter, Ordered {
    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        return chain
            .filter(exchange)
            .then(trace(exchange))
            .then()
    }

    override fun getOrder() = LOWEST_PRECEDENCE

    private fun trace(exchange: ServerWebExchange) = just(exchange).contextWrite {
        it.apply {
            get<Observation>(KEY).scoped {
                val traceId = Tracing.get().traceId()

                exchange.response.headers.set(HEADER_TRACE_ID, traceId)
            }
        }
    }
}
