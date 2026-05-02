package com.willfp.eco.core.integrations.discord;

import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a Discord embed object used in webhook messages. This is a lightweight
 * DTO matching the fields accepted by Discord's webhook execute endpoint.
 */
public final class DiscordEmbed {
    /** The embed title (max 256 characters). */
    @Nullable
    public final String title;

    /** The main embed description (max 4096 characters). */
    @Nullable
    public final String description;

    /** URL the title will link to, if set. */
    @Nullable
    public final String url;

    /** ISO8601 timestamp for the embed footer. */
    @Nullable
    public final String timestamp;

    /** Integer RGB color code for the embed sidebar. */
    @Nullable
    public final Integer color;

    /** Footer block (text and optional icon). */
    @Nullable
    public final DiscordEmbedFooter footer;

    /** Image block (displayed large in the embed). */
    @Nullable
    public final DiscordEmbedMedia image;

    /** Thumbnail block (small image in the top-right). */
    @Nullable
    public final DiscordEmbedMedia thumbnail;

    /** Author block (name, url and optional icon). */
    @Nullable
    public final DiscordEmbedAuthor author;

    /** List of embed fields (name/value pairs). */
    @Nullable
    public final List<DiscordEmbedField> fields;

    /**
     * Construct a new embed.
     *
     * @param title       The embed title.
     * @param description The embed description.
     * @param url         The URL the title links to.
     * @param timestamp   ISO8601 timestamp string.
     * @param color       RGB color integer.
     * @param footer      Footer block.
     * @param image       Image block.
     * @param thumbnail   Thumbnail block.
     * @param author      Author block.
     * @param fields      Fields to display in the embed.
     */
    public DiscordEmbed(@Nullable final String title,
                        @Nullable final String description,
                        @Nullable final String url,
                        @Nullable final String timestamp,
                        @Nullable final Integer color,
                        @Nullable final DiscordEmbedFooter footer,
                        @Nullable final DiscordEmbedMedia image,
                        @Nullable final DiscordEmbedMedia thumbnail,
                        @Nullable final DiscordEmbedAuthor author,
                        @Nullable final List<DiscordEmbedField> fields) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.timestamp = timestamp;
        this.color = color;
        this.footer = footer;
        this.image = image;
        this.thumbnail = thumbnail;
        this.author = author;
        this.fields = fields;
    }
}

