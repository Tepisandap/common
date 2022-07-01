package kh.org.nbc.common.helper

import org.springframework.data.jpa.domain.Specification
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object SpecHelper {

    fun <T> getDateFromToSpec(dateFrom: LocalDateTime, dateTo: LocalDateTime, fieldName: String): Specification<T> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.between(root.get(fieldName), dateFrom, dateTo)
        }
    }

    fun <T> genFilterDateFromToSpec(dateFrom: LocalDate?, dateTo: LocalDate?, dateFieldName: String
    ): Specification<T>? {
        val date = validateDateFromDateTo(dateFrom, dateTo)
        return date?.let { getDateFromToSpec(date[0], date[1], dateFieldName) }
    }

    fun validateDateFromDateTo(dateFrom: LocalDate?, dateTo: LocalDate?): List<LocalDateTime>? {
        val dateTimeFrom: LocalDateTime
        val dateTimeTo: LocalDateTime

        when {
            dateFrom != null && dateTo == null -> {
                dateTimeFrom = dateFrom.atStartOfDay()
                dateTimeTo = dateFrom.atTime(LocalTime.MAX)
            }
            dateFrom == null && dateTo != null -> {
                dateTimeFrom = dateTo.atStartOfDay()
                dateTimeTo = dateTo.atTime(LocalTime.MAX)
            }
            dateFrom != null && dateTo != null -> {
                if (dateFrom.isAfter(dateTo)) err("Invalid DateFrom and DateTo")
                dateTimeFrom = dateFrom.atStartOfDay()
                dateTimeTo = dateTo.atTime(LocalTime.MAX)
            }
            else -> return null
        }
        return listOf(dateTimeFrom, dateTimeTo)
    }

    fun <T> genFilterMinMaxSpec(
        fieldName: String,
        min: Long?,
        max: Long?,
    ): Specification<T> {
        validateMinMaxSpec(min, max)
        return Specification { root, _, cb ->
            when {
                min != null && max == null -> cb.greaterThanOrEqualTo(root.get(fieldName), min)
                min == null && max != null -> cb.lessThanOrEqualTo(root.get(fieldName), max)
                min != null && max != null -> cb.between(root.get(fieldName), min, max)
                else -> null
            }
        }
    }

    fun validateMinMaxSpec(min: Long?, max: Long?): List<Long?> {
        if (min != null && max != null)
            if (min > max) err("Invalid min and max range")
        return listOf(min, max)
    }

    fun <T> genFilterMinMaxSpec(
        fieldName: String,
        min: BigDecimal?,
        max: BigDecimal?,
    ): Specification<T> {
        validateMinMaxSpec(min, max)
        return Specification { root, _, cb ->
            when {
                min != null && max == null -> cb.greaterThanOrEqualTo(root.get(fieldName), min)
                min == null && max != null -> cb.lessThanOrEqualTo(root.get(fieldName), max)
                min != null && max != null -> cb.between(root.get(fieldName), min, max)
                else -> null
            }
        }
    }

    fun validateMinMaxSpec(min: BigDecimal?, max: BigDecimal?): List<BigDecimal?> {
        if (min != null && max != null)
            if (min > max) err("Invalid min and max range")
        return listOf(min, max)
    }

    fun <T> genFilterMinMaxSpec(
        fieldName: String,
        min: Double?,
        max: Double?,
    ): Specification<T> {
        validateMinMaxSpec(min, max)
        return Specification { root, _, cb ->
            when {
                min != null && max == null -> cb.greaterThanOrEqualTo(root.get(fieldName), min)
                min == null && max != null -> cb.lessThanOrEqualTo(root.get(fieldName), max)
                min != null && max != null -> cb.between(root.get(fieldName), min, max)
                else -> null
            }
        }
    }

    fun validateMinMaxSpec(min: Double?, max: Double?): List<Double?> {
        if (min != null && max != null)
            if (min > max) err("Invalid min and max range")
        return listOf(min, max)
    }

    fun getPreQueryValue(value: String): String {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_")
    }
}
