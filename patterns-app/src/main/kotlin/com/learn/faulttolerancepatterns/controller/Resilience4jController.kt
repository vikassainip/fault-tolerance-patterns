package com.learn.faulttolerancepatterns.controller

import com.learn.faulttolerancepatterns.resilience4j.Resilience4jBulkhead
import com.learn.faulttolerancepatterns.resilience4j.Resilience4jCircuitBreaker
import com.learn.faulttolerancepatterns.resilience4j.Resilience4jRateLimiter
import com.learn.faulttolerancepatterns.resilience4j.Resilience4jRetry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Resilience4jController(
    private val resilience4jCircuitBreaker: Resilience4jCircuitBreaker,
    private val resilience4jBulkhead: Resilience4jBulkhead,
    private val resilience4jRateLimiter: Resilience4jRateLimiter,
    private val resilience4jRetry: Resilience4jRetry
) {

    @GetMapping("/api/fault/resilience4j/failures")
    fun resilience4jFailures(): String {
        return resilience4jCircuitBreaker.testCircuitBreakerWhileError()
    }

    @GetMapping("/api/fault/resilience4j/slow/call")
    fun bulkheadSlowCall(): String {
        return resilience4jBulkhead.testBulkheadWithSlowCall()
    }

    @GetMapping("/api/fault/resilience4j/rate/limit")
    fun rateLimit(): String {
        return resilience4jRateLimiter.rateLimitingExternalRequest()
    }

    @GetMapping("/api/fault/resilience4j/retry")
    fun resilience4jRetry(): String {
        return resilience4jRetry.testRetryForTransientError()
    }
}