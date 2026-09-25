package com.learn.faulttolerancepatterns.retry

import com.learn.faulttolerancepatterns.client.HttpClient
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class BasicRetry constructor(
    private val httpClient: HttpClient
){

    private val logger = KotlinLogging.logger {}

    /**
     * The client of this app has to wait till maxRetries is reached, and then it will get the exception.
     */
    fun callExternalServiceWithRetry(url: String, maxRetries: Int): String {
        var attempt = 0
        while (attempt < maxRetries) {
            try {
                logger.info { "Calling external service" }
                return httpClient.httpGetRequest(url)
            } catch (e: Exception) {
                logger.error(e) { "Error calling external service, attempt ${attempt + 1} of $maxRetries" }
                attempt++
                if (attempt >= maxRetries) {
                    logger.error { "Max retries reached, throwing exception" }
                    throw e
                }
                Thread.sleep(1000)
            }
        }
        throw RuntimeException("Failed to call external service after $maxRetries attempts")
    }
}