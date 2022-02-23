package com.leijendary.spring.boot.apigateway.core.decorator

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.leijendary.spring.boot.apigateway.core.data.*
import com.leijendary.spring.boot.apigateway.core.util.AnyUtil.toJson
import com.leijendary.spring.boot.apigateway.core.util.SpringContext.Companion.getBean
import org.reactivestreams.Publisher
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils.release
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.http.server.reactive.ServerHttpResponseDecorator
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI

class StandardResponseDecorator(
    private val request: ServerHttpRequest,
    response: ServerHttpResponse,
    private val responseType: Class<*>? = null
) : ServerHttpResponseDecorator(response) {
    private val mapper = getBean(ObjectMapper::class.java)

    override fun writeWith(body: Publisher<out DataBuffer>): Mono<Void> {
        return if (headers.contentType == APPLICATION_JSON) {
            val standardResponse = buildStandardResponse(body as Flux)

            super.writeWith(standardResponse)
        } else {
            super.writeWith(body)
        }
    }

    private fun buildStandardResponse(body: Flux<out DataBuffer>): Flux<out DataBuffer> {
        val uri = request.uri
        val status = delegate.rawStatusCode ?: INTERNAL_SERVER_ERROR.value()

        return body.buffer().map {
            val joined = DefaultDataBufferFactory().join(it)
            val content = ByteArray(joined.readableByteCount())

            joined.read(content)

            release(joined)

            val originalResponse = if (content.isNotEmpty()) mapper.readTree(content) else null
            val standardResponse: Response = when (status) {
                in 100..399 -> dataResponse(originalResponse, status, uri)
                else -> errorResponse(originalResponse, status, uri)
            }
            val bytes = standardResponse.toJson()!!.toByteArray()

            headers.contentLength = bytes.size.toLong()

            super.bufferFactory().wrap(bytes)
        }
    }

    private fun dataResponse(jsonNode: JsonNode?, status: Int, uri: URI): DataResponse<Any> {
        val builder = DataResponse.builder<Any>(uri)
            .status(status)

        if (responseType == null || jsonNode == null) {
            return builder
                .data(jsonNode)
                .build()
        }

        return when (val body = mapper.convertValue(jsonNode, responseType)) {
            is PageResponse<*> -> builder
                .data(body.content)
                .meta(body)
                .links(body)
                .build()
            else -> builder
                .data(body)
                .build()
        }
    }

    private fun errorResponse(originalResponse: Any?, status: Int, uri: URI): ErrorResponse {
        val errors = mapper.convertValue(originalResponse, object : TypeReference<List<ErrorData>>() {})

        return ErrorResponse.builder(uri)
            .addErrors(errors)
            .status(status)
            .build()
    }
}