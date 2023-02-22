package com.leijendary.spring.apigateway.template.core.util

import com.leijendary.spring.apigateway.template.core.util.SpringContext.Companion.getBean
import io.micrometer.tracing.TraceContext
import io.micrometer.tracing.Tracer

private val tracer = getBean(Tracer::class)

object Tracing {
    fun get(): TraceContext = tracer.nextSpan().context()
}
