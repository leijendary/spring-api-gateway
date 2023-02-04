package com.leijendary.spring.apigateway.template.core.extension

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.leijendary.spring.apigateway.template.core.util.SpringContext.Companion.getBean

object AnyExtension {
    private val log = logger()
    private val mapper = getBean(ObjectMapper::class)

    fun Any.toJson(): String? {
        try {
            return mapper.writeValueAsString(this)
        } catch (e: JsonProcessingException) {
            log.warn("Failed to parse object to json", e)
        }

        return null
    }
}
