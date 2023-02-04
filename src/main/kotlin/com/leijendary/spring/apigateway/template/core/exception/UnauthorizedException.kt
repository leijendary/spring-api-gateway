package com.leijendary.spring.apigateway.template.core.exception

import com.leijendary.spring.apigateway.template.core.model.ErrorModel
import org.springframework.http.HttpStatus.UNAUTHORIZED

class UnauthorizedException(errorModel: ErrorModel) : ErrorModelException(listOf(errorModel), UNAUTHORIZED)
