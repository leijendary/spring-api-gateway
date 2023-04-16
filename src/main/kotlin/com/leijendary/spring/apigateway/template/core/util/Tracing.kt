package com.leijendary.spring.apigateway.template.core.util

import com.leijendary.spring.apigateway.template.core.util.BeanContainer.tracer
import io.micrometer.tracing.TraceContext

object Tracing {
    fun get(): TraceContext = tracer.nextSpan().context()
}
