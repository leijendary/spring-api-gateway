package com.leijendary.spring.apigateway.template.core.error

import com.leijendary.spring.apigateway.template.core.exception.ErrorModelException
import com.leijendary.spring.apigateway.template.core.extension.fullPath
import com.leijendary.spring.apigateway.template.core.extension.logger
import com.leijendary.spring.apigateway.template.core.extension.toJson
import com.leijendary.spring.apigateway.template.core.model.ErrorModel
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
@Order
class GlobalExceptionHandler(private val messageSource: MessageSource) : ErrorWebExceptionHandler {
    private val log = logger()
    private val genericErrorData = errorData(listOf("server", "internal"), "error.generic")

    override fun handle(exchange: ServerWebExchange, exception: Throwable): Mono<Void> {
        val (errors, status) = buildErrorResponse(exchange, exception)
        val response = exchange.response
        response.headers.contentType = APPLICATION_JSON
        response.rawStatusCode = status

        val json = errors.toJson()
        val dataBuffer = response.bufferFactory().wrap(json.toByteArray(UTF_8))

        return response.writeWith(just(dataBuffer))
    }

    private fun buildErrorResponse(exchange: ServerWebExchange, exception: Throwable): Pair<List<ErrorModel>, Int> {
        val errors = mutableListOf<ErrorModel>()
        var status = INTERNAL_SERVER_ERROR.value()

        when (exception) {
            is ErrorModelException -> {
                errors.addAll(exception.errors)
                status = exception.status.value()
            }

            is ResponseStatusException -> {
                statusResponse(exchange.request, exception, errors)
                status = exception.statusCode.value()
            }

            else -> {
                log.error("Generic error handling", exception)

                errors.add(genericErrorData)
            }
        }

        return errors to status
    }

    private fun statusResponse(
        request: ServerHttpRequest,
        exception: ResponseStatusException,
        errors: MutableList<ErrorModel>
    ) {
        val error = when (exception.statusCode) {
            NOT_FOUND -> errorData(
                listOf("request", "path"),
                "error.mapping.notFound",
                arrayOf(request.uri.fullPath())
            )

            GATEWAY_TIMEOUT -> errorData(listOf("request"), "error.gateway.timeout")
            else -> genericErrorData
        }

        errors.add(error)
    }

    private fun errorData(source: List<Any>, code: String, args: Array<Any> = emptyArray()): ErrorModel {
        val message = messageSource.getMessage(code, args, getLocale())

        return ErrorModel(source, code, message)
    }
}
