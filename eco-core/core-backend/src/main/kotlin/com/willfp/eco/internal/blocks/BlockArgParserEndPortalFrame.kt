package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.EndPortalFrame

object BlockArgParserEndPortalFrame : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is EndPortalFrame) return null
        var eye: Boolean? = null

        for (arg in args) {
            if (arg.equals("eye", true)) {
                eye = true
            }
        }

        eye ?: return null

        return BlockArgParseResult(
            {
                val endPortalFrame = it.blockData as? EndPortalFrame ?: return@BlockArgParseResult false

                endPortalFrame.hasEye() == eye
            },
            {
                val endPortalFrame = it.blockData as? EndPortalFrame ?: return@BlockArgParseResult

                endPortalFrame.setEye(eye)
                it.blockData = endPortalFrame
            }
        )
    }
}