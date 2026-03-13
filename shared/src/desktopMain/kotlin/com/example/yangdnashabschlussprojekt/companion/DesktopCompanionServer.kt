package com.example.yangdnashabschlussprojekt.companion

import com.example.yangdnashabschlussprojekt.feature.model.CompanionHistoryEntry
import com.example.yangdnashabschlussprojekt.feature.model.CompanionSnapshot
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

private const val COMPANION_PORT = 45872

data class DesktopCompanionMirrorState(
    val serverUrls: List<String> = emptyList(),
    val lastDeviceName: String? = null,
    val lastSeenAtEpochMillis: Long? = null,
    val lastSnapshot: CompanionSnapshot? = null,
    val mirroredHistory: List<CompanionHistoryEntry> = emptyList(),
    val serverMessage: String = "Desktop Companion wartet auf ein verbundenes Handy."
)

class DesktopCompanionServer {
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private val _state = MutableStateFlow(
        DesktopCompanionMirrorState(
            serverUrls = resolveServerUrls()
        )
    )
    val state = _state.asStateFlow()

    private var httpServer: HttpServer? = null

    @Synchronized
    fun ensureStarted() {
        if (httpServer != null) return

        val server = HttpServer.create(InetSocketAddress(COMPANION_PORT), 0).apply {
            createContext("/api/companion/status") { exchange ->
                handleStatus(exchange)
            }
            createContext("/api/companion/snapshot") { exchange ->
                handleSnapshot(exchange)
            }
            executor = Executors.newCachedThreadPool()
            start()
        }

        httpServer = server
        _state.update { current ->
            current.copy(
                serverMessage = "Desktop Companion ist bereit. Verbinde dein Handy ueber eine der angezeigten Adressen."
            )
        }
    }

    private fun handleStatus(exchange: HttpExchange) {
        if (exchange.requestMethod != "GET") {
            exchange.sendSimpleResponse(405, "Method Not Allowed")
            return
        }

        val body = json.encodeToString(mapOf("ok" to true))
        exchange.sendJsonResponse(200, body)
    }

    private fun handleSnapshot(exchange: HttpExchange) {
        if (exchange.requestMethod != "POST") {
            exchange.sendSimpleResponse(405, "Method Not Allowed")
            return
        }

        val body = exchange.requestBody.use(InputStream::readBytes)
            .toString(StandardCharsets.UTF_8)

        val snapshot = runCatching {
            json.decodeFromString(CompanionSnapshot.serializer(), body)
        }.getOrElse { error ->
            exchange.sendSimpleResponse(400, error.message ?: "Invalid JSON")
            return
        }

        _state.update { current ->
            current.copy(
                lastDeviceName = snapshot.deviceName,
                lastSeenAtEpochMillis = snapshot.updatedAtEpochMillis,
                lastSnapshot = snapshot,
                mirroredHistory = mergeHistory(current.mirroredHistory, snapshot.historyItems),
                serverMessage = "${snapshot.deviceName} sendet jetzt Live-Daten an den Desktop."
            )
        }

        exchange.sendJsonResponse(200, json.encodeToString(mapOf("received" to true)))
    }

    private fun mergeHistory(
        current: List<CompanionHistoryEntry>,
        incoming: List<CompanionHistoryEntry>
    ): List<CompanionHistoryEntry> {
        if (incoming.isEmpty()) return current

        return (incoming + current)
            .distinctBy { entry ->
                "${entry.timestampMillis}-${entry.recognizedText}-${entry.translatedText}"
            }
            .sortedByDescending { it.timestampMillis }
            .take(12)
    }

    private fun HttpExchange.sendJsonResponse(code: Int, body: String) {
        responseHeaders.add("Content-Type", "application/json; charset=utf-8")
        sendResponseHeaders(code, body.toByteArray(StandardCharsets.UTF_8).size.toLong())
        responseBody.use { output ->
            output.write(body.toByteArray(StandardCharsets.UTF_8))
        }
    }

    private fun HttpExchange.sendSimpleResponse(code: Int, message: String) {
        responseHeaders.add("Content-Type", "text/plain; charset=utf-8")
        sendResponseHeaders(code, message.toByteArray(StandardCharsets.UTF_8).size.toLong())
        responseBody.use { output ->
            output.write(message.toByteArray(StandardCharsets.UTF_8))
        }
    }

    private fun resolveServerUrls(): List<String> {
        val urls = NetworkInterface.getNetworkInterfaces()
            .toList()
            .asSequence()
            .filter { network -> network.isUp && !network.isLoopback && !network.isVirtual }
            .flatMap { network -> network.inetAddresses.toList().asSequence() }
            .filterIsInstance<Inet4Address>()
            .map { address -> "http://${address.hostAddress}:$COMPANION_PORT" }
            .toMutableList()

        if (urls.none { it.contains("127.0.0.1") }) {
            urls += "http://127.0.0.1:$COMPANION_PORT"
        }

        return urls.distinct().sorted()
    }
}
