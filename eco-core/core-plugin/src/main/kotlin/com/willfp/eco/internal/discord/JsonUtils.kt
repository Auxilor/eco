package com.willfp.eco.internal.discord

internal object JsonUtils {
    fun escape(s: String): String {
        val sb = StringBuilder()
        for (ch in s) {
            when (ch) {
                '"' -> sb.append("\\\"")
                '\\' -> sb.append("\\\\")
                '\b' -> sb.append("\\b")
                '\u000C' -> sb.append("\\f")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                else -> {
                    if (ch.code < 0x20) {
                        sb.append(String.format("\\u%04x", ch.code))
                    } else sb.append(ch)
                }
            }
        }
        return sb.toString()
    }

    fun toJson(message: DiscordWebhookMessage): String {
        val parts = mutableListOf<String>()
        message.content?.let { parts += "\"content\":\"${escape(it)}\"" }
        message.username?.let { parts += "\"username\":\"${escape(it)}\"" }
        message.avatar_url?.let { parts += "\"avatar_url\":\"${escape(it)}\"" }
        message.tts?.let { parts += "\"tts\":${it}" }
        message.embeds?.let { embeds ->
            val embedStrs = embeds.map { embedToJson(it) }
            parts += "\"embeds\":[${embedStrs.joinToString(",")} ]"
        }
        return "{${parts.joinToString(",")}}"
    }

    private fun embedToJson(embed: DiscordEmbed): String {
        val parts = mutableListOf<String>()
        embed.title?.let { parts += "\"title\":\"${escape(it)}\"" }
        embed.description?.let { parts += "\"description\":\"${escape(it)}\"" }
        embed.url?.let { parts += "\"url\":\"${escape(it)}\"" }
        embed.timestamp?.let { parts += "\"timestamp\":\"${escape(it)}\"" }
        embed.color?.let { parts += "\"color\":${it}" }
        embed.footer?.let { f ->
            val footerParts = mutableListOf<String>()
            footerParts += "\"text\":\"${escape(f.text)}\""
            f.icon_url?.let { footerParts += "\"icon_url\":\"${escape(it)}\"" }
            parts += "\"footer\":{${footerParts.joinToString(",")}}"
        }
        embed.image?.let { parts += "\"image\":{\"url\":\"${escape(it.url)}\"}" }
        embed.thumbnail?.let { parts += "\"thumbnail\":{\"url\":\"${escape(it.url)}\"}" }
        embed.author?.let { a ->
            val authorParts = mutableListOf<String>()
            authorParts += "\"name\":\"${escape(a.name)}\""
            a.url?.let { authorParts += "\"url\":\"${escape(it)}\"" }
            a.icon_url?.let { authorParts += "\"icon_url\":\"${escape(it)}\"" }
            parts += "\"author\":{${authorParts.joinToString(",")}}"
        }
        embed.fields?.let { fields ->
            val fieldStrs = fields.map { f ->
                val fp = mutableListOf<String>()
                fp += "\"name\":\"${escape(f.name)}\""
                fp += "\"value\":\"${escape(f.value)}\""
                f.inline?.let { fp += "\"inline\":${it}" }
                "{${fp.joinToString(",")}}"
            }
            parts += "\"fields\":[${fieldStrs.joinToString(",")}]"
        }
        return "{${parts.joinToString(",")}}"
    }
}

