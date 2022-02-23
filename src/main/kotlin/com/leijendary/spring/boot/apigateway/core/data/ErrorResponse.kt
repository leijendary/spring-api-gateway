package com.leijendary.spring.boot.apigateway.core.data

import com.leijendary.spring.boot.apigateway.core.util.fullPath
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import java.net.URI
import java.time.Instant.now

class ErrorResponse(
    val errors: List<ErrorData> = emptyList(),
    val meta: Map<String, Any> = emptyMap(),
    val links: Map<String, String?>? = null
) : Response {
    companion object {
        fun builder(uri: URI): ErrorResponseBuilder {
            return ErrorResponseBuilder()
                .uri(uri)
                .status(INTERNAL_SERVER_ERROR.value())
                .selfLink()
        }
    }

    class ErrorResponseBuilder {
        private val errors: MutableList<ErrorData> = ArrayList()
        private val meta: MutableMap<String, Any> = HashMap()
        private val links: MutableMap<String, String?> = HashMap()
        private lateinit var uri: URI

        fun build(): ErrorResponse {
            meta["timestamp"] = now().toEpochMilli()

            return ErrorResponse(errors, meta, links)
        }

        fun addError(source: List<Any>, code: String, message: String?): ErrorResponseBuilder {
            errors.add(ErrorData(source, code, message))

            return this
        }

        fun addErrors(errors: List<ErrorData>): ErrorResponseBuilder {
            errors.forEach { addError(it.source, it.code, it.message) }

            return this
        }

        fun meta(key: String, value: Any): ErrorResponseBuilder {
            meta[key] = value

            return this
        }

        fun uri(uri: URI): ErrorResponseBuilder {
            this.uri = uri

            return this
        }

        fun status(status: Int): ErrorResponseBuilder {
            meta["status"] = status

            return this
        }

        fun selfLink(): ErrorResponseBuilder {
            links["self"] = uri.fullPath()

            return this
        }
    }
}