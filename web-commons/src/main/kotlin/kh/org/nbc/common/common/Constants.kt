package kh.org.nbc.common.common

import com.fasterxml.jackson.databind.ObjectMapper
import kh.org.nbc.common.util.AppContext

object Constants {
    /** yyyy-MM-dd HH:mm:ss */
    const val ISO_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
    /** dd/MM/yyyy HH:mm:ss */
    const val DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss"
    /** dd/MM/yyyy */
    const val DATE_FORMAT = "dd/MM/yyyy"
    /** HH:mm */
    const val TIME_FORMAT = "HH:mm"
    /** KH */
    const val DEFAULT_PHONE_REGION = "KH"
    /** kh.org.nbc */
    const val BASE_PACKAGE = "kh.org.nbc"

    val objectMapper: ObjectMapper = AppContext.getContext().getBean(ObjectMapper::class.java)
}