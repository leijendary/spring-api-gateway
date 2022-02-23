package com.leijendary.spring.boot.apigateway.core.exception

import com.leijendary.spring.boot.apigateway.core.data.ErrorData
import org.springframework.http.HttpStatus.FORBIDDEN

class AccessDeniedException(errorData: ErrorData) : ErrorDataException(listOf(errorData), FORBIDDEN)