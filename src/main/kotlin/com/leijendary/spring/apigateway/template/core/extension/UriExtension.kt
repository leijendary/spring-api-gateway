package com.leijendary.spring.apigateway.template.core.extension

import java.net.URI

fun URI.fullPath(): String {
    return query?.let { "$path?$it" } ?: path
}