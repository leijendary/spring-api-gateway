package com.leijendary.spring.template.apigateway.core.exception

import com.leijendary.spring.template.apigateway.core.model.ErrorModel
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR

open class ErrorModelException(
    val errors: List<ErrorModel>,
    val status: HttpStatus = INTERNAL_SERVER_ERROR
) : RuntimeException()
