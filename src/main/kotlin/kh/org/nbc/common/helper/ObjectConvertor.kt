package kh.org.nbc.common.helper

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import kh.org.nbc.common.common.Constants.objectMapper
import kh.org.nbc.common.exception.ErrorCode
import kh.org.nbc.common.exception.ErrorCode.INTERNAL_ERROR
import kh.org.nbc.common.exception.ErrorCode.JSON_PARSING_ERROR
import kh.org.nbc.common.helper.ObjectConvertor.Companion.log
import kh.org.nbc.common.response.PageResponse
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page

class ObjectConvertor {
    companion object {
        val log: Logger = LoggerFactory.getLogger(ObjectConvertor::class.java)
    }
}

fun <K> Iterable<*>.ct(type: Class<K>): List<K> = this.filterNotNull().map { it.ct(type) }

fun <K> Collection<*>.ct(type: Class<K>): List<K> = this.filterNotNull().map { it.ct(type) }

fun <K> Page<*>.ct(type: Class<K>): PageResponse<K> = this.map { it.ct(type) }.toPageResponse()

fun <K> Any.ct(type: Class<K>): K {
    try {
        if (this is String && !type.typeName.equals(JSONObject::class.qualifiedName)) {
            if (this.isBlank()) return objectMapper.convertValue(emptyMap<String, String>(), type)
            return objectMapper.convertValue(this, JSONObject::class.java).toMap().ct(type)
        }
        return objectMapper.convertValue(this, type)
    } catch (ex: IllegalArgumentException) {
        if (ex.cause is MissingKotlinParameterException) {
            log.error("MissingKotlinParameterException: {}", ex.cause)
            (ex.cause as MissingKotlinParameterException).path.map {
                it.description.split(".").last()
            }.let {
                JSON_PARSING_ERROR.issue(it.first())
            }
        }
        err(INTERNAL_ERROR, "Can not deserialize value")
    }
}

fun <K : Any> Any.ct(type: Class<K>, safeMode: Boolean): Any {
    if (safeMode && !this.isJson()) {
        log.debug("\"${this.asString()}\" can't cover to type ${type.typeName}" )
        return "".asString()
    } else if (safeMode && this.isJson()) {
        try {
            ct(type)
        } catch (ex: Exception) {
            return this.asString()
        }
    }
    return ct(type)
}

fun Any?.asString(): String {
    if (this is String) return this
    return this?.let { objectMapper.writeValueAsString(it) } ?: ""
}
