package kh.org.nbc.common.config

data class LogObject(
	val requestId: String,
	val appName: String = "",
	val appVersion: String = "",
	val action: String, // pre, post
	val step: String, // request, response
	val name: String = """${if (action != "") action + "_" else ""}$step#$requestId""",
	val requestMethod: String, // POST, GET, PUT
	val requestURI: String, // /api/v1/car
	val clientHost: String, // 0.0.0.0
	val httpStatus: Any = "", // 200, 400
	val user: Any = "",
	val time: String = "", // $timeElapsed ms
	val request: String = "",
	val response: String = "",
	val exception: String = ""
)