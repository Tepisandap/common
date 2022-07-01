package kh.org.nbc.common.util

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Configuration

@Configuration
open class AppContext: ApplicationContextAware {
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        AppContext.setContext(applicationContext)
    }
    companion object {
        private lateinit var ctx: ApplicationContext
        fun setContext(applicationContext: ApplicationContext) {
            ctx = applicationContext
        }
        fun getContext(): ApplicationContext = ctx

        fun <T> getBean(type: Class<T>) = ctx.getBean(type)
    }

}