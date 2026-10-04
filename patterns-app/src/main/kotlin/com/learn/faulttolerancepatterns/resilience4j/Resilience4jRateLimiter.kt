package com.learn.faulttolerancepatterns.resilience4j

import com.learn.faulttolerancepatterns.client.HttpClient
import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterConfig
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import io.github.resilience4j.ratelimiter.RequestNotPermitted
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.function.Supplier

/**
    * here client is trying to not overwhelm the server, server can also protect itself by applying rate limiting
*/
@Component
class Resilience4jRateLimiter (
    private val httpClient: HttpClient
) {

    lateinit var rateLimiter: RateLimiter

    lateinit var rateLimitedExternalCall: Supplier<String>

    @PostConstruct
    fun init() {
        configRateLimiter()
        rateLimitedExternalCall = RateLimiter.decorateSupplier(rateLimiter) {
            httpClient.httpGetRequest("http://localhost:9090/api/test/slow/call")
        }
    }

    fun configRateLimiter() {
        val config = RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofSeconds(6)) // RateLimiter splits all nanoseconds from the start of epoch into cycles. Each cycle has a duration configured by RateLimiterConfig.limitRefreshPeriod
            .limitForPeriod(2) // Allow 1 request per cycle (6 seconds)
            .timeoutDuration(Duration.ofMillis(500)) // Wait for 500ms if limit is reached
            .build()

        val rateLimiterRegistry = RateLimiterRegistry.of(config)
        rateLimiter = rateLimiterRegistry.rateLimiter("myRateLimiter", config)
    }

    fun rateLimitingExternalRequest(): String {
        var response: String = ""
        try {
            response = rateLimitedExternalCall.get()
            println("Result: $response")
        } catch (e: RequestNotPermitted) {
            println("Rate limit exceeded. ${e.message}")
            return "Rate limit exceeded"
        } catch (e: Exception) {
            response = "error"
            println("Error occurred: ${e.message}")
        }
        return response
    }

}