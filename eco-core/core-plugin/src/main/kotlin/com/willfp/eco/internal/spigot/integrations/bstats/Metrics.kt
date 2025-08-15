package com.willfp.eco.internal.spigot.integrations.bstats

import com.willfp.eco.core.EcoPlugin
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.InputStreamReader
import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import java.util.function.Supplier
import java.util.logging.Level
import java.util.zip.GZIPOutputStream
import javax.net.ssl.HttpsURLConnection

class Metrics(private val plugin: EcoPlugin) {
    private val metricsBase: MetricsBase

    private fun appendPlatformData(builder: JsonObjectBuilder) {
        builder.appendField("playerAmount", playerAmount)
        builder.appendField("onlineMode", if (Bukkit.getOnlineMode()) 1 else 0)
        builder.appendField("bukkitVersion", Bukkit.getVersion())
        builder.appendField("bukkitName", Bukkit.getName())
        builder.appendField("javaVersion", System.getProperty("java.version"))
        builder.appendField("osName", System.getProperty("os.name"))
        builder.appendField("osArch", System.getProperty("os.arch"))
        builder.appendField("osVersion", System.getProperty("os.version"))
        builder.appendField("coreCount", Runtime.getRuntime().availableProcessors())
    }

    private fun appendServiceData(builder: JsonObjectBuilder) {
        @Suppress("DEPRECATION")
        builder.appendField("pluginVersion", plugin.description.version)
    }

    private val playerAmount: Int
        get() = Bukkit.getOnlinePlayers().size

    class MetricsBase(
        private val platform: String,
        private val serverUuid: String,
        private val serviceId: Int,
        private val appendPlatformDataConsumer: Consumer<JsonObjectBuilder>,
        private val appendServiceDataConsumer: Consumer<JsonObjectBuilder>,
        private val submitTaskConsumer: Consumer<Runnable>?,
        private val checkServiceEnabledSupplier: Supplier<Boolean>,
        private val infoLogger: Consumer<String>,
        private val logSentData: Boolean,
        private val logResponseStatusText: Boolean
    ) {

        private fun startSubmitting() {
            val submitTask = Runnable {
                if (!checkServiceEnabledSupplier.get()) {
                    // Submitting data or service is disabled
                    scheduler.shutdown()
                    return@Runnable
                }
                if (submitTaskConsumer != null) {
                    submitTaskConsumer.accept { submitData() }
                } else {
                    submitData()
                }
            }
            // Many servers tend to restart at a fixed time at xx:00 which causes an uneven distribution
            // of requests on the
            // bStats backend. To circumvent this problem, we introduce some randomness into the initial
            // and second delay.
            // WARNING: You must not modify and part of this Metrics class, including the submit delay or
            // frequency!
            // WARNING: Modifying this code will get your plugin banned on bStats. Just don't do it!
            val initialDelay = (1000 * 60 * (3 + Math.random() * 3)).toLong()
            val secondDelay = (1000 * 60 * (Math.random() * 30)).toLong()
            runIgnoring {
                scheduler.schedule(submitTask, initialDelay, TimeUnit.MILLISECONDS)
                scheduler.scheduleAtFixedRate(
                    submitTask, initialDelay + secondDelay, (1000 * 60 * 30).toLong(), TimeUnit.MILLISECONDS
                )
            }
        }

        private fun submitData() {
            val baseJsonBuilder = JsonObjectBuilder()
            appendPlatformDataConsumer.accept(baseJsonBuilder)
            val serviceJsonBuilder = JsonObjectBuilder()
            appendServiceDataConsumer.accept(serviceJsonBuilder)

            serviceJsonBuilder.appendField("id", serviceId)
            baseJsonBuilder.appendField("service", serviceJsonBuilder.build())
            baseJsonBuilder.appendField("serverUUID", serverUuid)
            baseJsonBuilder.appendField("metricsVersion", METRICS_VERSION)
            val data = baseJsonBuilder.build()
            runIgnoring {
                scheduler.execute {
                    runIgnoring { sendData(data) }
                }
            }
        }

        @Throws(Exception::class)
        private fun sendData(data: JsonObjectBuilder.JsonObject) {
            if (logSentData) {
                infoLogger.accept("Sent bStats metrics data: $data")
            }
            val url = String.format(REPORT_URL, platform)
            val connection = URI(url).toURL().openConnection() as HttpsURLConnection
            // Compress the data to save bandwidth
            val compressedData = compress(data.toString())
            connection.requestMethod = "POST"
            connection.addRequestProperty("Accept", "application/json")
            connection.addRequestProperty("Connection", "close")
            connection.addRequestProperty("Content-Encoding", "gzip")
            connection.addRequestProperty("Content-Length", compressedData!!.size.toString())
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("User-Agent", "Metrics-Service/1")
            connection.doOutput = true
            DataOutputStream(connection.outputStream).use { outputStream -> outputStream.write(compressedData) }
            val builder = StringBuilder()
            BufferedReader(InputStreamReader(connection.inputStream)).use { bufferedReader ->
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    builder.append(line)
                }
            }
            if (logResponseStatusText) {
                infoLogger.accept("Sent data to bStats and received response: $builder")
            }
        }

