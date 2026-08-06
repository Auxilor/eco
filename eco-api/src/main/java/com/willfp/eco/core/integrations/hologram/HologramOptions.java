package com.willfp.eco.core.integrations.hologram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Immutable rendering options for a hologram.
 * <p>
 * Instances are created through {@link #builder()} or {@link #defaults()}, and map onto the
 * properties of the text display entity that backs the hologram.
 *
 * @see HologramManager#createHologram(org.bukkit.Location, HologramOptions)
 */
public final class HologramOptions {
    /**
     * The text lines of the hologram, top to bottom.
     */
    private final List<String> contents;

    /**
     * How the hologram rotates relative to the viewer.
     */
    private final Billboard billboard;

    /**
     * The uniform scale of the hologram, where 1.0 is the default size.
     */
    private final float scale;

    /**
     * The ARGB background colour, or null to use the client default background.
     */
    private final Integer backgroundColor;

    /**
     * If the text is rendered with a drop shadow.
     */
    private final boolean textShadow;

    /**
     * If the text is visible through blocks.
     */
    private final boolean seeThrough;

    /**
     * The text opacity, or null to leave the client default.
     */
    private final Byte textOpacity;

    /**
     * The view range multiplier applied to the client's entity render distance.
     */
    private final float viewRange;

    /**
     * The horizontal alignment of the text lines.
     */
    private final TextAlignment alignment;

    /**
     * The maximum line width in pixels before text wraps, or null to leave the client default.
     */
    private final Integer lineWidth;

    /**
     * If the hologram is shown to all players by default.
     */
    private final boolean visibleByDefault;

    /**
     * Create new hologram options from a builder.
     *
     * @param builder The builder to copy the values from.
     */
    private HologramOptions(@NotNull final Builder builder) {
        this.contents = Collections.unmodifiableList(new ArrayList<>(builder.contents));
        this.billboard = builder.billboard;
        this.scale = builder.scale;
        this.backgroundColor = builder.backgroundColor;
        this.textShadow = builder.textShadow;
        this.seeThrough = builder.seeThrough;
        this.textOpacity = builder.textOpacity;
        this.viewRange = builder.viewRange;
        this.alignment = builder.alignment;
        this.lineWidth = builder.lineWidth;
        this.visibleByDefault = builder.visibleByDefault;
    }

    /**
     * Get the text lines shown by the hologram.
     *
     * @return An unmodifiable copy of the text lines.
     */
    @NotNull
    public List<String> getContents() {
        return contents;
    }

    /**
     * Get how the hologram rotates relative to the viewer.
     *
     * @return The billboard mode.
     */
    @NotNull
    public Billboard getBillboard() {
        return billboard;
    }

    /**
     * Get the uniform scale of the hologram.
     *
     * @return The scale, where 1.0 is the default size.
     */
    public float getScale() {
        return scale;
    }

    /**
     * Get the background color of the hologram.
     *
     * @return ARGB background color, or null for the client default.
     */
    @Nullable
    public Integer getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Get if the text is rendered with a drop shadow.
     *
     * @return If the text has a shadow.
     */
    public boolean hasTextShadow() {
        return textShadow;
    }

    /**
     * Get if the text is visible through blocks.
     *
     * @return If the text is see-through.
     */
    public boolean isSeeThrough() {
        return seeThrough;
    }

    /**
     * Get the opacity of the hologram text.
     *
     * @return Text opacity 0-255 as a signed byte, or null for fully opaque.
     */
    @Nullable
    public Byte getTextOpacity() {
        return textOpacity;
    }

    /**
     * Get the view range multiplier applied to the client's entity render distance.
     *
     * @return The view range, where 1.0 is the default.
     */
    public float getViewRange() {
        return viewRange;
    }

    /**
     * Get the horizontal alignment of the text lines.
     *
     * @return The alignment.
     */
    @NotNull
    public TextAlignment getAlignment() {
        return alignment;
    }

    /**
     * Get the maximum line width in pixels before text wraps.
     *
     * @return The line width, or null to leave the client default.
     */
    @Nullable
    public Integer getLineWidth() {
        return lineWidth;
    }

    /**
     * Get if the hologram is shown to all players by default.
     * <p>
     * If true, {@link Hologram#hide(org.bukkit.entity.Player)} opts individual players out.
     * If false, the hologram is shown to nobody until
     * {@link Hologram#show(org.bukkit.entity.Player)} opts them in.
     *
     * @return If visible by default.
     */
    public boolean isVisibleByDefault() {
        return visibleByDefault;
    }

    /**
     * Create a builder initialized with the values of these options.
     *
     * @return A new builder pre-populated with this options' values.
     */
    @NotNull
    public Builder toBuilder() {
        return new Builder()
                .contents(this.contents)
                .billboard(this.billboard)
                .scale(this.scale)
                .backgroundColor(this.backgroundColor)
                .textShadow(this.textShadow)
                .seeThrough(this.seeThrough)
                .textOpacity(this.textOpacity)
                .viewRange(this.viewRange)
                .alignment(this.alignment)
                .lineWidth(this.lineWidth)
                .visibleByDefault(this.visibleByDefault);
    }

    /**
     * Create a new builder with all values set to their defaults.
     *
     * @return The builder.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Get hologram options with every value left at its default, and no contents.
     *
     * @return The default options.
     */
    @NotNull
    public static HologramOptions defaults() {
        return builder().build();
    }

    /**
     * Builder for {@link HologramOptions}.
     */
    public static final class Builder {
        /**
         * The text lines of the hologram, empty by default.
         */
        private List<String> contents = new ArrayList<>();

        /**
         * The billboard mode, {@link Billboard#CENTER} by default.
         */
        private Billboard billboard = Billboard.CENTER;

        /**
         * The uniform scale, 1.0 by default.
         */
        private float scale = 1.0f;

        /**
         * The ARGB background colour, null (client default) by default.
         */
        private Integer backgroundColor = null;

        /**
         * If the text has a drop shadow, false by default.
         */
        private boolean textShadow = false;

        /**
         * If the text is visible through blocks, false by default.
         */
        private boolean seeThrough = false;

        /**
         * The text opacity, null (client default) by default.
         */
        private Byte textOpacity = null;

        /**
         * The view range multiplier, 1.0 by default.
         */
        private float viewRange = 1.0f;

        /**
         * The text alignment, {@link TextAlignment#CENTER} by default.
         */
        private TextAlignment alignment = TextAlignment.CENTER;

        /**
         * The maximum line width in pixels, null (client default) by default.
         */
        private Integer lineWidth = null;

        /**
         * If the hologram is shown to all players by default, true by default.
         */
        private boolean visibleByDefault = true;

        /**
         * Set the text lines of the hologram.
         * <p>
         * The list is copied, so later modifications to it do not affect the builder.
         *
         * @param contents The text lines, top to bottom.
         * @return This builder.
         */
        @NotNull
        public Builder contents(@NotNull final List<String> contents) {
            this.contents = new ArrayList<>(contents);
            return this;
        }

        /**
         * Set how the hologram rotates relative to the viewer.
         *
         * @param billboard The billboard mode.
         * @return This builder.
         */
        @NotNull
        public Builder billboard(@NotNull final Billboard billboard) {
            this.billboard = billboard;
            return this;
        }

        /**
         * Set the uniform scale of the hologram.
         *
         * @param scale The scale, where 1.0 is the default size.
         * @return This builder.
         */
        @NotNull
        public Builder scale(final float scale) {
            this.scale = scale;
            return this;
        }

        /**
         * Set the background colour of the hologram.
         *
         * @param backgroundColor The ARGB colour, or null to use the client default background.
         * @return This builder.
         */
        @NotNull
        public Builder backgroundColor(@Nullable final Integer backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        /**
         * Set if the text is rendered with a drop shadow.
         *
         * @param textShadow If the text should have a shadow.
         * @return This builder.
         */
        @NotNull
        public Builder textShadow(final boolean textShadow) {
            this.textShadow = textShadow;
            return this;
        }

        /**
         * Set if the text is visible through blocks.
         *
         * @param seeThrough If the text should be see-through.
         * @return This builder.
         */
        @NotNull
        public Builder seeThrough(final boolean seeThrough) {
            this.seeThrough = seeThrough;
            return this;
        }

        /**
         * Set the text opacity.
         *
         * @param textOpacity The opacity, or null to leave the client default.
         * @return This builder.
         */
        @NotNull
        public Builder textOpacity(@Nullable final Byte textOpacity) {
            this.textOpacity = textOpacity;
            return this;
        }

        /**
         * Set the view range multiplier applied to the client's entity render distance.
         *
         * @param viewRange The view range, where 1.0 is the default.
         * @return This builder.
         */
        @NotNull
        public Builder viewRange(final float viewRange) {
            this.viewRange = viewRange;
            return this;
        }

        /**
         * Set the horizontal alignment of the text lines.
         *
         * @param alignment The alignment.
         * @return This builder.
         */
        @NotNull
        public Builder alignment(@NotNull final TextAlignment alignment) {
            this.alignment = alignment;
            return this;
        }

        /**
         * Set the maximum line width in pixels before text wraps.
         *
         * @param lineWidth The line width, or null to leave the client default.
         * @return This builder.
         */
        @NotNull
        public Builder lineWidth(@Nullable final Integer lineWidth) {
            this.lineWidth = lineWidth;
            return this;
        }

        /**
         * Set if the hologram is shown to all players by default.
         *
         * @param visibleByDefault If visible by default.
         * @return This builder.
         * @see HologramOptions#isVisibleByDefault()
         */
        @NotNull
        public Builder visibleByDefault(final boolean visibleByDefault) {
            this.visibleByDefault = visibleByDefault;
            return this;
        }

        /**
         * Build the hologram options.
         *
         * @return The created options.
         */
        @NotNull
        public HologramOptions build() {
            return new HologramOptions(this);
        }
    }
}
