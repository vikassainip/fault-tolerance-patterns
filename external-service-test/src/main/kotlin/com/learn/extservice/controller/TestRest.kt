package com.learn.extservice.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestRest {

    @GetMapping("/api/test")
    fun testRest(): String {
        Thread.sleep(1000); // processing some task
        return "Hello from TestRest\n"
    }

    @GetMapping("/api/test/transient/error")
    fun testRestWithException(): String {
        Thread.sleep(1000); // processing some task
        throw RuntimeException("Exception from TestRest\n")
    }

    @GetMapping("/api/test/timeout")
    fun testRestWithTimeout(): String {
        Thread.sleep(10000); // processing some heavy task
        return "Heavy task processed\n"
    }

}