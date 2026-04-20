package com.willfp.eco.internal.factory

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.factory.RunnableFactory
import com.willfp.eco.core.scheduling.RunnableTask
import com.willfp.eco.internal.scheduling.EcoRunnableTaskSpigot
import java.util.function.Consumer

class EcoRunnableFactorySpigot(private val plugin: EcoPlugin) : RunnableFactory {
    override fun create(consumer: Consumer<RunnableTask>): RunnableTask {
        return object : EcoRunnableTaskSpigot(plugin) {
            override fun run() {
                consumer.accept(this)
            }
        }
    }
}