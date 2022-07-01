package kh.org.nbc.common.config.converter

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.math.BigDecimal
import java.text.DecimalFormat

class BigDecimalSerializer: StdSerializer<BigDecimal>(BigDecimal::class.java) {
    override fun serialize(value: BigDecimal, gen: JsonGenerator, serializers: SerializerProvider) {
        val decimalFormat = DecimalFormat("#,###,###.##")
        gen.writeString(decimalFormat.format(value))
    }
}