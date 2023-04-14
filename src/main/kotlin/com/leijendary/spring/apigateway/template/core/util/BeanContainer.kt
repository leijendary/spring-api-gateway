package com.leijendary.spring.apigateway.template.core.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.leijendary.spring.apigateway.template.core.util.SpringContext.Companion.getBean
import io.micrometer.tracing.Tracer

object BeanContainer {
    val OBJECT_MAPPER = getBean(ObjectMapper::class)
    val TRACER = getBean(Tracer::class)
}