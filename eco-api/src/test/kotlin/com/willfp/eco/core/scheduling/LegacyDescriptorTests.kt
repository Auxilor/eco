package com.willfp.eco.core.scheduling

import java.lang.reflect.Method
import org.bukkit.scheduler.BukkitTask
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * Guards the eco 6 scheduling descriptors.
 *
 * Plugins compiled against eco 6 hold frozen call sites like
 * `Scheduler.runLater(Ljava/lang/Runnable;J)Lorg/bukkit/scheduler/BukkitTask;`. Those are
 * resolved by exact descriptor, so a change to a return type breaks every one of them with
 * `NoSuchMethodError` at plugin enable, which no compile of eco itself would catch.
 *
 * The descriptors survive because [Scheduler] and [RunnableTask] covariantly override
 * [LegacyScheduler] and [LegacyRunnableTask], which makes the compiler emit each old
 * descriptor as a bridge method. These tests assert on the compiled output rather than on
 * the source, because it is the compiled output that downstream links against: if someone
 * removes the `extends`, drops [EcoTask]'s `extends BukkitTask`, or renames a method, the
 * bridges silently stop being generated and these tests are what notices.
 */
internal class LegacyDescriptorTests {
    private val long = java.lang.Long.TYPE

    /**
     * Every descriptor an eco 6 jar can hold, as (owner, name, parameters, return type).
     */
    private val legacyDescriptors = listOf(
        Triple(Scheduler::class.java, "runLater", arrayOf<Class<*>>(Runnable::class.java, long)),
        Triple(Scheduler::class.java, "runLater", arrayOf<Class<*>>(long, Runnable::class.java)),
        Triple(Scheduler::class.java, "runTimer", arrayOf<Class<*>>(Runnable::class.java, long, long)),
        Triple(Scheduler::class.java, "runTimer", arrayOf<Class<*>>(long, long, Runnable::class.java)),
        Triple(Scheduler::class.java, "runAsyncTimer", arrayOf<Class<*>>(Runnable::class.java, long, long)),
        Triple(Scheduler::class.java, "runAsyncTimer", arrayOf<Class<*>>(long, long, Runnable::class.java)),
        Triple(Scheduler::class.java, "run", arrayOf<Class<*>>(Runnable::class.java)),
        Triple(Scheduler::class.java, "runAsync", arrayOf<Class<*>>(Runnable::class.java)),
        Triple(RunnableTask::class.java, "runTask", emptyArray<Class<*>>()),
        Triple(RunnableTask::class.java, "runTaskAsynchronously", emptyArray<Class<*>>()),
        Triple(RunnableTask::class.java, "runTaskLater", arrayOf<Class<*>>(long)),
        Triple(RunnableTask::class.java, "runTaskLaterAsynchronously", arrayOf<Class<*>>(long)),
        Triple(RunnableTask::class.java, "runTaskTimer", arrayOf<Class<*>>(long, long)),
        Triple(RunnableTask::class.java, "runTaskTimerAsynchronously", arrayOf<Class<*>>(long, long))
    )

    private fun declared(owner: Class<*>, name: String, params: Array<Class<*>>, returns: Class<*>): Method? =
        owner.declaredMethods.firstOrNull {
            it.name == name && it.returnType == returns && it.parameterTypes.contentEquals(params)
        }

    private fun signature(owner: Class<*>, name: String, params: Array<Class<*>>) =
        "${owner.simpleName}.$name(${params.joinToString(", ") { it.simpleName }})"

    @TestFactory
    fun `eco 6 descriptors are still emitted`(): List<DynamicTest> =
        legacyDescriptors.map { (owner, name, params) ->
            DynamicTest.dynamicTest("${signature(owner, name, params)}: BukkitTask") {
                val bridge = declared(owner, name, params, BukkitTask::class.java)

                Assertions.assertNotNull(
                    bridge,
                    "${signature(owner, name, params)} no longer returns BukkitTask. Every plugin " +
                        "compiled against eco 6 will fail with NoSuchMethodError on enable."
                )

                // A hand-written method would shadow the EcoTask-returning one during source
                // resolution. Only a compiler-emitted bridge is invisible to javac and kotlinc.
                Assertions.assertTrue(
                    bridge!!.isBridge && bridge.isSynthetic,
                    "${signature(owner, name, params)} returns BukkitTask but is not a bridge method, " +
                        "so it is visible to source and will make calls ambiguous."
                )
            }
        }

    @TestFactory
    fun `the modern descriptors sit alongside them`(): List<DynamicTest> =
        legacyDescriptors.map { (owner, name, params) ->
            DynamicTest.dynamicTest("${signature(owner, name, params)}: EcoTask") {
                Assertions.assertNotNull(
                    declared(owner, name, params, EcoTask::class.java),
                    "${signature(owner, name, params)} no longer returns EcoTask."
                )
            }
        }

    @Test
    fun `EcoTask is a BukkitTask`() {
        // The covariant overrides above are only legal because of this, so it is the single
        // assumption the entire compatibility layer rests on.
        Assertions.assertTrue(BukkitTask::class.java.isAssignableFrom(EcoTask::class.java))
    }

    @Test
    fun `the legacy interfaces are superinterfaces`() {
        Assertions.assertTrue(LegacyScheduler::class.java.isAssignableFrom(Scheduler::class.java))
        Assertions.assertTrue(LegacyRunnableTask::class.java.isAssignableFrom(RunnableTask::class.java))
    }

    @Test
    fun `syncRepeating still returns a task id`() {
        // No covariance is possible on an int return, so these are declared outright rather
        // than bridged, and are the only legacy descriptors that can go missing silently.
        Assertions.assertNotNull(
            declared(
                Scheduler::class.java, "syncRepeating",
                arrayOf(Runnable::class.java, long, long), java.lang.Integer.TYPE
            )
        )
        Assertions.assertNotNull(
            declared(
                Scheduler::class.java, "syncRepeating",
                arrayOf(long, long, Runnable::class.java), java.lang.Integer.TYPE
            )
        )
    }

    @Test
    fun `RunnableTask can still be cancelled`() {
        Assertions.assertNotNull(
            declared(LegacyRunnableTask::class.java, "cancel", emptyArray(), java.lang.Void.TYPE)
        )
    }

    @Test
    fun `everything the bridge exposes is deprecated for removal`() {
        val deprecated = listOf(
            LegacyScheduler::class.java,
            LegacyRunnableTask::class.java
        ).map { it.getAnnotation(Deprecated::class.java) }

        for (annotation in deprecated) {
            Assertions.assertNotNull(annotation)
            Assertions.assertTrue(annotation!!.forRemoval)
        }

        for (name in listOf("getTaskId", "getOwner", "isSync", "asBukkitTask")) {
            val method = EcoTask::class.java.declaredMethods.first { it.name == name }
            val annotation = method.getAnnotation(Deprecated::class.java)

            Assertions.assertNotNull(annotation, "EcoTask.$name is not deprecated.")
            Assertions.assertTrue(annotation!!.forRemoval, "EcoTask.$name is not marked for removal.")
        }
    }
}
