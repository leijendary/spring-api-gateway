package com.leijendary.spring.boot.apigateway.core.filter

import com.leijendary.spring.boot.apigateway.core.decorator.StandardResponseDecorator
import com.leijendary.spring.boot.apigateway.core.filter.StandardResponseGatewayFilterFactory.Config
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory
import org.springframework.stereotype.Component

@Component
class StandardResponseGatewayFilterFactory : GatewayFilterFactory<Config> {
    companion object {
        const val ORDER = WRITE_RESPONSE_FILTER_ORDER - 1
    }

    inner class Config {
        var responseType: Class<*>? = null
    }

    override fun apply(config: Config): GatewayFilter = OrderedGatewayFilter({ exchange, chain ->
        val request = exchange.request
        val response = exchange.response
        val decorator = StandardResponseDecorator(request, response, config.responseType)

        chain.filter(exchange.mutate().response(decorator).build())
    }, ORDER)

    override fun getConfigClass(): Class<Config> = Config::class.java

    override fun newConfig(): Config = Config()
}