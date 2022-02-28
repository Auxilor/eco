package com.willfp.eco.internal.lookup

import com.willfp.eco.core.lookup.LookupHandler
import com.willfp.eco.core.lookup.LookupHelper
import com.willfp.eco.core.lookup.SegmentParser
import com.willfp.eco.core.lookup.test.Testable

class SegmentParserGroup : SegmentParser("||") {
    override fun <T : Testable<*>> handleSegments(segments: Array<out String>, handler: LookupHandler<T>): T {
        val possibleOptions: MutableList<T> = ArrayList()

        for (option in segments) {
            val lookup = LookupHelper.parseWith(option, handler)
            if (handler.validate(lookup)) {
                possibleOptions.add(lookup)
            }
        }

        return handler.join(possibleOptions)
    }
}
