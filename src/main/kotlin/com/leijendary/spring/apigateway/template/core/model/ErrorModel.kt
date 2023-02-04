package com.leijendary.spring.apigateway.template.core.model

data class ErrorModel(val source: List<Any>, val code: String, val message: String? = null)
