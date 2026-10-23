package com.willfp.eco.core.integrations.model;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * How an animation is played on a model.
 *
 * @param lerpIn   Seconds to blend into the animation.
 * @param lerpOut  Seconds to blend out of the animation when it ends.
 * @param speed    The playback speed, where 1 is normal speed.
 * @param force    If the animation should restart if it is already playing.
 * @param loopMode How the animation repeats.
 * @param viewer   The only player to show the animation to, or null for everyone. Not every
 *                 model plugin supports this.
 */
public record ModelAnimation(double lerpIn,
                             double lerpOut,
                             double speed,
                             boolean force,
                             @NotNull LoopMode loopMode,
                             @Nullable Player viewer) {
    /**
     * Play the animation once, as the model plays it, with no blending.
     */
    public static final ModelAnimation DEFAULT = builder().build();

    /**
     * Create a builder.
     *
     * @return The builder.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * How an animation repeats.
     */
    public enum LoopMode {
        /**
         * The loop mode the model gives the animation.
         */
        MODEL,

        /**
         * Loop until stopped.
         */
        LOOP,

        /**
         * Play once, then stop.
         */
        ONCE,

        /**
         * Play once, then hold the last frame.
         */
        HOLD
    }

    /**
     * Builds a {@link ModelAnimation}.
     */
    public static final class Builder {
        /**
         * Seconds to blend in.
         */
        private double lerpIn = 0;

        /**
         * Seconds to blend out.
         */
        private double lerpOut = 0;

        /**
         * The playback speed.
         */
        private double speed = 1;

        /**
         * If the animation restarts when already playing.
         */
        private boolean force = false;

        /**
         * How the animation repeats.
         */
        private LoopMode loopMode = LoopMode.MODEL;

        /**
         * The only player to show the animation to.
         */
        private Player viewer = null;

        private Builder() {

        }

        /**
         * Set the seconds to blend into the animation.
         *
         * @param lerpIn The seconds.
         * @return The builder.
         */
        @NotNull
        public Builder lerpIn(final double lerpIn) {
            this.lerpIn = lerpIn;
            return this;
        }

        /**
         * Set the seconds to blend out of the animation.
         *
         * @param lerpOut The seconds.
         * @return The builder.
         */
        @NotNull
        public Builder lerpOut(final double lerpOut) {
            this.lerpOut = lerpOut;
            return this;
        }

        /**
         * Set the playback speed.
         *
         * @param speed The speed, where 1 is normal speed.
         * @return The builder.
         */
        @NotNull
        public Builder speed(final double speed) {
            this.speed = speed;
            return this;
        }

        /**
         * Set if the animation should restart when it is already playing.
         *
         * @param force If it should restart.
         * @return The builder.
         */
        @NotNull
        public Builder force(final boolean force) {
            this.force = force;
            return this;
        }

        /**
         * Set how the animation repeats.
         *
         * @param loopMode The loop mode.
         * @return The builder.
         */
        @NotNull
        public Builder loopMode(@NotNull final LoopMode loopMode) {
            this.loopMode = loopMode;
            return this;
        }

        /**
         * Set the only player to show the animation to.
         *
         * @param viewer The player, or null for everyone.
         * @return The builder.
         */
        @NotNull
        public Builder viewer(@Nullable final Player viewer) {
            this.viewer = viewer;
            return this;
        }

        /**
         * Build the animation.
         *
         * @return The animation.
         */
        @NotNull
        public ModelAnimation build() {
            return new ModelAnimation(lerpIn, lerpOut, speed, force, loopMode, viewer);
        }
    }
}
