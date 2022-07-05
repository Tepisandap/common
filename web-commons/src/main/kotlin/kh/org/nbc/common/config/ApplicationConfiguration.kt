package kh.org.nbc.common.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.hubspot.jackson.datatype.protobuf.ProtobufModule
import kh.org.nbc.common.common.Constants.OWNER_PACKAGE
import kh.org.nbc.common.exception.RestExceptionAdvice
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.*
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import th.co.geniustree.springdata.jpa.repository.support.JpaSpecificationExecutorWithProjectionImpl
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Configuration
@EnableJpaRepositories("$OWNER_PACKAGE.*", repositoryBaseClass = JpaSpecificationExecutorWithProjectionImpl::class)
@ConfigurationPropertiesScan(OWNER_PACKAGE)
@Import(LogConfiguration::class, RestExceptionAdvice::class)
@ComponentScan(basePackages = [OWNER_PACKAGE, "kh.org.nbc"])
open class ApplicationConfiguration(
    properties: DateTimeFormatProperties
) {
    private val log = LoggerFactory.getLogger(ApplicationConfiguration::class.java)

    private fun javaTimeModule(format: DateTimeFormatProperties.Format): SimpleModule {
        log.info("Registered Jackson Format -- DateTime: ${format.dateTime}")
        log.info("Registered Jackson Format -- Date: ${format.date}")
        log.info("Registered Jackson Format -- Time: ${format.time}")
        return JavaTimeModule()
            .addDeserializer(
                LocalDateTime::class.java,
                LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(format.dateTime))
            )
            .addSerializer(
                LocalDateTime::class.java,
                LocalDateTimeSerializer(DateTimeFormatter.ofPattern(format.dateTime))
            )
            .addDeserializer(LocalDate::class.java, LocalDateDeserializer(DateTimeFormatter.ofPattern(format.date)))
            .addSerializer(LocalDate::class.java, LocalDateSerializer(DateTimeFormatter.ofPattern(format.date)))
            .addDeserializer(LocalTime::class.java, LocalTimeDeserializer(DateTimeFormatter.ofPattern(format.time)))
            .addSerializer(LocalTime::class.java, LocalTimeSerializer(DateTimeFormatter.ofPattern(format.time)))
    }

    private val objectMapper: ObjectMapper = jacksonObjectMapper()
        .registerModule(Jdk8Module())
        .registerModule(javaTimeModule(properties.format))
        .registerModule(ParameterNamesModule())
        .registerModule(KotlinModule())
        .registerModule(ProtobufModule())
        .configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .setDefaultPropertyInclusion(JsonInclude.Include.ALWAYS)
        .setDateFormat(SimpleDateFormat(properties.format.dateTime))
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    @Bean
    @Primary
    open fun objectMapper(): ObjectMapper = objectMapper
}
