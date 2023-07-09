package com.leijendary.spring.template.apigateway.core.config

import com.leijendary.spring.template.apigateway.core.config.properties.AuthProperties
import com.leijendary.spring.template.apigateway.core.security.JwtAudienceValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.JwtValidators.createDefaultWithIssuer
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder.withJwkSetUri
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder

@Configuration
class SecurityConfiguration(private val authProperties: AuthProperties) {
    @Bean
    fun reactiveJwtDecoder(): ReactiveJwtDecoder {
        val issuer = authProperties.issuer
        val defaultValidator = createDefaultWithIssuer(issuer)
        val audience = authProperties.audience
        val audienceValidator = JwtAudienceValidator(audience)
        val validator = DelegatingOAuth2TokenValidator(defaultValidator, audienceValidator)
        val jwkSetUri = authProperties.jwkSetUri

        return withJwkSetUri(jwkSetUri)
            .build()
            .apply {
                setJwtValidator(validator)
            }
    }
}
