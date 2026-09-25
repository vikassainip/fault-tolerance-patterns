package com.learn.faulttolerancepatterns.client

import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.springframework.stereotype.Component

@Component
class HttpClient {

    fun httpGetRequest(url: String): String {
        val request: Request = Request.Builder()
            .url(url)
            .build()
        val call: Call = OkHttpClient().newCall(request)
        val response: Response = call.execute()
        return handleResponse(response)
    }

    fun httpPostRequest(url: String, jsonBody: String): String {
        val request: Request = Request.Builder()
            .url(url)
            .post(jsonBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()
        val call: Call = OkHttpClient().newCall(request)
        val response: Response = call.execute()
        return handleResponse(response)
    }

    private fun handleResponse(response: Response): String {
        val body = response.body.string() ?: ""

        if (!response.isSuccessful) {
            throw TransientServiceException(
                "HTTP ${response.code} from downstream service. Response: $body"
            )
        }

        return body
    }
}