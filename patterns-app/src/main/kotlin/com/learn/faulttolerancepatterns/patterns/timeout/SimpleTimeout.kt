package com.learn.faulttolerancepatterns.patterns.timeout

import com.learn.faulttolerancepatterns.client.HttpClient
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class SimpleTimeout (
    val httpClient: HttpClient
) {

    private val logger = KotlinLogging.logger {}

    fun callExternalServiceWithTimeout(url: String, timeoutMillis: Long): String {
        val response = arrayOfNulls<String>(1)
        val worker = object : Thread() {
            override fun run() {
                try {
                    logger.info { "Calling external service within thread ${Thread.currentThread().threadId()}" }
                    response[0] = httpClient.httpGetRequest(url)
                } catch (e: InterruptedException) {
                    throw e
                }
            }
        }
        worker.start()

        worker.join(timeoutMillis) // Waits for the worker thread to finish or timeout

        if (worker.isAlive) {
            logger.info { "Worker thread ${worker.threadId()} is still alive after join, interrupting" }
            worker.interrupt()
            throw RuntimeException("Timeout reached after $timeoutMillis milliseconds")
        }
        return response[0]!!
    }
}