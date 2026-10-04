package com.learn.faulttolerancepatterns.resilience4j

import com.learn.faulttolerancepatterns.client.HttpClient
import com.learn.faulttolerancepatterns.client.TransientServiceException
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import io.github.resilience4j.retry.RetryRegistry
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.function.Supplier

@Component
class Resilience4jRetry (
    private val httpClient: HttpClient
) {

    private val logger = KotlinLogging.logger {}

    lateinit var retry: Retry

    lateinit var retryableSupplier: Supplier<String>

    @PostConstruct
    fun init() {
        configRetry()
        retryableSupplier = Retry.decorateSupplier(retry) {
            httpClient.httpGetRequest("http://localhost:9090/api/test/transient/error")
        }
    }

    fun configRetry() {
        val retryConfig = RetryConfig.custom<String>()
            .maxAttempts(3) // 1 initial call + 2 retries
            .waitDuration(java.time.Duration.ofMillis(500))
            .retryExceptions(
                TransientServiceException::class.java,
                IOException::class.java
            )
            .build()

        val retryRegistry = RetryRegistry.of(retryConfig)
        retry = retryRegistry.retry("myRetry", retryConfig)
        retry.eventPublisher.onRetry { event ->
            logger.info { "Retrying after failure. attempt=${event.numberOfRetryAttempts}, wait=${event.waitInterval}" }
        }
    }

    fun testRetryForTransientError(): String {
        var response: String = ""
        try {
            response = retryableSupplier.get()
            println("Result: $response")
        } catch (e: Exception) {
            response = "error"
            println("Error occurred: ${e.message}")
        }
        return response
    }
}