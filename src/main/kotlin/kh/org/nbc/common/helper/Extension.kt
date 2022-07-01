package kh.org.nbc.common.helper

import com.fasterxml.jackson.databind.node.ObjectNode
import kh.org.nbc.common.common.Constants
import kh.org.nbc.common.common.Constants.objectMapper
import kh.org.nbc.common.exception.Error
import kh.org.nbc.common.exception.ErrorCode.*
import kh.org.nbc.common.exception.GeneralErrorException
import kh.org.nbc.common.response.PageResponse
import kh.org.nbc.common.response.Pagination
import kh.org.nbc.common.response.ResponseWrapper
import kh.org.nbc.common.response.UserNameRes
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mapping.PropertyPath
import org.springframework.http.ResponseEntity
import org.springframework.util.Assert
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoField
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.toMap as oriToMap

fun <T> Page<T>.toPageResponse(): PageResponse<T> {
    return PageResponse(
        content = this.content,
        pagination = Pagination(
            currentPage = this.pageable.pageNumber,
            pageSize = this.pageable.pageSize,
            totalElements = this.totalElements,
            totalPages = this.totalPages
        )
    )
}

fun ok(data: Any?) = ResponseEntity.ok(ResponseWrapper.data(data))

fun String.toSHA256Base64(): String {
    val byteHash = MessageDigest.getInstance("SHA-256").digest(this.toByteArray(StandardCharsets.UTF_8))
    return Base64.getEncoder().encodeToString(byteHash)
}

infix fun Boolean.then(action: () -> Any): Boolean {
    if (this)
        action.invoke()
    return this
}

infix fun Boolean.elze(action: () -> Any) {
    if (!this)
        action.invoke()
}

fun invalidArgument(field: String): Nothing = throw GeneralErrorException(INVALID_REQUEST_FORMAT, field)

fun idNotFound(msg: String): Nothing = throw GeneralErrorException(OBJECT_NOT_FOUND, msg)

fun notExisted(msg: String): Nothing = throw GeneralErrorException(RECORD_ALREADY_EXIST, msg)

fun unauthorized(msg: String? = ""): Nothing = throw GeneralErrorException(UNAUTHORIZED_ACCESS, msg ?: "")

fun err(error: Error, msg: Any = ""): Nothing = throw GeneralErrorException(error, msg)

fun err(error: Error, vararg arg: Any): Nothing = throw GeneralErrorException(error, *arg)

fun err(msg: Any): Nothing = throw GeneralErrorException(msg)

fun MutableMap<Long, UserNameRes?>.mapToString(): MutableMap<Long, String?> {
    return this.map { it.key to it.value?.fullName }.oriToMap().toMutableMap()
}

fun String.toMap(): MutableMap<String, Any> {
    return kotlin.runCatching { this.toJson().toMap() }
        .onFailure { invalidArgument("json format") }
        .getOrThrow()
}

fun Any.toMap(): MutableMap<String, Any> = this.asString().toMap()

fun String.toJsonArray(): JSONArray {
    if (!this.isJson()) err(JSON_PARSING_ERROR, this)
    try {
        return JSONArray(this)
    } catch (ex: JSONException) {
        err(JSON_PARSING_ERROR, this)
    }
}

fun String.toJson(): JSONObject {
    if (!this.isJson()) err(JSON_PARSING_ERROR, this)
    try {
        return JSONObject(this)
    } catch (ex: JSONException) {
        err(JSON_PARSING_ERROR, this)
    }
}

fun MutableMap<String, Any>.toJson(): JSONObject = JSONObject(this)

fun Any.toJson(): JSONObject = this.asString().toJson()

fun String.isJson(): Boolean {
    try {
        JSONObject(this)
    } catch (ex: JSONException) {
        try {
            JSONArray(this)
        } catch (ex: JSONException) {
            return false
        }
    }
    return true
}

fun String.contains(vararg other: String, ignoreCase: Boolean = false): Boolean {
    other.map {
        if (this.contains(it, ignoreCase)) return true
    }
    return false
}

fun <T> T?.notNull(name: String): T {
    Assert.notNull(this, "$name must not be null")
    return this!!
}

/**
 * Mainly used in Grpc service Backend
 * @since 0.3.32
 */
fun <T> T?.required(name: String): T {
    val bool = (this is String && this.isBlank()) || (this is Number && this.toDouble() == 0.0)
        || (this is BigDecimal && this == BigDecimal.ZERO) || (this == null)
    if (bool) FIELD_REQUIRED.issue(name)
    return this!!
}

fun Any.isJson(): Boolean = this.asString().isJson()

fun String.hideKey(key: String, format: String = "*****"): String {
    return try {
        if (!this.isJson()) return this
        val objectNode = objectMapper.readTree(this.asString())
        objectNode.findParents(key).map {
            (it as ObjectNode).put(key, format)
        }
        objectNode.asString()
    } catch (ex: Exception) {
        this
    }
}

fun String.removeKey(key: String): String {
    return try {
        if (!this.isJson()) return this
        val objectNode = Constants.objectMapper.readTree(this.asString())
        objectNode.findParents(key).map {
            (it as ObjectNode).remove(key)
        }
        objectNode.asString()
    } catch (ex: Exception) {
        this
    }
}

fun MutableMap<String, Any>.hideKey(key: String, format: String = "*****"): MutableMap<String, Any> {
    return this.asString().hideKey(key, format).toMap()
}

fun MutableMap<String, Any>.removeKey(key: String): MutableMap<String, Any> {
    return this.asString().removeKey(key).toMap()
}

fun <T> Pageable.checkingSortFields(type: Class<T>) {
    this.sort.toList().map { PropertyPath.from(it.property, type) }
}

fun LocalDate.settlementDateTime(
    tn: Long,
    cutOffTime: LocalTime?,
    holidayList: Set<LocalDate>?,
    isSettleNow: Boolean
): LocalDateTime {
    var date = this
    var settleDate: LocalDate
    val weekend = listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
    if (isSettleNow) {
        return date.atTime(LocalTime.now())
    } else {
        settleDate = if (tn == 0L && cutOffTime!!.isBefore(LocalTime.now())) date.plusDays(1) else date.plusDays(tn)
        while (date < settleDate) {
            date = date.plusDays(1)
            val day = DayOfWeek.of(date.get(ChronoField.DAY_OF_WEEK))
            if (holidayList?.contains(date) == true || day in weekend) settleDate = settleDate.plusDays(1)
        }
    }
    return settleDate.atTime(cutOffTime)
}

fun <E> MutableList<E>.letReturn(function: (MutableList<E>) -> Unit): MutableList<E> {
    this.let(function)
    return this
}

fun String.match(regex: String): Boolean {
    val regexPattern = Pattern.compile(regex)
    return regexPattern.matcher(this).matches()
}

fun <T> Iterable<T>.any(other: Iterable<T>): Boolean {
    if (this is Collection && isEmpty()) return false
    if (this.any { other.contains(it) }) return true
    return false
}

fun <T> Iterable<T>.getMatching(other: Iterable<T>): Iterable<T> {
    return this.filter { other.contains(it) }
}