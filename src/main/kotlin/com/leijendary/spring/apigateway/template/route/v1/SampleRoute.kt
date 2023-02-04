package com.leijendary.spring.apigateway.template.route.v1

import com.leijendary.spring.apigateway.template.core.extension.authenticated
import com.leijendary.spring.apigateway.template.core.extension.defaultFilters
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.RouteLocatorDsl
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.*

@Configuration
class SampleRoute {
    @Bean
    fun sampleRouteV1(builder: RouteLocatorBuilder): RouteLocator = RouteLocatorDsl(builder).apply {
        route("sample-list-v1") {
            uri("lb://sample")
            path("/api/v1/samples")
            method(GET)
            order(2)
            filters {
                defaultFilters {
                    authenticated("urn:sample:list:v1")
                }
            }
        }
        route("sample-create-v1") {
            uri("lb://sample")
            path("/api/v1/samples")
            method(POST)
            filters {
                defaultFilters {
                    authenticated("urn:sample:create:v1")
                }
            }
        }
        route("sample-get-v1") {
            uri("lb://sample")
            path("/api/v1/samples/{id}")
            method(GET)
            order(1)
            filters {
                defaultFilters {
                    authenticated("urn:sample:get:v1")
                }
            }
        }
        route("sample-update-v1") {
            uri("lb://sample")
            path("/api/v1/samples/{id}")
            method(PUT)
            order(1)
            filters {
                defaultFilters {
                    authenticated("urn:sample:update:v1")
                }
            }
        }
        route("sample-delete-v1") {
            uri("lb://sample")
            path("/api/v1/samples/{id}")
            method(DELETE)
            order(1)
            filters {
                defaultFilters {
                    authenticated("urn:sample:delete:v1")
                }
            }
        }
    }.build()
}
