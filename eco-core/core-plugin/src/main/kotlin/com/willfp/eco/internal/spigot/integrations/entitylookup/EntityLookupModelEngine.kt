package com.willfp.eco.internal.spigot.integrations.entitylookup

import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.args.EntityArgParseResult
import com.willfp.eco.core.entities.args.EntityArgParser
import com.willfp.eco.core.integrations.Integration
import com.willfp.eco.core.integrations.modelengine.ModelEngineManager

object EntityLookupModelEngine : Integration {
    fun register() {
        Entities.registerArgParser(EntityArgParserModelEngine)
    }

    override fun getPluginName(): String {
        return "ModelEngine"
    }

    private object EntityArgParserModelEngine : EntityArgParser {
        override fun parseArguments(args: Array<out String>): EntityArgParseResult? {
            var id: String? = null
            var animation: String? = null
            var hideBase = false

            for (arg in args) {
                if (arg.equals("model-engine-hide-base", ignoreCase = true)) {
                    hideBase = true
                    continue
                }

                val argSplit = arg.split(":")
                if (!argSplit[0].equals("model-engine", ignoreCase = true)) {
                    continue
                }
                if (argSplit.size < 2) {
                    continue
                }

                val value = argSplit[1].split(",")
                id = value[0]
                animation = value.getOrNull(1)?.takeIf { it.isNotBlank() }
            }

            if (id == null) {
                return null
            }

            return EntityArgParseResult(
                {
                    val modelled =
                        ModelEngineManager.getModeledEntity(it) ?: return@EntityArgParseResult false

                    modelled.models.containsKey(id)
                },
                {
                    val model = ModelEngineManager.createActiveModel(id) ?: return@EntityArgParseResult

                    val modelled = ModelEngineManager.createModeledEntity(it) ?: return@EntityArgParseResult
                    modelled.addModel(model)

                    if (hideBase) {
                        modelled.isBaseEntityVisible = false
                    }

                    animation?.let { name -> model.animationHandler.playAnimation(name, 0.0, 0.0, 1.0, false, true) }
                }
            )
        }
    }
}
