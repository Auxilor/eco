@file:JvmName("ItemsExtensions")

package com.willfp.eco.core.blocks

import com.willfp.eco.core.items.Items
import org.bukkit.block.Block

/** @see Items.toLookupString */
fun Block?.toLookupString(): String =
    Blocks.toLookupString(this)

/** @see Items.isEmpty */
@Deprecated("Use Block.isEcoEmpty", ReplaceWith("Block.isEmpty(this)"))
val Block?.isEmpty: Boolean
    get() = Blocks.isEmpty(this)

/** @see Block.isEmpty */
val Block?.isEcoEmpty: Boolean
    get() = Blocks.isEmpty(this)

/** @see Blocks.matchesAny */
fun Collection<TestableBlock>.matches(block: Block): Boolean =
    Blocks.matchesAny(block, this)

/** @see Blocks.matchesAny */
fun Collection<TestableBlock>.matches(blocks: Collection<Block>): Boolean =
    Blocks.matchesAny(blocks, this)