        companion object {
            const val METRICS_VERSION = "2.2.1"
            private val scheduler =
                Executors.newScheduledThreadPool(1) { task: Runnable? -> Thread(task, "bStats-Metrics") }
            private const val REPORT_URL = "https://bStats.org/api/v2/data/%s"

            private fun compress(str: String?): ByteArray? {
                if (str == null) {
                    return null
                }
                val outputStream = ByteArrayOutputStream()
                GZIPOutputStream(outputStream).use { gzip -> gzip.write(str.toByteArray(StandardCharsets.UTF_8)) }
                return outputStream.toByteArray()
            }
        }

        init {
            startSubmitting()
        }
    }

    class JsonObjectBuilder {
        private var builder: StringBuilder? = StringBuilder()
        private var hasAtLeastOneField = false

        fun appendField(key: String?, value: String?): JsonObjectBuilder {
            requireNotNull(value) { "JSON value must not be null" }
            appendFieldUnescaped(key, "\"" + escape(value) + "\"")
            return this
        }

        fun appendField(key: String?, value: Int): JsonObjectBuilder {
            appendFieldUnescaped(key, value.toString())
            return this
        }

        fun appendField(key: String?, `object`: JsonObject?): JsonObjectBuilder {
            requireNotNull(`object`) { "JSON object must not be null" }
            appendFieldUnescaped(key, `object`.toString())
            return this
        }

        private fun appendFieldUnescaped(key: String?, escapedValue: String) {
            checkNotNull(builder) { "JSON has already been built" }
            requireNotNull(key) { "JSON key must not be null" }
            if (hasAtLeastOneField) {
                builder!!.append(",")
            }
            builder!!.append("\"").append(escape(key)).append("\":").append(escapedValue)
            hasAtLeastOneField = true
        }

        fun build(): JsonObject {
            checkNotNull(builder) { "JSON has already been built" }
            val obj = JsonObject(
                builder!!.append("}").toString()
            )
            builder = null
            return obj
        }

        class JsonObject(private val value: String) {
            override fun toString(): String {
                return value
            }
        }

        companion object {
            private fun escape(value: String): String {
                val builder = StringBuilder()
                for (element in value) {
                    if (element == '"') {
                        builder.append("\\\"")
                    } else if (element == '\\') {
                        builder.append("\\\\")
                    } else if (element <= '\u000F') {
                        builder.append("\\u000").append(Integer.toHexString(element.code))
                    } else if (element <= '\u001F') {
                        builder.append("\\u00").append(Integer.toHexString(element.code))
                    } else {
                        builder.append(element)
                    }
                }
                return builder.toString()
            }
        }

        init {
            builder!!.append("{")
        }
    }

    init {
        // Get the config file
        val bStatsFolder = File(plugin.dataFolder.parentFile, "bStats")
        val configFile = File(bStatsFolder, "config.yml")
        val config = YamlConfiguration.loadConfiguration(configFile)
        if (!config.isSet("serverUuid")) {
            config.addDefault("enabled", true)
            config.addDefault("serverUuid", UUID.randomUUID().toString())
            config.addDefault("logFailedRequests", false)
            config.addDefault("logSentData", false)
            config.addDefault("logResponseStatusText", false)
            // Inform the server owners about bStats
            config
                .options()
                .copyDefaults(true)
            config.save(configFile)
        }
        // Load the data
        val serverUUID = config.getString("serverUuid")!!
        val logSentData = config.getBoolean("logSentData", false)
        val logResponseStatusText = config.getBoolean("logResponseStatusText", false)
        metricsBase = MetricsBase(
            "bukkit",
            serverUUID,
            plugin.bStatsId,
            { builder: JsonObjectBuilder -> appendPlatformData(builder) },
            { builder: JsonObjectBuilder -> appendServiceData(builder) },
            { submitDataTask: Runnable? -> Bukkit.getScheduler().runTask(plugin, submitDataTask!!) },
            { plugin.isEnabled },
            { message: String? -> this.plugin.logger.log(Level.INFO, message) },
            logSentData,
            logResponseStatusText
        )
    }
}

private fun runIgnoring(func: () -> Unit) {
    try {
        func()
    } catch (e: Exception) {
        // Do nothing
    }
}