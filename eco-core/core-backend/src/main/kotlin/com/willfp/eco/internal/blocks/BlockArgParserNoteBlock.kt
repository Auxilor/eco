package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.Instrument
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.NoteBlock

object BlockArgParserNoteBlock : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is NoteBlock) return null
        var instrument: Instrument? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (argSplit[0].equals("instrument", true)) {
                if (argSplit.size < 2) {
                    continue
                }
                instrument = runCatching { Instrument.valueOf(argSplit[1].uppercase()) }.getOrNull() ?: continue
            } // TODO: notes
        }

        instrument ?: return null

        return BlockArgParseResult(
            {
                val noteBlock = it.blockData as? NoteBlock ?: return@BlockArgParseResult false

                noteBlock.instrument == instrument
            },
            {
                val noteBlock = it.blockData as? NoteBlock ?: return@BlockArgParseResult

                noteBlock.instrument = instrument
                it.blockData = noteBlock
            }
        )
    }
}