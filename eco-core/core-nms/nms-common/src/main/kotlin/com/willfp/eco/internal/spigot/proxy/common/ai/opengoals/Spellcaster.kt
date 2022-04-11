package com.willfp.eco.internal.spigot.proxy.common.ai.opengoals

import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.monster.SpellcasterIllager

@Suppress("UNCHECKED_CAST")
class DelegatedSpellcaster(private val handle: SpellcasterIllager) : SpellcasterIllager(
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
        handle.applyRaidBuffs(wave, unused)
    }

    override fun getCelebrateSound(): SoundEvent {
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
    private var attackWarmupDelay = 0
    private var nextAttackTickCount = 0

    private val openHandle = DelegatedSpellcaster(handle)

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
            performSpellCasting()
            handle.playSound(openHandle.openCastingSoundEvent, 1.0f, 1.0f)
        }
    }

    protected abstract fun performSpellCasting()
    protected abstract val castingTime: Int
    protected abstract val castingInterval: Int
    protected abstract val spellPrepareSound: SoundEvent?
    protected abstract val spell: SpellcasterIllager.IllagerSpell?
    protected open val castWarmupTime: Int = 60
}
