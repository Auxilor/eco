package com.willfp.eco.core.integrations.discord;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a single field inside a Discord embed (name/value pair).
 */
public final class DiscordEmbedField {
    /** Field name (title). */
    public final String name;

    /** Field value (content). */
    public final String value;

    /** Whether this field should be displayed inline. */
    @Nullable
    public final Boolean inline;

    /**
     * Create a new embed field.
     *
     * @param name   Field name/title.
     * @param value  Field value/content.
     * @param inline Optional inline flag.
     */
    public DiscordEmbedField(final String name, final String value, @Nullable final Boolean inline) {
        this.name = name;
        this.value = value;
        this.inline = inline;
    }
}

