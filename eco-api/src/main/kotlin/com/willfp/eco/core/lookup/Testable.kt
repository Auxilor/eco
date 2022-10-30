@file:JvmName("TestableExtensions")

package com.willfp.eco.core.lookup

/** @see Testable.matches */
fun <T> T?.matches(test: Testable<T>) =
    test.matches(this)
