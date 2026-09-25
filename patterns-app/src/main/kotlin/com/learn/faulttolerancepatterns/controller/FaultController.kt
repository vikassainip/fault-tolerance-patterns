package com.learn.faulttolerancepatterns.controller

import com.learn.faulttolerancepatterns.retry.BasicRetry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FaultController (
    private val basicRetry: BasicRetry
) {

    @GetMapping("/api/fault/retry")
    fun retryFault(): String {
        return basicRetry.callExternalServiceWithRetry("http://localhost:9090/api/test/transient/error", 3)
    }
}