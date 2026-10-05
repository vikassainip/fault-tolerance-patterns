package com.learn.faulttolerancepatterns.springresilience4j

import com.learn.faulttolerancepatterns.client.HttpClient
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.stereotype.Component

@Component
open class SpringResilience4jCircuitBreaker(
    private val httpClient: HttpClient,
    private val circuitBreakerRegistry: CircuitBreakerRegistry
) {

    @CircuitBreaker(name = "externalservice", fallbackMethod = "fallback")
    open fun testSpringResilience4jCircuitBreaker(): String {
        return httpClient.httpGetRequest("http://localhost:9090/api/test/internal/server/error")
    }

    open fun fallback(ex: CallNotPermittedException): String {
        println("Fallback method called due to: ${ex.message}")
        return "Circuit breaker is open"
    }

    open fun fallback(throwable: Throwable): String {
        println("Fallback method called due to: ${throwable.message}")
        val circuitBreaker = circuitBreakerRegistry.circuitBreaker("externalservice")
        println("Circuit breaker state: ${circuitBreaker.state}")
        return "external service error"
    }

}