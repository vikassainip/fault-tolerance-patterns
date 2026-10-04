package com.learn.faulttolerancepatterns.resilience4j

import com.learn.faulttolerancepatterns.client.HttpClient
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.function.Supplier

@Component
class Resilience4jCircuitBreaker(
    private val httpClient: HttpClient
) {

    lateinit var circuitBreaker: CircuitBreaker

    lateinit var decoratedSupplier: Supplier<String>

    @PostConstruct
    fun init() {
        configCircuitBreaker()
        decoratedSupplier = CircuitBreaker.decorateSupplier(circuitBreaker) {
            httpClient.httpGetRequest("http://localhost:9090/api/test/internal/server/error")
        }
    }

    fun configCircuitBreaker() {
        val config: CircuitBreakerConfig = CircuitBreakerConfig.custom()
            .failureRateThreshold(50.0f) // Open when 50% of calls fail
            .minimumNumberOfCalls(10) // Require 10 calls before evaluating failure rate
            .waitDurationInOpenState(Duration.ofSeconds(60)) // Wait 60s before half-open checks
            .permittedNumberOfCallsInHalfOpenState(2) // Number of calls allowed in half-open state
            .slidingWindowSize(10) // Use last 10 calls for failure-rate calculation
            .recordExceptions(Exception::class.java) // Exceptions to record as failures
            .build()

        val circuitBreakerRegistry: CircuitBreakerRegistry = CircuitBreakerRegistry.of(config)
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("myCircuitBreaker", config)
    }

    fun testCircuitBreakerWhileError(): String {
        var response: String = ""
/*        val decoratedSupplier: Supplier<String> = CircuitBreaker.decorateSupplier(circuitBreaker) {
            httpClient.httpGetRequest("http://localhost:9090/api/test/internal/server/error")
        }*/

        try {
            response = decoratedSupplier.get()
            println("Result: $response")
        } catch (e: CallNotPermittedException) {
            println("Circuit is OPEN. ${e.message}")
            return "Circuit breaker is OPEN"
        } catch (e: Exception) {
            response = "error"
            println("Error occurred: ${e.message}")
        }

        return response
    }
}