package kh.org.nbc.common.helper

import java.util.*


fun Boolean.existsAndThrow(msg: String): Boolean {
    return this.then { notExisted(msg) } or this
}

fun <I> existsAndThrow(field: String, paramI: I, loader: (I) -> Boolean): Boolean {
    return loader(paramI).existsAndThrow("$field[$paramI]")
}

fun <I, J> existsAndThrow(field: String, paramI: I, paramJ: J, loader: (I, J) -> Boolean): Boolean {
    return loader(paramI, paramJ).existsAndThrow("$field[$paramI]")
}

fun <I, J, K> existsAndThrow(field: String, paramI: I, paramJ: J, paramK: K, loader: (I, J, K) -> Boolean): Boolean {
    return loader(paramI, paramJ, paramK).existsAndThrow("$field[$paramI]")
}

fun <I, J, K, L> existsAndThrow(field: String, paramI: I, paramJ: J, paramK: K, paramL: L, loader: (I, J, K, L) -> Boolean): Boolean {
    return loader(paramI, paramJ, paramK, paramL).existsAndThrow("$field[$paramI]")
}

fun <I, J, K, L, M> existsAndThrow(field: String, paramI: I, paramJ: J, paramK: K, paramL: L, paramM: M, loader:(I, J, K, L, M) -> Boolean): Boolean {
    return loader(paramI, paramJ, paramK, paramL, paramM).existsAndThrow("$field[$paramI]")
}

fun <I, J, K, L, M, N> existsAndThrow(field: String, paramI: I, paramJ: J, paramK: K, paramL: L, paramM: M, paramN: N, loader: (I, J, K, L, M, N) -> Boolean): Boolean {
    return loader(paramI, paramJ, paramK, paramL, paramM, paramN).existsAndThrow("$field[$paramI]")
}
