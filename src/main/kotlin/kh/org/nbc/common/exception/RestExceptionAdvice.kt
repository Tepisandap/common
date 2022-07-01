package kh.org.nbc.common.exception

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import feign.FeignException
import kh.org.nbc.common.common.Constants.BASE_PACKAGE
import kh.org.nbc.common.exception.ErrorCode.*
import kh.org.nbc.common.helper.ct
import kh.org.nbc.common.response.ResponseWrapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeanInstantiationException
import org.springframework.data.mapping.PropertyReferenceException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.net.SocketTimeoutException
import javax.servlet.http.HttpServletRequest

@ControllerAdvice
class RestExceptionAdvice : ResponseEntityExceptionHandler() {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(GeneralErrorException::class)
    fun handleGeneralErrorException(
        request: HttpServletRequest,
        ex: GeneralErrorException
    ): ResponseEntity<ResponseWrapper> {
        log.error(request.servletPath, ex.message)
        val response = ResponseWrapper.error(ex.errorCode.code(), ex.localizedMessage, ex.data)
        GeneralErrorException(ex.errorCode).data(ex.data)
        val errorDetail = ex.stackTrace.filter { it.className.contains(BASE_PACKAGE) }.joinToString { it.toString() }
        request.setAttribute("errorDetail", errorDetail)
        val httpStatus = when (ex.errorCode.code()) {
            INTERNAL_ERROR.code() -> HttpStatus.INTERNAL_SERVER_ERROR
            INVALID_TOKEN.code(), TOKEN_EXPIRED.code(), UNAUTHORIZED_ACCESS.code() -> HttpStatus.UNAUTHORIZED
            else -> HttpStatus.BAD_REQUEST
        }
        return ResponseEntity.status(httpStatus).body(response)
    }

    @ExceptionHandler(InsufficientAuthenticationException::class)
    fun handleInsufficientAuthenticationException(ex: InsufficientAuthenticationException): ResponseEntity<ResponseWrapper> {
        val response = ResponseWrapper.error(UNAUTHORIZED_ACCESS, ex.message ?: "")
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response)
    }

    override fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException, headers: HttpHeaders, status: HttpStatus, request: WebRequest
    ): ResponseEntity<Any> {
        log.error("MissingServletRequestParameterException", ex)
        val response = ResponseWrapper.error(
            error = MISSING_REQUIRED_FILTERING_PARAM,
            message = ex.parameterName
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    override fun handleBindException(
        ex: BindException, headers: HttpHeaders, status: HttpStatus, request: WebRequest
    ): ResponseEntity<Any> {
        log.error("Requested uri with exception: {}", (request as ServletWebRequest).request.requestURI)
        val fieldName = ex.fieldError?.field
        val rejectedValue = ex.fieldError?.rejectedValue
        val response = ResponseWrapper.error(BAD_REQUEST, "$fieldName[$rejectedValue]")
        return ResponseEntity.badRequest().body(response)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException, headers: HttpHeaders, status: HttpStatus, request: WebRequest
    ): ResponseEntity<Any> {
        log.error("Requested uri with exception: {}", (request as ServletWebRequest).request.requestURI)
        val fieldError = ex.bindingResult.fieldError
        val fieldName = fieldError?.field ?: ""
        val message = "$fieldName ${fieldError?.defaultMessage}"
        val response = ResponseWrapper.error(BAD_REQUEST, message)
        return ResponseEntity.badRequest().body(response)
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException, headers: HttpHeaders, status: HttpStatus, request: WebRequest
    ): ResponseEntity<Any> {
        log.error("Requested uri with exception: {}", (request as ServletWebRequest).request.requestURI)
        val response: ResponseWrapper = when (val rootCause = ex.rootCause) {
            is GeneralErrorException -> {
                ResponseWrapper.error(rootCause.errorCode.code(), rootCause.description!!)
            }
            is MismatchedInputException -> {
                val message = if (rootCause.path.size > 0) "${rootCause.path.last().fieldName} is required" else "invalid"
                ResponseWrapper.error(BAD_REQUEST, message)
            }
            else -> ResponseWrapper.error(BAD_REQUEST, handleException(request.request, rootCause, rootCause?.message))
        }
        return ResponseEntity.badRequest().body(response)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        request: HttpServletRequest,
        ex: IllegalArgumentException
    ): ResponseEntity<ResponseWrapper> {
        val response =
            ResponseWrapper.error(BAD_REQUEST, handleException(request, ex.cause ?: ex, (ex.cause ?: ex).message))
        return ResponseEntity.badRequest().body(response)
    }

    @ExceptionHandler(NotImplementedError::class)
    fun handleNotImplementedErrorException(
        request: HttpServletRequest,
        ex: NotImplementedError
    ): ResponseEntity<ResponseWrapper> {
        val response = ResponseWrapper.error(BAD_REQUEST, handleException(request, ex, ex.message))
        return ResponseEntity.badRequest().body(response)
    }

    @ExceptionHandler(PropertyReferenceException::class)
    fun handlePropertyReferenceException(ex: PropertyReferenceException): ResponseEntity<ResponseWrapper> {
        log.error("PropertyReferenceException", ex)
        val response = ResponseWrapper.error(UNRECOGNIZED_FIELD, ex.propertyName)
        return ResponseEntity.badRequest().body(response)
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException::class)
    fun handleAccessDenied(request: HttpServletRequest, ex: Exception): ResponseEntity<Any> {
        val response = ResponseWrapper.error(UNAUTHORIZED_ACCESS, "functionality")
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response)
    }

    @ExceptionHandler(BeanInstantiationException::class)
    fun handleBeanInstantiationException(
        request: HttpServletRequest,
        ex: BeanInstantiationException
    ): ResponseEntity<ResponseWrapper> {
        val response = ResponseWrapper.error(INTERNAL_ERROR, handleException(request, ex.cause, ex.cause?.message))
        return ResponseEntity.badRequest().body(response)
    }

    @ExceptionHandler(FeignException::class)
    fun handleFeignStatusException(e: FeignException): ResponseEntity<ResponseWrapper> {
        return when (e) {
            is SocketTimeoutException -> ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build()
            is FeignException.FeignClientException -> {
                val responseWrapper = e.contentUTF8().ct(ResponseWrapper::class.java)
                ResponseEntity.badRequest().body(responseWrapper)
            }
            else -> ResponseEntity.status(HttpStatus.BAD_GATEWAY).build()
        }
    }

    @ExceptionHandler(Exception::class)
    fun handleException(request: HttpServletRequest, ex: Exception): ResponseEntity<Any> {
        val message: String = handleException(request, ex, ex.cause?.message)
        val response = ResponseWrapper.error(INTERNAL_ERROR, message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }

    private fun handleException(request: HttpServletRequest, ex: Throwable?, errorMessage: String?): String {
        log.error(request.servletPath, ex)
        log.error("Requested uri with exception: {}", request.requestURI)
        log.error("error message: {}", errorMessage)
        val errorDetail = ex?.stackTrace?.filter { it.className.contains(BASE_PACKAGE) }?.joinToString { it.toString() }
        request.setAttribute("errorDetail", errorDetail)
        return when (ex) {
            is MissingKotlinParameterException -> "${ex.parameter.name} is missing"
            is NotImplementedError -> "An operation is not yet implemented"
            else -> errorMessage ?: "Invalid format"
        }
    }
}