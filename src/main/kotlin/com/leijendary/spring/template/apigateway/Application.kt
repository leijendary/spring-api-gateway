package com.leijendary.spring.template.apigateway

import org.springframework.boot.SpringBootVersion
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.core.env.get

@SpringBootApplication(
    exclude = [
        ErrorMvcAutoConfiguration::class,
        ReactiveUserDetailsServiceAutoConfiguration::class,
    ]
)
class ApiGatewayApplication

fun main(args: Array<String>) {
    runApplication<ApiGatewayApplication>(*args) {
        setBanner { environment, _, out ->
            val name = environment["info.app.name"]
            val version = environment["info.app.version"]
            val springVersion = SpringBootVersion.getVersion()

            out.print("Running $name v$version on Spring Boot v$springVersion")
        }
    }
}
