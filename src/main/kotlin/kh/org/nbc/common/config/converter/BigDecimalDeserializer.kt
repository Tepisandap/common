package kh.org.nbc.common.config.converter

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.math.BigDecimal
import java.text.DecimalFormat

class BigDecimalDeserializer: StdDeserializer<BigDecimal>(BigDecimal::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): BigDecimal {
        val decimalFormat = DecimalFormat("#,###,###.##")
        return decimalFormat.parse(p.text).toDouble().toBigDecimal()
    }
}