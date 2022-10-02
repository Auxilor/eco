package com.willfp.eco.internal.spigot.integrations.entitylookup

import com.ticxo.modelengine.api.ModelEngineAPI
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.args.EntityArgParseResult
import com.willfp.eco.core.entities.args.EntityArgParser
import com.willfp.eco.core.integrations.Integration

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

            for (arg in args) {
                val argSplit = arg.split(":")
                if (!argSplit[0].equals("model-engine", ignoreCase = true)) {
                    continue
                }
                if (argSplit.size < 2) {
                    continue
                }

                val modelEngineInfo = argSplit[1].split(",")

                id = modelEngineInfo.getOrNull(0)
                animation = modelEngineInfo.getOrNull(1)
            }

            if (id == null) {
                return null
            }

            return EntityArgParseResult(
                {
                    val modelled = ModelEngineAPI.getModeledEntity(it.uniqueId)

                    if (modelled == null) {
                        return@EntityArgParseResult false
                    }

                    modelled.models.containsKey(id)
                },
                {
                    val model = ModelEngineAPI.createActiveModel(id)

                    if (animation != null) {
                        val handler = model.animationHandler
                        val property = handler.getAnimation(animation)

                        if (property != null) {
                            handler.playAnimation(property, true)
                        }
                    }

                    val modelled = ModelEngineAPI.createModeledEntity(it)
                    modelled.addModel(model, true)
                }
            )
        }
    }
}
