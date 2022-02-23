package com.leijendary.spring.boot.apigateway.core.error

import com.leijendary.spring.boot.apigateway.core.data.ErrorData
import com.leijendary.spring.boot.apigateway.core.data.ErrorResponse
import com.leijendary.spring.boot.apigateway.core.data.ErrorResponse.ErrorResponseBuilder
import com.leijendary.spring.boot.apigateway.core.exception.ErrorDataException
import com.leijendary.spring.boot.apigateway.core.util.AnyUtil.toJson
import com.leijendary.spring.boot.apigateway.core.util.fullPath
import com.leijendary.spring.boot.apigateway.core.util.logger
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder.getLocale
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus.*
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.just
import java.nio.charset.StandardCharsets.UTF_8

/**
 * Handle Gateway errors. This has an order of -2 to surpass
 * DefaultErrorWebExceptionHandler which has an order of -1
 */
@Component
@Order(-2)
class GlobalExceptionHandler(private val messageSource: MessageSource) : ErrorWebExceptionHandler {
    private val log = logger()
    private val genericErrorData = errorData(listOf("server", "internal"), "error.generic")

    override fun handle(exchange: ServerWebExchange, exception: Throwable): Mono<Void> {
        val error = buildErrorResponse(exchange, exception)
        val json = error.toJson()
        val response = exchange.response
        response.headers.contentType = APPLICATION_JSON
        response.rawStatusCode = error.meta["status"] as Int

        val dataBuffer = response.bufferFactory().wrap(json?.toByteArray(UTF_8) ?: byteArrayOf(0))

        return response.writeWith(just(dataBuffer))
    }

    private fun buildErrorResponse(exchange: ServerWebExchange, exception: Throwable): ErrorResponse {
        val builder = ErrorResponse.builder(exchange.request.uri)

        return when (exception) {
            is ErrorDataException -> {
                builder
                    .addErrors(exception.errors)
                    .status(exception.status.value())
            }
            is ResponseStatusException -> {
                statusResponse(exchange.request, exception, builder)
            }
            else -> {
                log.error("Generic error handling", exception)

                builder
                    .addErrors(listOf(genericErrorData))
                    .status(INTERNAL_SERVER_ERROR.value())
            }
        }.build()
    }

    private fun statusResponse(
        request: ServerHttpRequest,
        exception: ResponseStatusException,
        builder: ErrorResponseBuilder
    ): ErrorResponseBuilder {
        return when (exception.status) {
            NOT_FOUND -> errorData(
                listOf("request", "path"),
                "error.mapping.notFound",
                arrayOf(request.uri.fullPath())
            )
            GATEWAY_TIMEOUT -> errorData(listOf("request"), "error.gateway.timeout")
            else -> genericErrorData
        }.let {
            builder
                .addErrors(listOf(it))
                .status(exception.rawStatusCode)
        }
    }

    private fun errorData(source: List<Any>, code: String, args: Array<Any> = emptyArray()): ErrorData {
        val message = messageSource.getMessage(code, args, getLocale())

        return ErrorData(source, code, message)
    }
}