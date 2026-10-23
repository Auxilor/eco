package com.willfp.eco.internal.spigot.integrations.model

import com.willfp.eco.core.entities.args.EntityArgParseResult
import com.willfp.eco.core.entities.args.EntityArgParser
import com.willfp.eco.core.integrations.model.ModelAnimation
import com.willfp.eco.core.integrations.model.ModelIntegration
import com.willfp.eco.core.integrations.model.ModelManager

/**
 * Parses `<model plugin>:<id>[,<animation>]` (such as `model-engine:dragon,flap`) and
 * `model-hide-base`, for every registered model plugin.
 */
object EntityArgParserModel : EntityArgParser {
    private val loop = ModelAnimation.builder().loopMode(ModelAnimation.LoopMode.LOOP).build()

    override fun parseArguments(args: Array<out String>): EntityArgParseResult? {
        var integration: ModelIntegration? = null
        var id: String? = null
        var animation: String? = null
        var hideBase = false

        for (arg in args) {
            if (arg.equals("model-hide-base", ignoreCase = true)) {
                hideBase = true
                continue
            }

            val argSplit = arg.split(":", limit = 2)
            if (argSplit.size < 2) {
                continue
            }

            val found = ModelManager.getIntegration(argSplit[0]) ?: continue
            val value = argSplit[1].split(",")
            integration = found
            id = value[0]
            animation = value.getOrNull(1)?.takeIf { it.isNotBlank() }
        }

        val modelIntegration = integration ?: return null
        val modelId = id ?: return null

        return EntityArgParseResult(
            {
                modelIntegration.hasModel(it, modelId)
            },
            {
                if (!modelIntegration.attachModel(it, modelId)) {
                    return@EntityArgParseResult
                }

                if (hideBase) {
                    modelIntegration.setBaseEntityVisible(it, false)
                }

                animation?.let { name -> modelIntegration.playAnimation(it, modelId, name, loop) }
            }
        )
    }
}
