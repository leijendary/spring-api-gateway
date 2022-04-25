package com.leijendary.spring.apigateway.template.core.exception

import com.leijendary.spring.apigateway.template.core.data.ErrorData
import org.springframework.http.HttpStatus.UNAUTHORIZED

class UnauthorizedException(errorData: ErrorData) : ErrorDataException(listOf(errorData), UNAUTHORIZED)