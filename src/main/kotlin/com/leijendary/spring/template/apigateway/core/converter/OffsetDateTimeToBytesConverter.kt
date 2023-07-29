package com.leijendary.spring.template.apigateway.core.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
@WritingConverter
class OffsetDateTimeToBytesConverter : Converter<OffsetDateTime, ByteArray> {
    override fun convert(source: OffsetDateTime): ByteArray {
        return source.toString().encodeToByteArray()
    }
}
