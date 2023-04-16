package com.leijendary.spring.apigateway.template.core.extension

import com.leijendary.spring.apigateway.template.core.util.BeanContainer.objectMapper

fun Any.toJson(): String {
    return objectMapper.writeValueAsString(this)
}