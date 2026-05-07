package com.willfp.eco.internal.discord

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.discord.DiscordIntegration
import com.willfp.eco.core.integrations.discord.DiscordWebhookMessage as ApiMessage
import com.willfp.eco.core.integrations.discord.ShutdownAware
import java.util.concurrent.CompletableFuture

/**
 * Implementation of the API's DiscordIntegration using the internal DiscordWebhookClient.
 * The client manages its own queued worker thread; call [shutdown] when the plugin is disabled.
 */
class DiscordIntegrationImpl(plugin: EcoPlugin) : DiscordIntegration, ShutdownAware {
    private val client = DiscordWebhookClient(plugin)

    override fun shutdown() = client.shutdown()

    override fun getPluginName(): String = "DiscordWebhook"

    override fun executeWebhookJson(webhookUrl: String, message: ApiMessage, wait: Boolean): CompletableFuture<String?> {
        return client.executeWebhookJson(webhookUrl, convertMessage(message), wait)
    }

    override fun executeWebhookWithFile(webhookUrl: String, message: ApiMessage, fileName: String, fileBytes: ByteArray, mimeType: String, wait: Boolean): CompletableFuture<String?> {
        return client.executeWebhookWithFile(webhookUrl, convertMessage(message), fileName, fileBytes, mimeType, wait)
    }

    private fun convertMessage(msg: ApiMessage): DiscordWebhookMessage {
        val embeds = msg.embeds?.map { e ->
            DiscordEmbed(
                title = e.title,
                description = e.description,
                url = e.url,
                timestamp = e.timestamp,
                color = e.color,
                footer = e.footer?.let { DiscordEmbedFooter(it.text, it.icon_url) },
                image = e.image?.let { DiscordEmbedMedia(it.url) },
                thumbnail = e.thumbnail?.let { DiscordEmbedMedia(it.url) },
                author = e.author?.let { DiscordEmbedAuthor(it.name, it.url, it.icon_url) },
                fields = e.fields?.map { f -> DiscordEmbedField(f.name, f.value, f.inline) }
            )
        }

        return DiscordWebhookMessage(
            content = msg.content,
            username = msg.username,
            avatar_url = msg.avatar_url,
            tts = msg.tts ?: false,
            embeds = embeds
        )
    }
}
