package com.learn.faulttolerancepatterns.springresilience4j

import com.learn.faulttolerancepatterns.client.HttpClient
import io.github.resilience4j.retry.RetryRegistry
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.stereotype.Component

@Component
open class SpringResilience4jRetry (
    private val httpClient: HttpClient,
    private val retryRegistry: RetryRegistry
) {

    init {
        val retry = retryRegistry.retry("externalservice")
        retry.eventPublisher.onRetry { event ->
            println("Retrying after failure. attempt=${event.numberOfRetryAttempts}, wait=${event.waitInterval}")
        }
    }

    @Retry(name = "externalservice", fallbackMethod = "fallback")
    open fun testSpringResilience4jRetry(): String {
        return httpClient.httpGetRequest("http://localhost:9090/api/test/transient/error")
    }

    open fun fallback(throwable: Throwable): String {
        println("Fallback method called due to: ${throwable.message}")
        return "external service error"
    }
}