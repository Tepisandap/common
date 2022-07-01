package kh.org.nbc.common.response

import kh.org.nbc.common.exception.ErrorCode
import kh.org.nbc.common.exception.Error
import kh.org.nbc.common.exception.GeneralErrorException
import kh.org.nbc.common.helper.asString
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder

data class ResponseWrapper(val status: Status, val data: Any?, var requestId: String = "") {
    companion object {
        fun data(data: Any?): ResponseWrapper {
            return ResponseWrapper(Status.SUCCESSFUL, data)
        }

        fun error(error: Error): ResponseWrapper {
            val status = Status(error.code(), error.message())
            return ResponseWrapper(status, null)
        }

        fun error(error: Error, message: String): ResponseWrapper {
            val status = Status(error.code(), String.format(error.message(), message).trim())
            return ResponseWrapper(status, null)
        }

        fun error(error: Error, vararg message: Any): ResponseWrapper {
            val status: Status = Status(error.code(), String.format(error.message(), *message).trim())
            return ResponseWrapper(status, null)
        }

        fun error(error: Error, message: String, data: Any? = null): ResponseWrapper {
            val status: Status = Status(error.code(), String.format(error.message(), message).trim())
            return ResponseWrapper(status, data)
        }

        fun error(code: String, message: String): ResponseWrapper {
            val status: Status = Status(code, message)
            return ResponseWrapper(status, null)
        }

        fun error(code: String, message: Any, data: Any? = null): ResponseWrapper {
            val status: Status = Status(code, message.toString())
            return ResponseWrapper(status, data)
        }

        fun error(ex: GeneralErrorException): ResponseWrapper {
            val status: Status = Status(ex.errorCode.code(), ex.description.asString())
            return ResponseWrapper(status, ex.data)
        }

    }

    fun initRequestId(): ResponseWrapper {
        val shortId: String = RandomStringUtils.randomAlphanumeric(10)
        val requestId = (RequestContextHolder.currentRequestAttributes().getAttribute("shortId", 0) ?: "") as String
        if (requestId.isBlank()){
            RequestContextHolder.currentRequestAttributes().setAttribute("shortId", shortId, RequestAttributes.SCOPE_REQUEST)
            this.requestId = shortId
        }
        return this
    }

    class Status(val errorCode: String, val errorMessage: String) {
        companion object {
            val SUCCESSFUL = Status(ErrorCode.SUCCESSFUL.code(), ErrorCode.SUCCESSFUL.message())
        }
    }

    init {
        requestId = (RequestContextHolder.currentRequestAttributes().getAttribute("shortId", 0) ?: "") as String
    }

}