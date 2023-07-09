package com.leijendary.spring.template.apigateway

import com.leijendary.spring.template.apigateway.container.JaegerContainerTest
import com.leijendary.spring.template.apigateway.container.RedisContainerTest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ContextConfiguration(
    initializers = [
        JaegerContainerTest.Initializer::class,
        RedisContainerTest.Initializer::class,
    ]
)
@AutoConfigureMockMvc
class ApplicationTests {
    @Test
    fun contextLoads() {
    }
}
