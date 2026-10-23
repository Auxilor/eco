package com.willfp.eco.core.integrations.modelengine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handles the animations of a ModelEngine model.
 */
public interface BridgedAnimationHandler {
    /**
     * Get the model this handler belongs to.
     *
     * @return The model.
     */
    @NotNull
    BridgedActiveModel getModel();

    /**
     * Get an animation by name.
     *
     * @param name The name of the animation.
     * @return The animation, or null if the model has no animation with that name.
     */
    @Nullable
    BridgedAnimation getAnimation(@NotNull String name);

    /**
     * Play an animation.
     *
     * @param animation The animation, from {@link #getAnimation(String)}.
     * @param force     If the animation should play even if it is already playing.
     */
    void playAnimation(@NotNull BridgedAnimation animation,
                       boolean force);

    /**
     * Play an animation, optionally looping it until it is stopped.
     *
     * @param animation The animation, from {@link #getAnimation(String)}.
     * @param force     If the animation should play even if it is already playing.
     * @param loop      If the animation should loop whatever loop mode the model gives it.
     */
    default void playAnimation(@NotNull final BridgedAnimation animation,
                               final boolean force,
                               final boolean loop) {
        playAnimation(animation, force);
    }

    /**
     * Play an animation by name.
     *
     * @param name    The name of the animation.
     * @param lerpIn  Seconds to blend into the animation.
     * @param lerpOut Seconds to blend out of the animation when it stops.
     * @param speed   The playback speed, where 1 is normal speed.
     * @param force   If the animation should restart if it is already playing.
     * @param loop    If the animation should loop whatever loop mode the model gives it.
     * @return If the animation is playing.
     */
    default boolean playAnimation(@NotNull final String name,
                                  final double lerpIn,
                                  final double lerpOut,
                                  final double speed,
                                  final boolean force,
                                  final boolean loop) {
        BridgedAnimation animation = getAnimation(name);

        if (animation == null) {
            return false;
        }

        playAnimation(animation, force, loop);
        return true;
    }

    /**
     * Get if an animation is playing.
     *
     * @param name The name of the animation.
     * @return If the animation is playing.
     */
    boolean isPlayingAnimation(@NotNull String name);

    /**
     * Stop an animation, letting it fade out.
     *
     * @param name The name of the animation.
     */
    void stopAnimation(@NotNull String name);

    /**
     * Stop an animation straight away, without fading out.
     *
     * @param name The name of the animation.
     */
    default void forceStopAnimation(@NotNull final String name) {
        stopAnimation(name);
    }
}
