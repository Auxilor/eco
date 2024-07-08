package com.willfp.eco.internal.compat.modern.entities

import com.willfp.eco.core.entities.Entities
import com.willfp.eco.internal.compat.modern.entities.parsers.EntityArgParserJumpStrength
import com.willfp.eco.internal.compat.modern.entities.parsers.EntityArgParserScale
import com.willfp.eco.internal.entities.ModernEntityArgParsers

class ModernEntityArgParsersImpl: ModernEntityArgParsers {
    override fun registerAll() {
        Entities.registerArgParser(EntityArgParserScale)
        Entities.registerArgParser(EntityArgParserJumpStrength)
    }
}
