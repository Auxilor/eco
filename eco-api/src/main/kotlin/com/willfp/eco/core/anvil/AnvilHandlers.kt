package com.willfp.eco.core.anvil

/**
 * Global registry the eco anvil shell reads from. A single handler is supported;
 * registering replaces any previous one. If none is registered the shell is a
 * no-op and vanilla anvil behavior is untouched.
 */
object AnvilHandlers {
    @Volatile
    private var registration: Pair<AnvilHandler, AnvilSettings>? = null

    @JvmStatic
    fun register(handler: AnvilHandler, settings: AnvilSettings) {
        registration = handler to settings
    }

    @JvmStatic
    fun unregister() {
        registration = null
    }

    @JvmStatic
    fun handler(): AnvilHandler? = registration?.first

    @JvmStatic
    fun settings(): AnvilSettings? = registration?.second
}
