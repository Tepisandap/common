//@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
//
//package kh.org.nbc.common.config
//
//import kh.org.nbc.common.common.ApplicationPropInfo
//import kh.org.nbc.common.helper.asString
//import kh.org.nbc.common.helper.contains
//import kh.org.nbc.common.helper.hideKey
//import kh.org.nbc.common.helper.letReturn
//import kh.org.nbc.common.util.SecurityUtils
//import org.apache.commons.lang3.RandomStringUtils
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.actuate.trace.http.HttpExchangeTracer
//import org.springframework.boot.actuate.trace.http.HttpTraceRepository
//import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository
//import org.springframework.boot.actuate.web.trace.servlet.HttpTraceFilter
//import org.springframework.boot.info.BuildProperties
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.core.io.FileSystemResource
//import org.springframework.stereotype.Component
//import org.springframework.web.util.ContentCachingRequestWrapper
//import org.springframework.web.util.ContentCachingResponseWrapper
//import java.time.Duration
//import java.time.Instant
//import java.util.*
//import java.util.stream.Collectors
//import javax.annotation.PostConstruct
//import javax.servlet.FilterChain
//import javax.servlet.http.HttpServletRequest
//import javax.servlet.http.HttpServletResponse
//
//@Configuration
//open class LogConfiguration {
//	private val log: Logger = LoggerFactory.getLogger(LogConfiguration::class.java)
//
//	@Bean
//	open fun httpTraceRepository(): HttpTraceRepository {
//		return InMemoryHttpTraceRepository()
//	}
//
//	@Component
//	open class TraceRequestFilter(repo: HttpTraceRepository, tracer: HttpExchangeTracer) : HttpTraceFilter(repo, tracer) {
//		private val log: Logger = LoggerFactory.getLogger(LogConfiguration::class.java)
//
//		@Autowired(required = false) lateinit var buildProperties: BuildProperties
//
//		fun checkLateInit(){
//			// checking whether the value is assigned or not
//			if(this::buildProperties.isInitialized)
//				println("Your value is not assigned");
//
//			else{
//				// initializing name
//				buildProperties.name
//				buildProperties.version
//				println(this.buildProperties)
//				// this will return true
//			}
//		}
//
//		override fun shouldNotFilter(request: HttpServletRequest): Boolean {
//			return request.servletPath.contains("/actuator", "/swagger-ui", "/api-docs", "/favicon.ico", "upload/temporary", "upload/terms-and-conditions")
//		}
//
//		override fun doFilterInternal(
//			request: HttpServletRequest,
//			httpServletResponse: HttpServletResponse,
//			filterChain: FilterChain
//		) {
//			val requestWrapper = ContentCachingRequestWrapper(request)
//			val responseWrapper = ContentCachingResponseWrapper(httpServletResponse)
//			val user = SecurityUtils.getCurrentUserLogin()
//			val shortId: String = RandomStringUtils.randomAlphanumeric(10)
//			request.setAttribute("shortId", shortId)
//
//			val queryString = requestWrapper.parameterMap.entries.stream()
//				.map{ e->e.key + "=" + e.value.first() }
//				.collect(Collectors.joining("&"))
//			val requestURI = if (queryString.isNotEmpty()) {
//				requestWrapper.requestURI.plus("?").plus(queryString)
//			} else requestWrapper.requestURI
//
//			checkLateInit()
//
//			val preRequest = LogObject(
//				requestId = shortId,
//				appName = buildProperties.name,
//				appVersion = buildProperties.version,
//				action = "pre",
//				step = "request",
//				requestMethod = requestWrapper.method,
//				requestURI = requestURI,
//				clientHost = requestWrapper.remoteHost,
//				user = user.asString()
//			)
//
//			log.info(preRequest.asString())
//
//			val start: Instant = Instant.now()
//
//			filterChain.doFilter(requestWrapper, responseWrapper)
//
//			val finish: Instant = Instant.now()
//			val timeElapsed: Long = Duration.between(start, finish).toMillis()
//
//			val requestArray = requestWrapper.contentAsByteArray
//			val requestStr = String(requestArray, Charsets.UTF_8).hideKey("password")
//
//			checkLateInit()
//
//			val postRequest = LogObject(
//				requestId = shortId,
//				appName = buildProperties.name,
//				appVersion = buildProperties.version,
//				action = "post",
//				step = "request",
//				requestMethod = requestWrapper.method,
//				requestURI = requestURI,
//				clientHost = requestWrapper.remoteHost,
//				user = user.asString(),
//				request = requestStr
//			)
//			log.info(postRequest.asString())
//
//			val responseArray = responseWrapper.contentAsByteArray
//			val responseStr = String(responseArray, Charsets.UTF_8)
//			val exception = (request.getAttribute("errorDetail") ?: "") as String
//
//			checkLateInit()
//
//			val response = LogObject(
//				requestId = shortId,
//				appName = buildProperties.name,
//				appVersion = buildProperties.version,
//				action = "",
//				step = "response",
//				requestMethod = requestWrapper.method,
//				requestURI = requestURI,
//				clientHost = requestWrapper.remoteHost,
//				httpStatus = httpServletResponse.status,
//				user = user.asString(),
//				exception = exception,
//				response = responseStr,
//				time = "$timeElapsed ms"
//			)
//			log.info(response.asString())
//
//			responseWrapper.copyBodyToResponse()
//		}
//	}
//
//	@PostConstruct
//	open fun logApplicationPropInfo() {
//		val prop = Properties()
//		val inputStream = FileSystemResource("").file.absoluteFile.listFiles().firstOrNull { it.name.equals("gradle.properties") }
//			?.inputStream() ?: return
//		prop.load(inputStream)
//		ApplicationPropInfo.CommonLib.build(prop).letReturn {
//			println("        :: Common libraries version ::        ")
//		}.map {
//			println(it)
//		}
//	}
//}
//
//
