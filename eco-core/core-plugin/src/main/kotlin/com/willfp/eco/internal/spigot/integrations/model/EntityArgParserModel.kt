package com.willfp.eco.internal.spigot.integrations.model

import com.willfp.eco.core.entities.args.EntityArgParseResult
import com.willfp.eco.core.entities.args.EntityArgParser
import com.willfp.eco.core.integrations.model.ModelAnimation
import com.willfp.eco.core.integrations.model.ModelIntegration
import com.willfp.eco.core.integrations.model.ModelManager
import org.bukkit.entity.Entity

/**
 * Parses `<model plugin>:<id>[,<animation>]` (such as `model-engine:dragon,flap`) and
 * `model-hide-base`, for every registered model plugin.
 */
object EntityArgParserModel : EntityArgParser {
    private val loop = ModelAnimation.builder().loopMode(ModelAnimation.LoopMode.LOOP).build()

    override fun parseArguments(args: Array<out String>): EntityArgParseResult? {
        val hideBase = args.any { it.equals("model-hide-base", ignoreCase = true) }
        val model = args.mapNotNull { parseModel(it) }.lastOrNull() ?: return null

        return EntityArgParseResult(
            { model.integration.hasModel(it, model.id) },
            { apply(it, model, hideBase) }
        )
    }

    private fun parseModel(arg: String): ModelArg? {
        val argSplit = arg.split(":", limit = 2)
        val integration = argSplit.takeIf { it.size == 2 }?.let { ModelManager.getIntegration(it[0]) } ?: return null
        val value = argSplit[1].split(",")

        return ModelArg(integration, value[0], value.getOrNull(1)?.takeIf { it.isNotBlank() })
    }

    private fun apply(entity: Entity, model: ModelArg, hideBase: Boolean) {
        if (!model.integration.attachModel(entity, model.id)) {
            return
        }

        if (hideBase) {
            model.integration.setBaseEntityVisible(entity, false)
        }

        model.animation?.let { model.integration.playAnimation(entity, model.id, it, loop) }
    }

    private data class ModelArg(
        val integration: ModelIntegration,
        val id: String,
        val animation: String?
    )
}
