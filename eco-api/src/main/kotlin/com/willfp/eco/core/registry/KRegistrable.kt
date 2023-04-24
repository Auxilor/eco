package com.willfp.eco.core.registry

/**
 * A registrable that has a string ID, for use with Kotlin.
 */
interface KRegistrable : Registrable {
    /**
     * The ID of the registrable.
     */
    val id: String

    override fun getID() = id
}
