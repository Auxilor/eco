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
        // By hierarchy, not by exact type: a nested section reaches gson as a value in a
        // Map<String, Any?>, so it is serialized by its runtime type. An exact-type adapter never
        // fires there, and the section is written out as its own fields rather than its contents.
        .registerTypeHierarchyAdapter(Config::class.java, this)
        .create()!!

    override fun serialize(src: Config, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return gson.toJsonTree(src.toMap())
    }
}
