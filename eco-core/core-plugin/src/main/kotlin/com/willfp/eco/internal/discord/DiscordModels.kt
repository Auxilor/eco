package com.willfp.eco.internal.discord

/**
 * Lightweight models for Discord webhook execute payload.
 * Only implements the fields commonly used (content, username, avatar_url, tts, embeds).
 */
data class DiscordWebhookMessage(
    val content: String? = null,
    val username: String? = null,
    val avatar_url: String? = null,
    val tts: Boolean? = null,
    val embeds: List<DiscordEmbed>? = null,
)

data class DiscordEmbed(
    val title: String? = null,
    val description: String? = null,
    val url: String? = null,
    val timestamp: String? = null,
    val color: Int? = null,
    val footer: DiscordEmbedFooter? = null,
    val image: DiscordEmbedMedia? = null,
    val thumbnail: DiscordEmbedMedia? = null,
    val author: DiscordEmbedAuthor? = null,
    val fields: List<DiscordEmbedField>? = null,
)

data class DiscordEmbedFooter(val text: String, val icon_url: String? = null)
data class DiscordEmbedMedia(val url: String)
data class DiscordEmbedAuthor(val name: String, val url: String? = null, val icon_url: String? = null)
data class DiscordEmbedField(val name: String, val value: String, val inline: Boolean? = null)

