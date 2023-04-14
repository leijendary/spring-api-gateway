package com.leijendary.spring.apigateway.template.core.util

import com.leijendary.spring.apigateway.template.core.util.BeanContainer.TRACER
import io.micrometer.tracing.TraceContext

object Tracing {
    fun get(): TraceContext = TRACER.nextSpan().context()
}
