package com.willfp.eco.core.dragdrop

object DragAndDropHandlers {
    private val registrations = linkedMapOf<String, Pair<DragAndDropHandler, DragAndDropSettings>>()

    @JvmStatic
    @Synchronized
    fun register(handler: DragAndDropHandler, settings: DragAndDropSettings = DragAndDropSettings()) {
        registrations[handler.id] = handler to settings
    }

    @JvmStatic
    @Synchronized
    fun unregister(id: String) {
        registrations.remove(id)
    }

    @JvmStatic
    @Synchronized
    fun unregisterAll(pluginPrefix: String) {
        val prefix = "$pluginPrefix:"
        registrations.keys.filter { it.startsWith(prefix) }.forEach { registrations.remove(it) }
    }

    @JvmStatic
    @Synchronized
    fun all(): List<Pair<DragAndDropHandler, DragAndDropSettings>> = registrations.values.toList()
}
