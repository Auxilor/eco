package com.willfp.eco.core.integrations.hologram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Immutable rendering options for a hologram.
 */
public final class HologramOptions {
    private final List<String> contents;
    private final Billboard billboard;
    private final float scale;
    private final Integer backgroundColor;
    private final boolean textShadow;
    private final boolean seeThrough;
    private final Byte textOpacity;
    private final float viewRange;
    private final TextAlignment alignment;
    private final Integer lineWidth;
    private final boolean visibleByDefault;

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

    /** @return An unmodifiable copy of the text lines. */
    @NotNull
    public List<String> getContents() {
        return contents;
    }

    @NotNull
    public Billboard getBillboard() {
        return billboard;
    }

    public float getScale() {
        return scale;
    }

    /** @return ARGB background color, or null for the client default. */
    @Nullable
    public Integer getBackgroundColor() {
        return backgroundColor;
    }

    public boolean hasTextShadow() {
        return textShadow;
    }

    public boolean isSeeThrough() {
        return seeThrough;
    }

    /** @return Text opacity 0-255 as a signed byte, or null for fully opaque. */
    @Nullable
    public Byte getTextOpacity() {
        return textOpacity;
    }

    public float getViewRange() {
        return viewRange;
    }

    @NotNull
    public TextAlignment getAlignment() {
        return alignment;
    }

    @Nullable
    public Integer getLineWidth() {
        return lineWidth;
    }

    public boolean isVisibleByDefault() {
        return visibleByDefault;
    }

    /** @return A new builder pre-populated with this options' values. */
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

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull
    public static HologramOptions defaults() {
        return builder().build();
    }

    /**
     * Builder for {@link HologramOptions}.
     */
    public static final class Builder {
        private List<String> contents = new ArrayList<>();
        private Billboard billboard = Billboard.CENTER;
        private float scale = 1.0f;
        private Integer backgroundColor = null;
        private boolean textShadow = false;
        private boolean seeThrough = false;
        private Byte textOpacity = null;
        private float viewRange = 1.0f;
        private TextAlignment alignment = TextAlignment.CENTER;
        private Integer lineWidth = null;
        private boolean visibleByDefault = true;

        @NotNull
        public Builder contents(@NotNull final List<String> contents) {
            this.contents = new ArrayList<>(contents);
            return this;
        }

        @NotNull
        public Builder billboard(@NotNull final Billboard billboard) {
            this.billboard = billboard;
            return this;
        }

        @NotNull
        public Builder scale(final float scale) {
            this.scale = scale;
            return this;
        }

        @NotNull
        public Builder backgroundColor(@Nullable final Integer backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        @NotNull
        public Builder textShadow(final boolean textShadow) {
            this.textShadow = textShadow;
            return this;
        }

        @NotNull
        public Builder seeThrough(final boolean seeThrough) {
            this.seeThrough = seeThrough;
            return this;
        }

        @NotNull
        public Builder textOpacity(@Nullable final Byte textOpacity) {
            this.textOpacity = textOpacity;
            return this;
        }

        @NotNull
        public Builder viewRange(final float viewRange) {
            this.viewRange = viewRange;
            return this;
        }

        @NotNull
        public Builder alignment(@NotNull final TextAlignment alignment) {
            this.alignment = alignment;
            return this;
        }

        @NotNull
        public Builder lineWidth(@Nullable final Integer lineWidth) {
            this.lineWidth = lineWidth;
            return this;
        }

        @NotNull
        public Builder visibleByDefault(final boolean visibleByDefault) {
            this.visibleByDefault = visibleByDefault;
            return this;
        }

        @NotNull
        public HologramOptions build() {
            return new HologramOptions(this);
        }
    }
}
