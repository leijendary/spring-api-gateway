package com.leijendary.spring.apigateway.template.core.filter

import com.leijendary.spring.apigateway.template.core.exception.AccessDeniedException
import com.leijendary.spring.apigateway.template.core.exception.UnauthorizedException
import com.leijendary.spring.apigateway.template.core.extension.logger
import com.leijendary.spring.apigateway.template.core.filter.AuthenticatedGatewayFilterFactory.Config
import com.leijendary.spring.apigateway.template.core.model.ErrorModel
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder.getLocale
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.stereotype.Component

const val HEADER_USER_ID = "X-User-ID"
const val PREFIX_BEARER = "Bearer "

@Component
class AuthenticatedGatewayFilterFactory(
    private val messageSource: MessageSource,
    private val jwtDecoder: JwtDecoder
) : GatewayFilterFactory<Config> {
    private val log = logger()

    inner class Config {
        var scopes: Set<String> = emptySet()
    }

    override fun apply(config: Config) = GatewayFilter { exchange, chain ->
        val token = exchange.request.headers[AUTHORIZATION]
            ?.first()
            ?.replaceFirst(PREFIX_BEARER, "")
            ?: throw unauthorizedException()
        val jwt = try {
            jwtDecoder.decode(token)
        } catch (e: JwtException) {
            val message = e.message

            log.warn(message)

            if (message!!.contains("expired")) {
                throw expiredTokenException()
            }

            throw invalidTokenException()
        }

        val claims = jwt.claims

        if (config.scopes.isNotEmpty()) {
            val scope = claims["scope"].toString()

            checkScope(scope, config.scopes)
        }

        val subject = jwt.subject
        val request = exchange.request
            .mutate()
            .headers {
                it.remove(AUTHORIZATION)
                it[HEADER_USER_ID] = subject
            }
            .build()

        chain.filter(exchange.mutate().request(request).build())
    }

    override fun getConfigClass(): Class<Config> = Config::class.java

    override fun newConfig(): Config = Config()

    private fun checkScope(scope: String, scopes: Set<String>) {
        val hasScope = scope.split(" ").any { it in scopes }

        if (!hasScope) {
            throw accessDeniedException()
        }
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
