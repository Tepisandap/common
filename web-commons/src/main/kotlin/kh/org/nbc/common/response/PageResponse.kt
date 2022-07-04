package kh.org.nbc.common.response

data class PageResponse<T>(val content: List<T>, val pagination: Pagination)

data class Pagination(val currentPage: Int, val pageSize: Int, val totalElements: Long, val totalPages: Int)