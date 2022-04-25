package com.willfp.eco.internal.config

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.willfp.eco.core.config.interfaces.Config
import java.lang.reflect.Type

object EcoGsonSerializer : JsonSerializer<Config> {
    val gson = GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .registerTypeAdapter(Config::class.java, this)
        .create()!!

    override fun serialize(src: Config, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return gson.toJsonTree(src.toMap())
    }
}
