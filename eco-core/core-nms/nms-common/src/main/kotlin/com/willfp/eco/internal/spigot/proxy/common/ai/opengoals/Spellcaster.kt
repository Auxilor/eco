package com.willfp.eco.internal.spigot.proxy.common.ai.opengoals

import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.monster.SpellcasterIllager
import org.bukkit.craftbukkit.v1_17_R1.event.CraftEventFactory

@Suppress("UNCHECKED_CAST")
class OpenSpellcaster(private val handle: SpellcasterIllager) : SpellcasterIllager(
    handle.type as EntityType<out SpellcasterIllager>,
    handle.level
) {
    var openSpellCastingTickCount
        get() = this.spellCastingTickCount
        set(value) {
            this.spellCastingTickCount = value
        }

    val openCastingSoundEvent = this.castingSoundEvent

    override fun applyRaidBuffs(wave: Int, unused: Boolean) {
        // Mock
    }

    override fun getCelebrateSound(): SoundEvent {
        // Mock
        return handle.celebrateSound
    }

    override fun getCastingSoundEvent(): SoundEvent {
        return this.openCastingSoundEvent
    }
}

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
abstract class OpenUseSpellGoal(
    private val handle: SpellcasterIllager
) : Goal() {
    protected var attackWarmupDelay = 0
    protected var nextAttackTickCount = 0

    private val openHandle = OpenSpellcaster(handle)

    override fun canUse(): Boolean {
        val entityliving: LivingEntity = handle.target ?: return false
        return if (entityliving.isAlive) if (handle.isCastingSpell) false else handle.tickCount >= nextAttackTickCount else false
    }

    override fun canContinueToUse(): Boolean {
        val entityliving: LivingEntity = handle.target ?: return false
        return entityliving.isAlive && attackWarmupDelay > 0
    }

    override fun start() {
        attackWarmupDelay = castWarmupTime
        openHandle.openSpellCastingTickCount = castingTime
        nextAttackTickCount = handle.tickCount + castingInterval
        val soundeffect = spellPrepareSound
        if (soundeffect != null) {
            handle.playSound(soundeffect, 1.0f, 1.0f)
        }
        handle.setIsCastingSpell(spell)
    }

    override fun tick() {
        --attackWarmupDelay
        if (attackWarmupDelay == 0) {
            // CraftBukkit start
            if (!CraftEventFactory.handleEntitySpellCastEvent(
                    handle,
                    spell
                )
            ) {
                return
            }
            // CraftBukkit end
            performSpellCasting()
            handle.playSound(openHandle.openCastingSoundEvent, 1.0f, 1.0f)
        }
    }

    protected abstract fun performSpellCasting()
    protected open val castWarmupTime: Int
        get() = 20
    protected abstract val castingTime: Int
    protected abstract val castingInterval: Int
    protected abstract val spellPrepareSound: SoundEvent?
    protected abstract val spell: SpellcasterIllager.IllagerSpell?
}
