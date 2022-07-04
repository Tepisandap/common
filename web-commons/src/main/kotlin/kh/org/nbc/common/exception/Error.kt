package kh.org.nbc.common.exception

interface Error {
    fun code(): String
    fun message(): String
    fun issue(): Nothing {
        throw GeneralErrorException(this)
    }
    fun issue(vararg arg: Any): Nothing {
        throw GeneralErrorException(this, *arg)
    }
    fun init(): GeneralErrorException {
        return GeneralErrorException(this)
    }
    fun init(vararg arg: Any): GeneralErrorException {
        return GeneralErrorException(this, *arg)
    }
}