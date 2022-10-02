package com.willfp.eco.internal.lookup

import com.willfp.eco.core.lookup.LookupHandler
import com.willfp.eco.core.lookup.SegmentParser
import com.willfp.eco.core.lookup.Testable

object SegmentParserGroup : SegmentParser("||") {
    override fun <T : Testable<*>> handleSegments(segments: Array<out String>, handler: LookupHandler<T>): T {
        val possibleOptions = mutableListOf<T>()

        for (option in segments) {
            val lookup = handler.parseKey(option)
            if (handler.validate(lookup)) {
                possibleOptions.add(lookup)
            }
        }

        if (possibleOptions.isEmpty()) {
            return handler.failsafe
        }

        return handler.join(possibleOptions)
    }
}
