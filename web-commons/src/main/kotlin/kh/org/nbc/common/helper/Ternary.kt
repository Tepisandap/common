package kh.org.nbc.common.helper

data class Ternary<T>(val target: T?, val result: Boolean)

infix fun <T> Boolean.then(target: T?): Ternary<T> {
    return Ternary(target, this)
}

infix fun <T> Ternary<T>.or(target: T?): T? {
    return if (this.result) this.target else target
}