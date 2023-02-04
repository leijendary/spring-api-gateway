package com.leijendary.spring.apigateway.template.core.filter

import io.micrometer.observation.Observation
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor
import io.micrometer.tracing.Tracer
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

const val HEADER_TRACE_ID = "X-Trace-ID"

@Component
class TraceFilter(private val tracer: Tracer) : GlobalFilter {
    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        return chain
            .filter(exchange)
            .contextWrite {
                val observation = it.get<Observation>(ObservationThreadLocalAccessor.KEY)
                observation.openScope()

                val traceId = tracer.nextSpan().context().traceId()

                exchange.response.headers.set(HEADER_TRACE_ID, traceId)

                it
            }
    }
}
