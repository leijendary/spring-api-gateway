package com.leijendary.spring.template.apigateway.core.filter

import com.leijendary.spring.template.apigateway.core.exception.UnauthorizedException
import com.leijendary.spring.template.apigateway.core.extension.logger
import com.leijendary.spring.template.apigateway.core.model.ErrorModel
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder.getLocale
import org.springframework.core.Ordered
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

const val HEADER_USER_ID = "X-User-ID"
const val HEADER_SCOPE = "X-Scope"
const val PREFIX_BEARER = "Bearer "
const val CLAIM_SCOPE = "scope"

private val source = listOf("header", AUTHORIZATION)

@Component
class AuthorizationGlobalFilter(
    private val messageSource: MessageSource,
    private val reactiveJwtDecoder: ReactiveJwtDecoder,
) : GlobalFilter, Ordered {
    private val log = logger()
    private val expiredError = errorData("access.expired")
    private val invalidError = errorData("access.invalid")

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val token = exchange.request.headers[AUTHORIZATION]
            ?.first()
            ?.replaceFirst(PREFIX_BEARER, "")
            ?: return chain.filter(exchange)

        return reactiveJwtDecoder
            .decode(token)
            .flatMap { mutate(exchange, chain, it) }
            .onErrorMap(::handle)
    }

    override fun getOrder() = HIGHEST_PRECEDENCE + 3

    private fun mutate(exchange: ServerWebExchange, chain: GatewayFilterChain, jwt: Jwt): Mono<Void> {
        val scope = jwt.getClaimAsString(CLAIM_SCOPE)
        val request = exchange.request
            .mutate()
            .headers { headers ->
                headers.remove(AUTHORIZATION)
                headers[HEADER_USER_ID] = jwt.subject
                scope?.let { headers[HEADER_SCOPE] = it }
            }
            .build()
        val mutated = exchange
            .mutate()
            .request(request)
            .build()

        return chain.filter(mutated)
    }

    private fun handle(it: Throwable) = when (it) {
        is JwtException -> {
            val message = it.message!!

            log.warn(message)

            if (message.contains("expired")) {
                throw UnauthorizedException(expiredError)
            }

            throw UnauthorizedException(invalidError)
        }

        else -> it
    }

    private fun errorData(code: String): ErrorModel {
        val message = messageSource.getMessage(code, emptyArray(), getLocale())

        return ErrorModel(source, code, message)
    }
}
