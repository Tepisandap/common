package kh.org.nbc.common.config

import kh.org.nbc.common.common.Constants
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = true)
data class DateTimeFormatProperties(
    val format: Format = Format()
) {
    data class Format(
        var dateTime: String = Constants.DATETIME_FORMAT,
        var date: String = Constants.DATE_FORMAT,
        var time: String = Constants.TIME_FORMAT
    )
}
