package com.learn.extservice.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestRest {

    @GetMapping("/api/test")
    fun testRest(): String {
        Thread.sleep(1000); // processing some heavy task
        return "Hello from TestRest\n"
    }

    @GetMapping("/api/test/transient/error")
    fun testRestWithException(): String {
        Thread.sleep(1000); // processing some heavy task
        throw RuntimeException("Exception from TestRest")
    }
}