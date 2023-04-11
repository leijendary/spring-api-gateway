package com.leijendary.spring.apigateway.template.core.extension

import com.fasterxml.jackson.databind.ObjectMapper
import com.leijendary.spring.apigateway.template.core.util.SpringContext.Companion.getBean

private val mapper = getBean(ObjectMapper::class)

fun Any.toJson(): String {
    return mapper.writeValueAsString(this)
}