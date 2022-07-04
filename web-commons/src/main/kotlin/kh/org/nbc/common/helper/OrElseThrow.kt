package kh.org.nbc.common.helper

import java.util.*

fun <T> Optional<T>.orElseThrow(msg: String): T = this.orElseThrow { idNotFound(msg) }

fun <T> T?.orElseThrow(msg: String): T = this ?: idNotFound(msg)

fun <T, I> getOrElseThrow(field: String, paramI: I, loader: (I) -> Optional<T>): T {
    return loader(paramI).orElseThrow { idNotFound("$field[$paramI]") }
}

fun <T, I, J> getOrElseThrow(field: String, paramI: I, paramJ: J, loader: (I, J) -> Optional<T>): T {
    return loader(paramI, paramJ).orElseThrow { idNotFound("$field[$paramI]") }
}

fun <T, I, J, K> getOrElseThrow(field: String, paramI: I, paramJ: J, paramK: K, loader: (I, J, K) -> Optional<T>): T {
    return loader(paramI, paramJ, paramK).orElseThrow { idNotFound("$field[$paramI]") }
}

fun <T, I, J, K, L> getOrElseThrow(field: String, paramI: I, paramJ: J, paramK: K, paramL: L, loader: (I, J, K, L) -> Optional<T>): T {
    return loader(paramI, paramJ, paramK, paramL).orElseThrow { idNotFound("$field[$paramI]") }
}

fun <T, I, J, K, L, M> getOrElseThrow(field: String, paramI: I, paramJ: J, paramK: K, paramL: L, paramM: M, loader:(I, J, K, L, M) -> Optional<T>): T {
    return loader(paramI, paramJ, paramK, paramL, paramM).orElseThrow { idNotFound("$field[$paramI]") }
}

fun <T, I, J, K, L, M, N> getOrElseThrow(field: String, paramI: I, paramJ: J, paramK: K, paramL: L, paramM: M, paramN: N, loader: (I, J, K, L, M, N) -> Optional<T>): T {
    return loader(paramI, paramJ, paramK, paramL, paramM, paramN).orElseThrow { idNotFound("$field[$paramI]") }
}



