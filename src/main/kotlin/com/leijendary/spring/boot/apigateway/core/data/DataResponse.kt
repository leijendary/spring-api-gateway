package com.leijendary.spring.boot.apigateway.core.data

import com.leijendary.spring.boot.apigateway.core.util.fullPath
import org.springframework.http.HttpStatus.OK
import org.springframework.web.util.UriComponentsBuilder.fromUri
import java.net.URI
import java.time.Instant.now

class DataResponse<T>(
    val data: T? = null,
    val meta: Map<String, Any> = emptyMap(),
    val links: Map<String, String?>? = null
) : Response {
    companion object {
        fun <T> builder(uri: URI): DataResponseBuilder<T> {
            return DataResponseBuilder<T>()
                .uri(uri)
                .status(OK.value())
                .selfLink()
        }
    }

    class DataResponseBuilder<T> {
        private var data: T? = null
        private val meta: MutableMap<String, Any> = HashMap()
        private val links: MutableMap<String, String?> = HashMap()
        private lateinit var uri: URI

        fun build(): DataResponse<T> {
            meta["timestamp"] = now().toEpochMilli()

            return DataResponse(data, meta, links)
        }

        fun data(data: T?): DataResponseBuilder<T> {
            this.data = data

            return this
        }

        fun meta(key: String, value: Any): DataResponseBuilder<T> {
            meta[key] = value

            return this
        }

        fun status(status: Int): DataResponseBuilder<T> {
            meta["status"] = status

            return this
        }

        fun meta(page: PageResponse<*>): DataResponseBuilder<T> {
            meta["page"] = PageMeta(page)

            return this
        }

        fun uri(uri: URI): DataResponseBuilder<T> {
            this.uri = uri

            return this
        }

        fun selfLink(): DataResponseBuilder<T> {
            links["self"] = uri.fullPath()

            return this
        }

        fun links(page: PageResponse<*>): DataResponseBuilder<T> {
            links["self"] = createLink(page.number, page.size)

            if (page.previous) {
                links["previous"] = createLink(page.number - 1, page.size)
            }

            if (page.next) {
                links["next"] = createLink(page.number + 1, page.size)
            }

            links["last"] = createLink(page.totalPages - 1, page.size)

            return this
        }

        private fun createLink(page: Int, size: Int): String {
            return fromUri(uri)
                .replaceQueryParam("page", page)
                .replaceQueryParam("size", size)
                .build()
                .toUri()
                .fullPath()
        }
    }
}