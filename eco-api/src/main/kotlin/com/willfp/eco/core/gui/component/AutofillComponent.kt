package com.willfp.eco.core.gui.component

import com.willfp.eco.core.gui.GUIComponent
import kotlin.properties.Delegates

/** A [GUIComponent] that stores the maximum rows and columns it is given on init. */
open class AutofillComponent : GUIComponent {
    protected var maxRows by Delegates.notNull<Int>()
        private set
    protected var maxColumns by Delegates.notNull<Int>()
        private set

    override fun getRows() = maxRows
    override fun getColumns() = maxColumns

    override fun init(maxRows: Int, maxColumns: Int) {
        this.maxRows = maxRows
        this.maxColumns = maxColumns
    }
}
