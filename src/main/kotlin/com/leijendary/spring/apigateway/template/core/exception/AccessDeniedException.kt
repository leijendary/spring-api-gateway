package com.leijendary.spring.apigateway.template.core.exception

import com.leijendary.spring.apigateway.template.core.model.ErrorModel
import org.springframework.http.HttpStatus.FORBIDDEN

class AccessDeniedException(errorModel: ErrorModel) : ErrorDataException(listOf(errorModel), FORBIDDEN)
