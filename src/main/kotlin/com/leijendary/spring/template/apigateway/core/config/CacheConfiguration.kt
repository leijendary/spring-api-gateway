package com.leijendary.spring.template.apigateway.core.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.redis.core.convert.RedisCustomConversions

@Configuration
@EnableCaching
class CacheConfiguration {
    @Bean
    fun redisCustomConversions(converters: List<Converter<*, *>>): RedisCustomConversions {
        return RedisCustomConversions(converters)
    }
}
