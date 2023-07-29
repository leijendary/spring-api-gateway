package com.leijendary.spring.template.apigateway.core.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
@ReadingConverter
class BytesToOffsetDateTimeConverter : Converter<ByteArray, OffsetDateTime> {
    override fun convert(source: ByteArray): OffsetDateTime {
        val value = String(source)

        return OffsetDateTime.parse(value)
    }
}
