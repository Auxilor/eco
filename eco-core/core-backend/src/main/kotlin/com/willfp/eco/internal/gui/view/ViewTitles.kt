package com.willfp.eco.internal.gui.view

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.util.toComponent
import org.bukkit.inventory.view.builder.InventoryViewBuilder
import java.lang.reflect.Method

internal interface ViewTitle {
    fun apply(handle: InventoryViewBuilder<*>, title: String): InventoryViewBuilder<*>
}

private object PaperViewTitle : ViewTitle {
    override fun apply(handle: InventoryViewBuilder<*>, title: String): InventoryViewBuilder<*> =
        handle.title(title.toComponent())
}

private object SpigotViewTitle : ViewTitle {
    // Resolved against the interface rather than the CraftBukkit implementation class, which
    // isn't public and so can't be invoked reflectively.
    private val method: Method by lazy {
        InventoryViewBuilder::class.java.getMethod("title", String::class.java)
    }

    override fun apply(handle: InventoryViewBuilder<*>, title: String): InventoryViewBuilder<*> =
        method.invoke(handle, title) as InventoryViewBuilder<*>
}

internal object ViewTitles {
    private val impl: ViewTitle =
        if (Prerequisite.HAS_PAPER.isMet) PaperViewTitle else SpigotViewTitle

    @Suppress("UNCHECKED_CAST")
    fun <B : InventoryViewBuilder<*>> apply(handle: B, title: String?): B =
        if (title == null) handle else impl.apply(handle, title) as B
}
