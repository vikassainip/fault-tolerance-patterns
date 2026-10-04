package com.learn.faulttolerancepatterns.resilience4j

import com.learn.faulttolerancepatterns.client.HttpClient
import io.github.resilience4j.bulkhead.Bulkhead
import io.github.resilience4j.bulkhead.BulkheadConfig
import io.github.resilience4j.bulkhead.BulkheadFullException
import io.github.resilience4j.bulkhead.BulkheadRegistry
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.function.Supplier

@Component
class Resilience4jBulkhead (
    private val httpClient: HttpClient
) {

    lateinit var bulkhead: Bulkhead

    lateinit var bulkheadProtectedCall: Supplier<String>

    @PostConstruct
    fun init() {
        configBulkhead()
        bulkheadProtectedCall = Bulkhead.decorateSupplier(bulkhead) {
            httpClient.httpGetRequest("http://localhost:9090/api/test/slow/call")
        }
    }

    fun configBulkhead() {
        val config = BulkheadConfig.custom()
            .maxConcurrentCalls(2) // Maximum number of concurrent calls
            .maxWaitDuration(Duration.ZERO) // Maximum wait time for a call to be accepted
            .build()
        val bulkheadRegistry: BulkheadRegistry = BulkheadRegistry.of(config)
        bulkhead = bulkheadRegistry.bulkhead("myBulkhead", config)
    }

    fun testBulkheadWithSlowCall(): String {
        var response: String = ""
        try {
            response = bulkheadProtectedCall.get()
            println("Result: $response")
        } catch (ex: BulkheadFullException) {
            print("Bulkhead is full. ${ex.message}")
            return "Bulkhead is full"
        } catch (e: Exception) {
            response = "error"
            println("Error occurred: ${e.message}")
        }
        return response
    }
}