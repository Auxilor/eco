package com.willfp.eco.internal.compat.modern.items

import com.willfp.eco.core.items.Items
import com.willfp.eco.internal.compat.modern.items.parsers.ArgParserFireResistant
import com.willfp.eco.internal.compat.modern.items.parsers.ArgParserGlider
import com.willfp.eco.internal.compat.modern.items.parsers.ArgParserGlint
import com.willfp.eco.internal.compat.modern.items.parsers.ArgParserItemName
import com.willfp.eco.internal.compat.modern.items.parsers.ArgParserMaxDamage
import com.willfp.eco.internal.compat.modern.items.parsers.ArgParserMaxStackSize
import com.willfp.eco.internal.compat.modern.items.parsers.ArgParserItemModel
import com.willfp.eco.internal.compat.modern.items.parsers.ArgParserTooltipStyle
import com.willfp.eco.internal.compat.modern.items.parsers.ArgParserTrim
import com.willfp.eco.internal.items.ModernItemArgParsers

class ModernItemArgParsersImpl : ModernItemArgParsers {
    override fun registerAll() {
        Items.registerArgParser(ArgParserTrim)
        Items.registerArgParser(ArgParserFireResistant)
        Items.registerArgParser(ArgParserGlint)
        Items.registerArgParser(ArgParserItemName)
        Items.registerArgParser(ArgParserMaxDamage)
        Items.registerArgParser(ArgParserMaxStackSize)
        Items.registerArgParser(ArgParserItemModel)
        Items.registerArgParser(ArgParserGlider)
        Items.registerArgParser(ArgParserTooltipStyle)
    }
}
