package com.leijendary.spring.boot.apigateway.core.util

import com.leijendary.spring.boot.apigateway.core.config.properties.RequestProperties
import com.leijendary.spring.boot.apigateway.core.config.properties.RetryProperties
import com.leijendary.spring.boot.apigateway.core.filter.AuthenticatedGatewayFilterFactory
import com.leijendary.spring.boot.apigateway.core.filter.StandardResponseGatewayFilterFactory
import com.leijendary.spring.boot.apigateway.core.filter.TraceIdHeaderGatewayFilterFactory
import com.leijendary.spring.boot.apigateway.core.filter.TraceIdHeaderGatewayFilterFactory.Companion.HEADER_TRACE_ID
import com.leijendary.spring.boot.apigateway.core.resolver.RemoteAddressKeyResolver
import com.leijendary.spring.boot.apigateway.core.util.SpringContext.Companion.getBean
import org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory.BackoffConfig
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec
import org.springframework.http.HttpStatus.BAD_GATEWAY
import org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import java.time.Duration.ofMillis

private val authenticatedGatewayFilterFactory = getBean(AuthenticatedGatewayFilterFactory::class.java)
private val redisRateLimiter = getBean(RedisRateLimiter::class.java)
private val remoteAddressKeyResolver = getBean(RemoteAddressKeyResolver::class.java)
private val requestProperties = getBean(RequestProperties::class.java)
private val retryProperties = getBean(RetryProperties::class.java)
private val standardResponseGatewayFilterFactory = getBean(StandardResponseGatewayFilterFactory::class.java)
private val traceIdHeaderGatewayFilterFactory = getBean(TraceIdHeaderGatewayFilterFactory::class.java)

fun GatewayFilterSpec.defaultFilters(responseType: Class<*>? = null, filterSpecs: () -> GatewayFilterSpec) {
    traceIdHeader(HEADER_TRACE_ID)
    setRequestSize(requestProperties.maxSize)
    requestRateLimiter {
        it.keyResolver = remoteAddressKeyResolver
        it.rateLimiter = redisRateLimiter
    }
    filterSpecs()
    retry {
        it.retries = retryProperties.retries
        it.backoff = BackoffConfig(
            ofMillis(retryProperties.backoff.firstBackoff),
            ofMillis(retryProperties.backoff.maxBackoff),
            retryProperties.backoff.factor,
            true
        )
        it.setStatuses(BAD_GATEWAY, SERVICE_UNAVAILABLE)
    }
    standardResponse(responseType)
}

fun GatewayFilterSpec.authenticated(vararg scopes: String): GatewayFilterSpec {
    return filter(authenticatedGatewayFilterFactory.apply { it.scopes = scopes.toSet() })
}

fun GatewayFilterSpec.traceIdHeader(name: String): GatewayFilterSpec {
    return filter(traceIdHeaderGatewayFilterFactory.apply { it.name = name })
}

fun GatewayFilterSpec.standardResponse(responseType: Class<*>? = null): GatewayFilterSpec {
    return filter(standardResponseGatewayFilterFactory.apply { it.responseType = responseType })
}