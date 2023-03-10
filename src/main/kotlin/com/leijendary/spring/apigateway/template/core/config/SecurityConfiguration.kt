package com.leijendary.spring.apigateway.template.core.config

import com.leijendary.spring.apigateway.template.core.config.properties.AuthProperties
import com.leijendary.spring.apigateway.template.core.security.AudienceValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.JwtValidators.createDefault
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder.withJwkSetUri
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder

@Configuration
class SecurityConfiguration(private val authProperties: AuthProperties) {
    @Bean
    fun reactiveJwtDecoder(): ReactiveJwtDecoder {
        val audienceValidators = authProperties
            .audiences
            .map { AudienceValidator(it) }
        val defaultValidator = createDefault()
        val validator = DelegatingOAuth2TokenValidator(*audienceValidators.toTypedArray(), defaultValidator)
        val jwkSetUri = authProperties.jwkSetUri

        return withJwkSetUri(jwkSetUri)
            .build()
            .apply {
                setJwtValidator(validator)
            }
    }
}
