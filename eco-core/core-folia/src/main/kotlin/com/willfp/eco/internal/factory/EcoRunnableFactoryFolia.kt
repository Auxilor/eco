package com.willfp.eco.internal.factory

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.factory.RunnableFactory
import com.willfp.eco.core.scheduling.RunnableTask
import com.willfp.eco.internal.schedule.EcoRunnableTaskFolia
import java.util.function.Consumer

class EcoRunnableFactoryFolia(private val plugin: EcoPlugin) : RunnableFactory {
    override fun create(consumer: Consumer<RunnableTask>): RunnableTask {
        return object : EcoRunnableTaskFolia(plugin) {
            override fun run() {
                consumer.accept(this)
            }
        }
    }
}