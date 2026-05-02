package com.willfp.eco.internal.discord

import com.willfp.eco.core.EcoPlugin
import java.io.ByteArrayOutputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Discord webhook client that queues outgoing requests and executes them one at a time on a
 * dedicated daemon thread. After each successful response the worker inspects Discord's
 * rate-limit headers and sleeps when the bucket is exhausted. HTTP 429 responses cause the
 * request to be placed back at the front of the queue and the thread to sleep for the duration
 * specified by the Retry-After header before retrying.
 *
 * All public methods return a [CompletableFuture] that is completed (on the worker thread) once
 * the request has actually been sent and a final response obtained; the caller's thread never
 * blocks.
 */
class DiscordWebhookClient(private val plugin: EcoPlugin) {

    private val httpClient: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()

    /** A request that has been placed in the send queue. */
    private data class QueuedRequest(
        val httpRequest: HttpRequest,
        val resultFuture: CompletableFuture<String?>,
        /** If true the response body is forwarded to the future; otherwise null is used. */
        val returnBody: Boolean
    )

    /**
     * Unbounded double-ended blocking queue. Normal enqueue uses [LinkedBlockingDeque.put] (tail);
     * retries after 429 use [LinkedBlockingDeque.putFirst] (head) so they are retried immediately
     * after the sleep, ahead of any newly queued requests.
     */
    private val queue = LinkedBlockingDeque<QueuedRequest>()

    private val running = AtomicBoolean(true)

    private val workerThread = Thread(::processQueue, "eco-discord-webhook-worker").also {
        it.isDaemon = true
        it.start()
    }

    // -------------------------------------------------------------------------
    // Worker
    // -------------------------------------------------------------------------

    private fun processQueue() {
        while (running.get() || queue.isNotEmpty()) {
            val item = try {
                queue.poll(500, TimeUnit.MILLISECONDS) ?: continue
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            }

            try {
                // Blocking send on the worker thread — main/server thread is never touched.
                val resp = httpClient.send(item.httpRequest, BodyHandlers.ofString())
                val status = resp.statusCode()

                when {
                    status == 429 -> {
                        // Discord rate-limited us. Sleep for Retry-After, then push the request
                        // back to the front of the queue so it is the next one attempted.
                        val retryAfterSec = resp.headers()
                            .firstValue("Retry-After")
                            .map { it.toDoubleOrNull() ?: 1.0 }
                            .orElse(1.0)
                        val isGlobal = resp.headers()
                            .firstValue("X-RateLimit-Global")
                            .map { it.equals("true", ignoreCase = true) }
                            .orElse(false)
                        plugin.logger.warning(
                            "Discord webhook rate-limited (429, global=$isGlobal). " +
                                "Retrying after ${retryAfterSec}s — request re-queued at front."
                        )
                        queue.putFirst(item)
                        if (retryAfterSec != null) {
                            sleep((retryAfterSec * 1000).toLong())
                        }
                    }

                    status in 200..299 -> {
                        plugin.logger.fine("Discord webhook sent successfully: $status")
                        item.resultFuture.complete(if (item.returnBody) resp.body() else null)
                        respectBucket(resp)
                    }

                    else -> {
                        plugin.logger.warning("Discord webhook failed: $status -> ${resp.body()}")
                        item.resultFuture.complete(null)
                        respectBucket(resp)
                    }
                }
            } catch (ex: Exception) {
                plugin.logger.warning("Discord webhook exception: ${ex.message}")
                item.resultFuture.complete(null)
            }
        }
    }

    /**
     * After a non-429 response, check whether the rate-limit bucket has been exhausted and,
     * if so, sleep until Discord resets it (X-RateLimit-Reset-After).
     */
    private fun respectBucket(resp: HttpResponse<String>) {
        val remaining = resp.headers()
            .firstValue("X-RateLimit-Remaining")
            .map { it.toIntOrNull() ?: 1 }
            .orElse(1)

        if (remaining != null) {
            if (remaining <= 0) {
                val resetAfterSec = resp.headers()
                    .firstValue("X-RateLimit-Reset-After")
                    .map { it.toDoubleOrNull() ?: 0.0 }
                    .orElse(0.0)

                if (resetAfterSec != null) {
                    if (resetAfterSec > 0) {
                        val waitMs = (resetAfterSec * 1000).toLong() + 50 // +50 ms buffer
                        plugin.logger.fine(
                            "Discord webhook bucket exhausted (remaining=0). " +
                                    "Waiting ${resetAfterSec}s before next request."
                        )
                        sleep(waitMs)
                    }
                }
            }
        }
    }

