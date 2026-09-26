package com.learn.faulttolerancepatterns.controller

import com.learn.faulttolerancepatterns.patterns.retry.BasicRetry
import com.learn.faulttolerancepatterns.patterns.timeout.SimpleTimeout
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FaultController (
    private val basicRetry: BasicRetry,
    private val simpleTimeout: SimpleTimeout
) {

    @GetMapping("/api/fault/retry")
    fun retryFault(): String {
        return basicRetry.callExternalServiceWithRetry("http://localhost:9090/api/test/transient/error", 3)
    }

    @GetMapping("/api/fault/timeout")
    fun timeoutFault(): String {
        return simpleTimeout.callExternalServiceWithTimeout("http://localhost:9090/api/test/timeout", 3000)
    }
}