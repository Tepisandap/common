package kh.org.nbc.common.common

import java.util.*

object ApplicationPropInfo {

    enum class CommonLib(private val key: String, private val projectName: String) {
        COMMON("web-common-version", "Common Library");

        companion object {
            fun build(properties: Properties): MutableList<String> {
                val results = mutableListOf<String>()
                properties.map { prop ->
                    val commonLibVersionProp = enumValues<CommonLib>().firstOrNull { commonLibVersion ->
                        prop.key.toString().lowercase() == commonLibVersion.key.lowercase()
                    } ?: return@map
                    val str = StringBuilder(commonLibVersionProp.projectName)
                        .append(": ")
                        .append(prop.value)
                        .toString()
                    results.add(str)
                }
                return results
            }
        }
    }
}