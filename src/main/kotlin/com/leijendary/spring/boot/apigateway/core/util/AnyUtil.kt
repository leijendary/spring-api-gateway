package com.leijendary.spring.boot.apigateway.core.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.leijendary.spring.boot.apigateway.core.util.SpringContext.Companion.getBean

object AnyUtil {
    private val log = logger()
    private val mapper: ObjectMapper = getBean(ObjectMapper::class.java)

    fun Any.toJson(): String? {
        try {
            return mapper.writeValueAsString(this)
        } catch (e: JsonProcessingException) {
            log.warn("Failed to parse object to json", e)
        }

        return null
    }
}