package com.example.yangdnashabschlussprojekt.ui.camera

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSNumber
import platform.Foundation.NSOperationQueue

data class IOSMLKitTextScanResult(
    val fullText: String,
    val blocks: List<IOSTextDetectedBox>
)

data class IOSMLKitObjectScanResult(
    val primaryLabel: String,
    val candidates: List<String>
)

private const val TEXT_REQUEST_NOTIFICATION = "SmartVisionMLKitTextRequest"
private const val TEXT_RESPONSE_NOTIFICATION = "SmartVisionMLKitTextResponse"
private const val OBJECT_REQUEST_NOTIFICATION = "SmartVisionMLKitObjectRequest"
private const val OBJECT_RESPONSE_NOTIFICATION = "SmartVisionMLKitObjectResponse"

@OptIn(ExperimentalForeignApi::class)
suspend fun recognizeTextWithIOSMLKit(
    imagePath: String
): IOSMLKitTextScanResult? = suspendCancellableCoroutine { continuation ->
    val notificationCenter = NSNotificationCenter.defaultCenter
    val requestId = "text-$imagePath"
    var observer: Any? = null

    observer = notificationCenter.addObserverForName(
        name = TEXT_RESPONSE_NOTIFICATION,
        `object` = null,
        queue = NSOperationQueue.mainQueue
    ) { notification ->
        val userInfo = notification?.userInfo ?: return@addObserverForName
        val responseRequestId = userInfo["requestId"] as? String ?: return@addObserverForName
        if (responseRequestId != requestId) return@addObserverForName

        if (observer != null) {
            notificationCenter.removeObserver(observer!!)
        }
        val error = userInfo["error"] as? String
        if (error != null) {
            continuation.resume(null)
            return@addObserverForName
        }

        val fullText = userInfo["fullText"] as? String ?: ""
        val blocks = (userInfo["blocks"] as? List<*>)
            ?.mapNotNull { payload ->
                val block = payload as? Map<*, *> ?: return@mapNotNull null
                val text = block["text"] as? String ?: return@mapNotNull null
                IOSTextDetectedBox(
                    text = text,
                    left = block.numberValue("left"),
                    top = block.numberValue("top"),
                    right = block.numberValue("right"),
                    bottom = block.numberValue("bottom")
                )
            }
            .orEmpty()

        continuation.resume(
            IOSMLKitTextScanResult(
                fullText = fullText,
                blocks = blocks
            )
        )
    }

    continuation.invokeOnCancellation {
        if (observer != null) {
            notificationCenter.removeObserver(observer!!)
        }
    }

    notificationCenter.postNotificationName(
        TEXT_REQUEST_NOTIFICATION,
        null,
        mapOf(
            "requestId" to requestId,
            "path" to imagePath
        )
    )
}

@OptIn(ExperimentalForeignApi::class)
suspend fun detectObjectsWithIOSMLKit(
    imagePath: String
): IOSMLKitObjectScanResult? = suspendCancellableCoroutine { continuation ->
    val notificationCenter = NSNotificationCenter.defaultCenter
    val requestId = "object-$imagePath"
    var observer: Any? = null

    observer = notificationCenter.addObserverForName(
        name = OBJECT_RESPONSE_NOTIFICATION,
        `object` = null,
        queue = NSOperationQueue.mainQueue
    ) { notification ->
        val userInfo = notification?.userInfo ?: return@addObserverForName
        val responseRequestId = userInfo["requestId"] as? String ?: return@addObserverForName
        if (responseRequestId != requestId) return@addObserverForName

        if (observer != null) {
            notificationCenter.removeObserver(observer!!)
        }
        val error = userInfo["error"] as? String
        if (error != null) {
            continuation.resume(null)
            return@addObserverForName
        }

        val objects = (userInfo["objects"] as? List<*>)
            ?.mapNotNull { payload ->
                val item = payload as? Map<*, *> ?: return@mapNotNull null
                (item["label"] as? String)
                    ?.trim()
                    ?.takeIf { it.isNotBlank() }
            }
            .orEmpty()

        val primaryLabel = (userInfo["primaryLabel"] as? String)
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: objects.firstOrNull().orEmpty()

        continuation.resume(
            IOSMLKitObjectScanResult(
                primaryLabel = primaryLabel,
                candidates = objects.distinct()
            )
        )
    }

    continuation.invokeOnCancellation {
        if (observer != null) {
            notificationCenter.removeObserver(observer!!)
        }
    }

    notificationCenter.postNotificationName(
        OBJECT_REQUEST_NOTIFICATION,
        null,
        mapOf(
            "requestId" to requestId,
            "path" to imagePath
        )
    )
}

private fun Map<*, *>.numberValue(key: String): Float {
    return (this[key] as? NSNumber)?.floatValue ?: 0f
}
