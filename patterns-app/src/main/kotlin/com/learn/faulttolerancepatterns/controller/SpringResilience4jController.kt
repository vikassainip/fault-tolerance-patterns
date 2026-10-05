package com.learn.faulttolerancepatterns.controller

import com.learn.faulttolerancepatterns.springresilience4j.SpringResilience4jCircuitBreaker
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SpringResilience4jController (
    private val springResilience4jCircuitBreaker: SpringResilience4jCircuitBreaker
) {

    @GetMapping("/api/fault/spring/resilience4j/failures")
    fun testCircuitBreakerWhileError(): String {
        return springResilience4jCircuitBreaker.testSpringResilience4jCircuitBreaker()
    }
}