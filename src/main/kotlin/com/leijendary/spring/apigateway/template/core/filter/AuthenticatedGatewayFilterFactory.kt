package com.leijendary.spring.apigateway.template.core.filter

import com.leijendary.spring.apigateway.template.core.exception.AccessDeniedException
import com.leijendary.spring.apigateway.template.core.exception.UnauthorizedException
import com.leijendary.spring.apigateway.template.core.extension.logger
import com.leijendary.spring.apigateway.template.core.filter.AuthenticatedGatewayFilterFactory.Config
import com.leijendary.spring.apigateway.template.core.model.ErrorModel
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder.getLocale
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

const val HEADER_USER_ID = "X-User-ID"
const val PREFIX_BEARER = "Bearer "
const val CLAIM_SCOPE = "scope"

private val sourceAuthorization = listOf("header", AUTHORIZATION)
private val sourceScope = listOf("header", AUTHORIZATION, "scope")

@Component
class AuthenticatedGatewayFilterFactory(
    private val messageSource: MessageSource,
    private val reactiveJwtDecoder: ReactiveJwtDecoder,
) : AbstractGatewayFilterFactory<Config>(Config::class.java) {
    private val log = logger()
    private val deniedError = errorData(sourceScope, "access.denied")
    private val expiredError = errorData(sourceAuthorization, "access.expired")
    private val invalidError = errorData(sourceAuthorization, "access.invalid")
    private val unauthorizedError = errorData(sourceAuthorization, "access.unauthorized")

    class Config {
        var scope: String = ""
    }

    override fun apply(config: Config) = GatewayFilter { exchange, chain ->
        val token = exchange.request.headers[AUTHORIZATION]
            ?.first()
            ?.replaceFirst(PREFIX_BEARER, "")
            ?: throw UnauthorizedException(unauthorizedError)

        reactiveJwtDecoder
            .decode(token)
            .doOnNext { checkScope(config, it) }
            .flatMap { mutate(exchange, chain, it) }
            .onErrorMap { handle(it) }
    }

    override fun shortcutFieldOrder(): List<String> = listOf(CLAIM_SCOPE)

    private fun mutate(exchange: ServerWebExchange, chain: GatewayFilterChain, jwt: Jwt): Mono<Void> {
        val subject = jwt.subject
        val request = exchange
            .request
            .mutate()
            .headers { headers ->
                headers.remove(AUTHORIZATION)
                headers[HEADER_USER_ID] = subject
            }
            .build()
        val mutated = exchange
            .mutate()
            .request(request)
            .build()

        return chain.filter(mutated)
    }

    private fun checkScope(config: Config, jwt: Jwt) {
        // Passed scope is empty. No need to validate
        if (config.scope.isBlank()) {
            return
        }

        val configScopes = config.scope
            .split(" ")
            .map { it.trim() }
            .toSet()
        val jwtScopes = jwt.claims[CLAIM_SCOPE]
            .toString()
            .split(" ")
        val hasScope = configScopes.any { it in jwtScopes }

        if (!hasScope) {
            throw AccessDeniedException(deniedError)
        }
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

    private fun errorData(sources: List<String>, code: String): ErrorModel {
        val message = messageSource.getMessage(code, emptyArray(), getLocale())

        return ErrorModel(sources, code, message)
    }
}
