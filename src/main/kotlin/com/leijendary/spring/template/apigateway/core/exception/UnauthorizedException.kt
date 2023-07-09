package com.leijendary.spring.template.apigateway.core.exception

import com.leijendary.spring.template.apigateway.core.model.ErrorModel
import org.springframework.http.HttpStatus.UNAUTHORIZED

class UnauthorizedException(errorModel: ErrorModel) : ErrorModelException(listOf(errorModel), UNAUTHORIZED)
