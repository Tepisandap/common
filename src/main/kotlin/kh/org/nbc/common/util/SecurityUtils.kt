package kh.org.nbc.common.util

import kh.org.nbc.common.helper.ct
import kh.org.nbc.common.helper.isJson
import kh.org.nbc.common.response.UserAuth
import org.springframework.security.core.context.SecurityContextHolder

class SecurityUtils {
    companion object {
        fun getCurrentUserLogin(): UserAuth? {
            val securityContext = SecurityContextHolder.getContext()
            return securityContext?.authentication?.principal?.let {
                if (it == "anonymousUser" || !it.isJson()) null
                else it.ct(UserAuth::class.java)
            }
        }

        fun getCurrentUserJWT(): String? {
            val securityContext = SecurityContextHolder.getContext()
            return securityContext?.authentication?.takeIf { it.credentials is String }?.let { it.credentials as String }
        }

        fun getCurrentUserAuthority(): List<String> {
            return SecurityContextHolder.getContext()
                .authentication
                .authorities
                .map { it.authority }
        }
    }
}