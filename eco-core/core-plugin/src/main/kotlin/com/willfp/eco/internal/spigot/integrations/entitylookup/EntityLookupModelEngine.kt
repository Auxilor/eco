package com.willfp.eco.internal.spigot.integrations.entitylookup

import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.args.EntityArgParseResult
import com.willfp.eco.core.entities.args.EntityArgParser
import com.willfp.eco.core.integrations.Integration
import com.willfp.modelenginebridge.ModelEngineBridge

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

            for (arg in args) {
                val argSplit = arg.split(":")
                if (!argSplit[0].equals("model-engine", ignoreCase = true)) {
                    continue
                }
                if (argSplit.size < 2) {
                    continue
                }

                id = argSplit[1]
            }

            if (id == null) {
                return null
            }

            return EntityArgParseResult(
                {
                    val modelled =
                        ModelEngineBridge.instance.getModeledEntity(it.uniqueId) ?: return@EntityArgParseResult false

                    modelled.models.containsKey(id)
                },
                {
                    val model = ModelEngineBridge.instance.createActiveModel(id) ?: return@EntityArgParseResult

                    val modelled = ModelEngineBridge.instance.createModeledEntity(it)
                    modelled.addModel(model)
                }
            )
        }
    }
}
