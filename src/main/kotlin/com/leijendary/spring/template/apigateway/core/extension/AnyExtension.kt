package com.leijendary.spring.template.apigateway.core.extension

import com.leijendary.spring.template.apigateway.core.util.BeanContainer.objectMapper

fun Any.toJson(): String {
    return objectMapper.writeValueAsString(this)
}
