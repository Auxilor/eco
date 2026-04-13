package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.TrialSpawner
import org.bukkit.block.data.type.Vault

object BlockArgParserTrial : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (
            blockData !is TrialSpawner &&
            blockData !is Vault
        ) return null

        var state: Any? = null
        var ominous: Boolean? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (argSplit[0].equals("ominous", true)) {
                ominous = true
            } else if (argSplit[0].equals("state", true)) {
                if (argSplit.size < 2) {
                    continue
                }
                state = when (blockData) {
                    is TrialSpawner -> runCatching { TrialSpawner.State.valueOf(argSplit[1].uppercase()) }.getOrNull()
                        ?: continue

                    is Vault -> runCatching { Vault.State.valueOf(argSplit[1].uppercase()) }.getOrNull() ?: continue
                    else -> continue
                }
            }

        }

        if (state == null && ominous == null) return null

        return BlockArgParseResult(
            {
                when (val blockData = it.blockData) {
                    is TrialSpawner ->
                        (state == null || blockData.trialSpawnerState == state) &&
                                (ominous == null || blockData.isOminous == ominous)

                    is Vault -> (state == null || blockData.vaultState == state) &&
                            (ominous == null || blockData.isOminous == ominous)

                    else -> false
                }
            },
            {
                val data = it.blockData

                when (data) {
                    is TrialSpawner -> {
                        if (ominous != null) data.isOminous = ominous
                        if (state != null) data.trialSpawnerState =
                            state as? TrialSpawner.State ?: return@BlockArgParseResult
                    }

                    is Vault -> {
                        if (ominous != null) data.isOminous = ominous
                        if (state != null) data.vaultState = state as? Vault.State ?: return@BlockArgParseResult
                    }

                    else -> return@BlockArgParseResult
                }

                it.blockData = data
            }
        )
    }
}