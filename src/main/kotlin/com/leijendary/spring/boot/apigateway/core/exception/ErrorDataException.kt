package com.leijendary.spring.boot.apigateway.core.exception

import com.leijendary.spring.boot.apigateway.core.data.ErrorData
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR

open class ErrorDataException(
    val errors: List<ErrorData>,
    val status: HttpStatus = INTERNAL_SERVER_ERROR
) : RuntimeException()