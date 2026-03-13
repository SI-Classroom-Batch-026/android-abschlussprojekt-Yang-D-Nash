package com.example.yangdnashabschlussprojekt.di

import com.example.yangdnashabschlussprojekt.data.repository.CloudVisionConfig
import com.example.yangdnashabschlussprojekt.data.repository.CloudTranslateConfig
import com.example.yangdnashabschlussprojekt.data.repository.CloudTranslateTransport
import com.example.yangdnashabschlussprojekt.data.repository.CloudVisionTransport
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import platform.Foundation.NSBundle
import platform.Foundation.NSProcessInfo

class IOSCloudVisionConfig : CloudVisionConfig {
    override fun apiKey(): String? {
        val infoPlistValue = NSBundle.mainBundle.objectForInfoDictionaryKey("CLOUD_VISION_API_KEY") as? String
        if (!infoPlistValue.isNullOrBlank()) {
            return infoPlistValue
        }
        return NSProcessInfo.processInfo.environment["CLOUD_VISION_API_KEY"] as? String
    }
}

class IOSCloudTranslateConfig : CloudTranslateConfig {
    override fun apiKey(): String? {
        val infoPlistValue = NSBundle.mainBundle.objectForInfoDictionaryKey("CLOUD_TRANSLATE_API_KEY") as? String
        if (!infoPlistValue.isNullOrBlank()) {
            return infoPlistValue
        }
        return NSProcessInfo.processInfo.environment["CLOUD_TRANSLATE_API_KEY"] as? String
    }
}

class IOSCloudVisionTransport : CloudVisionTransport, CloudTranslateTransport {
    private val client = HttpClient(Darwin) {
        install(HttpTimeout) {
            requestTimeoutMillis = 5_000
            connectTimeoutMillis = 5_000
            socketTimeoutMillis = 5_000
        }
    }

    override suspend fun postJson(url: String, body: String): String {
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        if (!response.status.isSuccess()) {
            throw IllegalStateException("Vision API Fehler (${response.status.value}).")
        }
        return response.bodyAsText()
    }
}
