package com.example.yangdnashabschlussprojekt.di

import com.example.yangdnashabschlussprojekt.BuildConfig
import com.example.yangdnashabschlussprojekt.data.repository.CloudVisionConfig
import com.example.yangdnashabschlussprojekt.data.repository.CloudVisionTransport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class AndroidCloudVisionConfig : CloudVisionConfig {
    override fun apiKey(): String? = BuildConfig.CLOUD_VISION_API_KEY.takeIf { it.isNotBlank() }
}

class AndroidCloudVisionTransport : CloudVisionTransport {
    override suspend fun postJson(url: String, body: String): String = withContext(Dispatchers.IO) {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 5_000
            readTimeout = 5_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
        }

        try {
            OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                writer.write(body)
            }
            val responseCode = connection.responseCode
            val responseBody = (if (responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            })?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()

            if (responseCode !in 200..299) {
                throw IllegalStateException("Vision API Fehler ($responseCode).")
            }

            responseBody
        } finally {
            connection.disconnect()
        }
    }
}
