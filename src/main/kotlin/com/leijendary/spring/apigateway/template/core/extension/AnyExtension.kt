package com.leijendary.spring.apigateway.template.core.extension

import com.leijendary.spring.apigateway.template.core.util.BeanContainer.OBJECT_MAPPER

fun Any.toJson(): String {
    return OBJECT_MAPPER.writeValueAsString(this)
}