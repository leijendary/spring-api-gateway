package com.leijendary.spring.boot.apigateway.core.util

import java.net.URI

fun URI.fullPath(): String {
    return query?.let { "$path?$it" } ?: path
}