package kh.org.nbc.common.response

import java.time.LocalDate
import java.time.temporal.ChronoUnit

class Interval(start: LocalDate, end: LocalDate) {
    var start: LocalDate = start
        set(value) {
            range = this.start.until(this.end, ChronoUnit.DAYS).toInt()
            field = value
        }
    var end: LocalDate = end
        set(value) {
            range = this.start.until(this.end, ChronoUnit.DAYS).toInt()
            field = value
        }
    var range: Int
    init {
        range = this.start.until(this.end, ChronoUnit.DAYS).toInt()
    }
}