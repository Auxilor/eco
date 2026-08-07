package com.willfp.eco.core.dragdrop

/**
 * Global registry the eco drag and drop shell reads from. Handlers are keyed by
 * [DragAndDropHandler.id] and tested in registration order; the first match wins.
 * If nothing is registered the shell is a no-op.
 */
object DragAndDropHandlers {
    /** The registered handlers, keyed by ID, in registration order. */
    private val registrations = linkedMapOf<String, Pair<DragAndDropHandler, DragAndDropSettings>>()

    /**
     * Register a handler, replacing any handler already registered under the same ID.
     *
     * @param handler  The handler.
     * @param settings The settings, defaulting to [DragAndDropSettings].
     */
    @JvmStatic
    @Synchronized
    fun register(handler: DragAndDropHandler, settings: DragAndDropSettings = DragAndDropSettings()) {
        registrations[handler.id] = handler to settings
    }

    /**
     * Remove the handler registered under an ID, if there is one.
     *
     * @param id The ID.
     */
    @JvmStatic
    @Synchronized
    fun unregister(id: String) {
        registrations.remove(id)
    }

    /**
     * Remove every handler whose ID starts with `pluginPrefix:`.
     *
     * @param pluginPrefix The plugin prefix, without the trailing colon.
     */
    @JvmStatic
    @Synchronized
    fun unregisterAll(pluginPrefix: String) {
        val prefix = "$pluginPrefix:"
        registrations.keys.filter { it.startsWith(prefix) }.forEach { registrations.remove(it) }
    }

    /**
     * Get every registration.
     *
     * @return A snapshot of the handlers and their settings, in registration order.
     */
    @JvmStatic
    @Synchronized
    fun all(): List<Pair<DragAndDropHandler, DragAndDropSettings>> = registrations.values.toList()
}