    private fun sleep(ms: Long) {
        if (ms <= 0) return
        try {
            Thread.sleep(ms)
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Queue a webhook execute request with a JSON payload.
     *
     * @param webhookUrl Full webhook URL (id/token).
     * @param message    The message payload.
     * @param wait       If true, ask Discord to return the created message JSON in the future value.
     * @return A [CompletableFuture] completed on the worker thread once the request is processed.
     */
    fun executeWebhookJson(
        webhookUrl: String,
        message: DiscordWebhookMessage,
        wait: Boolean = false
    ): CompletableFuture<String?> {
        val url = buildUrl(webhookUrl, wait)
        val json = JsonUtils.toJson(message)
        val req = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(15))
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofString(json))
            .build()

        plugin.logger.fine("Queuing Discord webhook (JSON) to $url | payload: $json")
        return enqueue(req, wait)
    }

    /**
     * Queue a webhook execute request with a single file attachment (multipart/form-data).
     *
     * @param webhookUrl Full webhook URL (id/token).
     * @param message    The message payload sent as the `payload_json` part.
     * @param fileName   Filename for the attachment.
     * @param fileBytes  File contents as a byte array.
     * @param mimeType   MIME type for the attachment (e.g. "image/png").
     * @param wait       If true, ask Discord to return the created message JSON in the future value.
     * @return A [CompletableFuture] completed on the worker thread once the request is processed.
     */
    fun executeWebhookWithFile(
        webhookUrl: String,
        message: DiscordWebhookMessage,
        fileName: String,
        fileBytes: ByteArray,
        mimeType: String = "application/octet-stream",
        wait: Boolean = false
    ): CompletableFuture<String?> {
        val boundary = "----eco-webhook-${System.currentTimeMillis()}"
        val url = buildUrl(webhookUrl, wait)
        val payloadJson = JsonUtils.toJson(message)
        val nl = "\r\n"

        val baos = ByteArrayOutputStream()
        // payload_json part
        baos.write("--$boundary$nl".toByteArray())
        baos.write("Content-Disposition: form-data; name=\"payload_json\"$nl".toByteArray())
        baos.write("Content-Type: application/json$nl$nl".toByteArray())
        baos.write(payloadJson.toByteArray())
        baos.write(nl.toByteArray())
        // file part
        baos.write("--$boundary$nl".toByteArray())
        baos.write("Content-Disposition: form-data; name=\"file\"; filename=\"$fileName\"$nl".toByteArray())
        baos.write("Content-Type: $mimeType$nl$nl".toByteArray())
        baos.write(fileBytes)
        baos.write(nl.toByteArray())
        // end
        baos.write("--$boundary--$nl".toByteArray())

        val req = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(60))
            .header("Content-Type", "multipart/form-data; boundary=$boundary")
            .POST(BodyPublishers.ofByteArray(baos.toByteArray()))
            .build()

        plugin.logger.fine(
            "Queuing Discord webhook (multipart) to $url | " +
                "payload_json=$payloadJson | file=$fileName (${fileBytes.size} bytes)"
        )
        return enqueue(req, wait)
    }

    /** Convenience: send plain text, fire-and-forget (wait=false). */
    fun sendContent(webhookUrl: String, content: String): CompletableFuture<String?> =
        executeWebhookJson(webhookUrl, DiscordWebhookMessage(content = content), wait = false)

    /** Convenience: send plain text and wait for created message JSON. */
    fun sendContentWithWait(webhookUrl: String, content: String): CompletableFuture<String?> =
        executeWebhookJson(webhookUrl, DiscordWebhookMessage(content = content), wait = true)

    /**
     * Signal the worker to stop after draining remaining queued requests.
     * Should be called when the plugin is disabled.
     */
    fun shutdown() {
        running.set(false)
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private fun enqueue(req: HttpRequest, returnBody: Boolean): CompletableFuture<String?> {
        val future = CompletableFuture<String?>()
        queue.put(QueuedRequest(req, future, returnBody))
        return future
    }

    private fun buildUrl(webhookUrl: String, wait: Boolean): String {
        if (!wait) return webhookUrl
        return if (webhookUrl.contains('?')) "$webhookUrl&wait=true" else "$webhookUrl?wait=true"
    }
}
