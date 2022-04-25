package com.leijendary.spring.apigateway.template.core.config

import com.leijendary.spring.apigateway.template.core.config.properties.AuthProperties
import com.leijendary.spring.apigateway.template.core.security.AudienceValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtValidators.createDefault
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder.withJwkSetUri

@Configuration
class SecurityConfiguration(private val authProperties: AuthProperties) {
    @Bean
    fun jwtDecoder(): JwtDecoder {
        val audience = authProperties.audience
        val withAudience = AudienceValidator(audience)
        val defaultValidator = createDefault()
        val validator = DelegatingOAuth2TokenValidator(withAudience, defaultValidator)
        val jwkSetUri = authProperties.jwkSetUri
        val jwtDecoder = withJwkSetUri(jwkSetUri).build()
        jwtDecoder.setJwtValidator(validator)

        return jwtDecoder
    }
}