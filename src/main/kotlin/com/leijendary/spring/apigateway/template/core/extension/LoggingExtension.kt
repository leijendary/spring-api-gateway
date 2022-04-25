package com.leijendary.spring.apigateway.template.core.extension

import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger

inline fun <reified T> T.logger(): Logger {
    return getLogger(T::class.java)
}