package com.willfp.eco.core.anvil

/**
 * Global registry the eco anvil shell reads from. A single handler is supported;
 * registering replaces any previous one. If none is registered the shell is a
 * no-op and vanilla anvil behavior is untouched.
 */
object AnvilHandlers {
    @Volatile
    private var registration: Pair<AnvilHandler, AnvilSettings>? = null

    /**
     * Register the handler and settings for the anvil shell, replacing any previous
     * registration.
     *
     * @param handler  The handler.
     * @param settings The settings.
     */
    @JvmStatic
    fun register(handler: AnvilHandler, settings: AnvilSettings) {
        registration = handler to settings
    }

    /**
     * Remove the current registration, returning the anvil to vanilla behavior.
     */
    @JvmStatic
    fun unregister() {
        registration = null
    }

    /**
     * Get the registered handler.
     *
     * @return The handler, or null if nothing is registered.
     */
    @JvmStatic
    fun handler(): AnvilHandler? = registration?.first

    /**
     * Get the registered settings.
     *
     * @return The settings, or null if nothing is registered.
     */
    @JvmStatic
    fun settings(): AnvilSettings? = registration?.second
}
