package com.leijendary.spring.apigateway.template.core.filter

import com.leijendary.spring.apigateway.template.core.exception.AccessDeniedException
import com.leijendary.spring.apigateway.template.core.exception.UnauthorizedException
import com.leijendary.spring.apigateway.template.core.extension.logger
import com.leijendary.spring.apigateway.template.core.filter.AuthenticatedGatewayFilterFactory.Config
import com.leijendary.spring.apigateway.template.core.model.ErrorModel
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder.getLocale
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.stereotype.Component

const val HEADER_USER_ID = "X-User-ID"
const val PREFIX_BEARER = "Bearer "
const val CLAIM_SCOPE = "scope"

@Component
class AuthenticatedGatewayFilterFactory(
    private val messageSource: MessageSource,
    private val reactiveJwtDecoder: ReactiveJwtDecoder,
) : AbstractGatewayFilterFactory<Config>(Config::class.java) {
    private val log = logger()

    class Config {
        var scope: String = ""
    }

    override fun apply(config: Config) = GatewayFilter { exchange, chain ->
        val token = exchange.request.headers[AUTHORIZATION]
            ?.first()
            ?.replaceFirst(PREFIX_BEARER, "")
            ?: throw unauthorizedException()

        reactiveJwtDecoder
            .decode(token)
            .doOnNext { checkScope(config, it) }
            .flatMap {
                val subject = it.subject

                val request = exchange
                    .request
                    .mutate()
                    .headers { headers ->
                        headers.remove(AUTHORIZATION)
                        headers[HEADER_USER_ID] = subject
                    }
                    .build()
                    .let { req ->
                        exchange.mutate().request(req).build()
                    }

                chain.filter(request)
            }
            .onErrorMap { handle(it) }
    }

    override fun shortcutFieldOrder(): List<String> = listOf(CLAIM_SCOPE)

    private fun checkScope(config: Config, jwt: Jwt) {
        // Passed scope is empty. No need to validate
        if (config.scope.isBlank()) {
            return
        }

        val jwtScopes = jwt.claims[CLAIM_SCOPE]
            .toString()
            .split(" ")
        val configScopes = config.scope
            .split(" ")
            .map { it.trim() }
            .toSet()
        val hasScope = jwtScopes.any { it in configScopes }

        if (!hasScope) {
            throw accessDeniedException()
        }
    }

    private fun handle(it: Throwable) = when (it) {
        is JwtException -> {
            val message = it.message!!

            log.warn(message)

            if (message.contains("expired")) {
                throw expiredTokenException()
            }

            throw invalidTokenException()
        }

        else -> it
    }

    private fun unauthorizedException(): UnauthorizedException {
        val errorData = errorData(listOf("header", "authorization"), "access.unauthorized")

        return UnauthorizedException(errorData)
    }

    private fun invalidTokenException(): UnauthorizedException {
        val errorData = errorData(listOf("header", "authorization"), "access.invalid")

        return UnauthorizedException(errorData)
    }

    private fun expiredTokenException(): UnauthorizedException {
        val errorData = errorData(listOf("header", "authorization"), "access.expired")

        return UnauthorizedException(errorData)
    }

    private fun accessDeniedException(): AccessDeniedException {
        val errorData = errorData(listOf("header", "authorization", "scope"), "access.denied")

        return AccessDeniedException(errorData)
    }

    private fun errorData(sources: List<String>, code: String): ErrorModel {
        val message = messageSource.getMessage(code, emptyArray(), getLocale())

        return ErrorModel(sources, code, message)
    }
}
