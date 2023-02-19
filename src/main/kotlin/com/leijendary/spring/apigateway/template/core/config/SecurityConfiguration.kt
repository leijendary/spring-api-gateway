package com.leijendary.spring.apigateway.template.core.config

import com.leijendary.spring.apigateway.template.core.config.properties.AuthProperties
import com.leijendary.spring.apigateway.template.core.security.AudienceValidator
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory.INSTANCE
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.JwtValidators.createDefault
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder.withJwkSetUri
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
class SecurityConfiguration(private val authProperties: AuthProperties) {
    @Bean
    fun reactiveJwtDecoder(): ReactiveJwtDecoder {
        val webClient = webClient()
        val audienceValidators = authProperties
            .audiences
            .map { AudienceValidator(it) }
        val defaultValidator = createDefault()
        val validator = DelegatingOAuth2TokenValidator(*audienceValidators.toTypedArray(), defaultValidator)
        val jwkSetUri = authProperties.jwkSetUri
        val jwtDecoder = withJwkSetUri(jwkSetUri)
            .webClient(webClient)
            .build()
        jwtDecoder.setJwtValidator(validator)

        return jwtDecoder
    }

    private fun webClient(): WebClient {
        val sslContext = SslContextBuilder
            .forClient()
            .trustManager(INSTANCE)
            .build()
        val httpClient = HttpClient
            .create()
            .secure { it.sslContext(sslContext) }
        val connector = ReactorClientHttpConnector(httpClient)

        return WebClient.builder()
            .clientConnector(connector)
            .build()
    }
}
