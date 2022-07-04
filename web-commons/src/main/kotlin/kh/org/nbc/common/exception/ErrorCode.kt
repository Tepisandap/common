package kh.org.nbc.common.exception

enum class ErrorCode(private val code: String, private val message: String): Error {
    SUCCESSFUL("0", ""),
    OBJECT_NOT_FOUND("1", "%s is not found"),
    INVALID_REQUEST_FORMAT("2", "%s is invalid"),
    RECORD_ALREADY_EXIST("3", "%s is already existed"),
    INTERNAL_ERROR("4", "Internal server error, %s"),
    BAD_REQUEST("5", "%s"),
    UNRECOGNIZED_FIELD("6", "Unrecognized field: %s"),
    MISSING_REQUIRED_FILTERING_PARAM("7", "Missing query params: %s"),
    JSON_PARSING_ERROR("8", "Json parsing error value: %s"),
    FIELD_REQUIRED("9", "%s is required"),
    INVALID_TOKEN("13", "Invalid token"),
    TOKEN_EXPIRED("14", "Token expired"),
    UNAUTHORIZED_ACCESS("15", "Unauthorized access %s"),
    REQUEST_TIMEOUT("16", "Request timeout"),
    CHANGE_PASSWORD("33", "%s");

    override fun code(): String = this.code

    override fun message(): String = this.message
}
