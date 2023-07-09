package com.leijendary.spring.template.apigateway.core.util

import com.leijendary.spring.template.apigateway.core.util.BeanContainer.tracer
import io.micrometer.tracing.TraceContext

object Tracing {
    fun get(): TraceContext = tracer.nextSpan().context()
}
