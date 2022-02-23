package com.leijendary.spring.boot.apigateway.core.data

data class ErrorData(val source: List<Any>, val code: String, val message: String? = null)