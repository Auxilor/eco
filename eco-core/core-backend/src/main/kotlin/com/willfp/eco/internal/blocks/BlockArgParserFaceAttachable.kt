package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.FaceAttachable

object BlockArgParserFaceAttachable : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is FaceAttachable) return null
        var attachedFace: FaceAttachable.AttachedFace? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("attached_face", true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            attachedFace = runCatching { FaceAttachable.AttachedFace.valueOf(argSplit[1].uppercase()) }.getOrNull() ?: continue
        }

        attachedFace ?: return null

        return BlockArgParseResult(
            {
                val faceAttachable = it.blockData as? FaceAttachable ?: return@BlockArgParseResult false

                faceAttachable.attachedFace == attachedFace
            },
            {
                val faceAttachable = it.blockData as? FaceAttachable ?: return@BlockArgParseResult

                faceAttachable.attachedFace = attachedFace
                it.blockData = faceAttachable
            }
        )
    }
}