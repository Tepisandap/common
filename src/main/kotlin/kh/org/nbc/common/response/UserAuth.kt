package kh.org.nbc.common.response

import com.fasterxml.jackson.annotation.JsonProperty

data class UserAuth(
    @JsonProperty("userId")
    val userId: Long,
    @JsonProperty("participantId")
    val participantId: Long,
    @JsonProperty("roleId")
    val roleId: Long,
    @JsonProperty("roleType")
    val roleType: String
)