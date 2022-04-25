package com.leijendary.spring.apigateway.template.core.exception

import com.leijendary.spring.apigateway.template.core.data.ErrorData
import org.springframework.http.HttpStatus.FORBIDDEN

class AccessDeniedException(errorData: ErrorData) : ErrorDataException(listOf(errorData), FORBIDDEN)