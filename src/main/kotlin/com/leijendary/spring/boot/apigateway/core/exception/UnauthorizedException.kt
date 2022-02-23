package com.leijendary.spring.boot.apigateway.core.exception

import com.leijendary.spring.boot.apigateway.core.data.ErrorData
import org.springframework.http.HttpStatus.UNAUTHORIZED

class UnauthorizedException(errorData: ErrorData) : ErrorDataException(listOf(errorData), UNAUTHORIZED)