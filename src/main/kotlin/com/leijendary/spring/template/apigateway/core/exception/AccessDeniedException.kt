package com.leijendary.spring.template.apigateway.core.exception

import com.leijendary.spring.template.apigateway.core.model.ErrorModel
import org.springframework.http.HttpStatus.FORBIDDEN

class AccessDeniedException(errorModel: ErrorModel) : ErrorModelException(listOf(errorModel), FORBIDDEN)
