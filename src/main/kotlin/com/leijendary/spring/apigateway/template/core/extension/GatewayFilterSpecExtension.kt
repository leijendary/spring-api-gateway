package com.leijendary.spring.apigateway.template.core.extension

import com.leijendary.spring.apigateway.template.core.config.properties.RequestProperties
import com.leijendary.spring.apigateway.template.core.config.properties.RetryProperties
import com.leijendary.spring.apigateway.template.core.filter.AuthenticatedGatewayFilterFactory
import com.leijendary.spring.apigateway.template.core.filter.HEADER_TRACE_ID
import com.leijendary.spring.apigateway.template.core.resolver.RemoteAddressKeyResolver
import com.leijendary.spring.apigateway.template.core.util.SpringContext.Companion.getBean
import org.springframework.cloud.gateway.filter.factory.DedupeResponseHeaderGatewayFilterFactory.Strategy.RETAIN_FIRST
import org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory.BackoffConfig
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec
import org.springframework.http.HttpStatus.BAD_GATEWAY
import org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import java.time.Duration.ofMillis

private val authenticatedGatewayFilterFactory = getBean(AuthenticatedGatewayFilterFactory::class)
private val redisRateLimiter = getBean(RedisRateLimiter::class)
private val remoteAddressKeyResolver = getBean(RemoteAddressKeyResolver::class)
private val requestProperties = getBean(RequestProperties::class)
private val retryProperties = getBean(RetryProperties::class)
private val backoff = retryProperties.backoff.let {
    BackoffConfig(ofMillis(it.firstBackoff), ofMillis(it.maxBackoff), it.factor, true)
}

fun GatewayFilterSpec.defaultFilters(filterSpecs: () -> GatewayFilterSpec) {
    requestRateLimiter {
        it.keyResolver = remoteAddressKeyResolver
        it.rateLimiter = redisRateLimiter
    }
    setRequestSize(requestProperties.maxSize)
    filterSpecs()
    retry {
        it.retries = retryProperties.retries
        it.backoff = backoff
        it.setStatuses(BAD_GATEWAY, SERVICE_UNAVAILABLE)
    }
    dedupeResponseHeader(HEADER_TRACE_ID, RETAIN_FIRST.name)
}

fun GatewayFilterSpec.authenticated(vararg scopes: String = emptyArray()): GatewayFilterSpec {
    return filter(authenticatedGatewayFilterFactory.apply { it.scopes = scopes.toSet() })
}
