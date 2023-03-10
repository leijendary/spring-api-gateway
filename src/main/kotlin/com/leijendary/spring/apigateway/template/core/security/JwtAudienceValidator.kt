package com.leijendary.spring.apigateway.template.core.security

import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2ErrorCodes.INVALID_TOKEN
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.failure
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.success
import org.springframework.security.oauth2.jwt.Jwt

class JwtAudienceValidator(private val audiences: List<String>) : OAuth2TokenValidator<Jwt> {
    override fun validate(jwt: Jwt): OAuth2TokenValidatorResult {
        val hasAudience = audiences.any { it in jwt.audience }

        if (hasAudience) {
            return success()
        }

        val error = OAuth2Error(INVALID_TOKEN)

        return failure(error)
    }
}
