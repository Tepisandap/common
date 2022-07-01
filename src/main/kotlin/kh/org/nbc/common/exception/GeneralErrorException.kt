package kh.org.nbc.common.exception

import kh.org.nbc.common.exception.ErrorCode.BAD_REQUEST

open class GeneralErrorException(var errorCode: Error, vararg arg: Any) : RuntimeException() {

    override fun getLocalizedMessage(): String {
        return message
    }

    var data: Any? = null
    var description: String? = null

    fun data(data: Any?): GeneralErrorException {
        this.data = data
        return this
    }

    fun description(description: String?): GeneralErrorException {
        this.description = description
        return this
    }

    constructor(arg: Any) : this(BAD_REQUEST, arg)

    init {
        try {
            this.description = String.format(errorCode.message(), *arg).trim()
        } catch (ex: java.lang.Exception) {
            this.description = errorCode.message()
                .replace(",", "")
                .replace("%s", "")
                .trim()
        }
    }

    override val message: String
        get() = this.description.toString()


    fun fire(): Nothing {
        throw this
    }
}