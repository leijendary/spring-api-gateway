package com.leijendary.spring.template.apigateway.core.filter

import com.leijendary.spring.template.apigateway.core.exception.AccessDeniedException
import com.leijendary.spring.template.apigateway.core.exception.UnauthorizedException
import com.leijendary.spring.template.apigateway.core.filter.AuthenticatedGatewayFilterFactory.Config
import com.leijendary.spring.template.apigateway.core.model.ErrorModel
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder.getLocale
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Component

private val sourceAuthorization = listOf("header", AUTHORIZATION)
private val sourceScope = listOf("header", AUTHORIZATION, "scope")

@Component
class AuthenticatedGatewayFilterFactory(private val messageSource: MessageSource) :
    AbstractGatewayFilterFactory<Config>(Config::class.java) {
    private val deniedError = errorData(sourceScope, "access.denied")
    private val unauthorizedError = errorData(sourceAuthorization, "access.unauthorized")

    class Config {
        var scope: String = ""
    }

    override fun apply(config: Config) = GatewayFilter { exchange, chain ->
        val hasUserId = exchange.request.headers.containsKey(HEADER_USER_ID)

        if (!hasUserId) {
            throw UnauthorizedException(unauthorizedError)
        }

        val scope = exchange.request.headers[HEADER_SCOPE]?.first() ?: ""

        checkScope(config, scope)

        chain.filter(exchange)
    }

    override fun shortcutFieldOrder(): List<String> = listOf(CLAIM_SCOPE)

    private fun checkScope(config: Config, scope: String) {
        // Passed scope is empty. No need to validate
        if (config.scope.isBlank()) {
            return
        }

        val configScopes = config.scope
            .split(" ")
            .map { it.trim() }
            .toSet()
        val headerScopes = scope.split(" ")
        val hasScope = configScopes.any { it in headerScopes }

        if (!hasScope) {
            throw AccessDeniedException(deniedError)
        }
    }

    private fun errorData(sources: List<String>, code: String): ErrorModel {
        val message = messageSource.getMessage(code, emptyArray(), getLocale())

        return ErrorModel(sources, code, message)
    }
}
