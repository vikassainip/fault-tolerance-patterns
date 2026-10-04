package com.learn.extservice.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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

    @GetMapping("/api/test/slow/call")
    fun testRestWithTimeout(): String {
        Thread.sleep(10000); // processing some heavy task
        return "Heavy task processed\n"
    }

    @GetMapping("/api/test/internal/server/error")
    fun testRestWithInternalServerError(): ResponseEntity<String> {
        Thread.sleep(1000); // processing some task
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Internal Server Error from TestRest\n")
    }

}