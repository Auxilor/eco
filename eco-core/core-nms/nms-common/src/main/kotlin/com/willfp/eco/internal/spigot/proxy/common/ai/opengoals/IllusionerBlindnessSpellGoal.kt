package com.willfp.eco.internal.spigot.proxy.common.ai.opengoals

import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.Difficulty
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.monster.Illusioner
import net.minecraft.world.entity.monster.SpellcasterIllager
import org.bukkit.event.entity.EntityPotionEffectEvent

class IllusionerBlindnessSpellGoal(
    private val illusioner: Illusioner
) : OpenUseSpellGoal(illusioner) {
    override val castingInterval = 180
    override val castingTime = 20
    override val spellPrepareSound: SoundEvent = SoundEvents.ILLUSIONER_PREPARE_BLINDNESS
    override val spell: SpellcasterIllager.IllagerSpell = SpellcasterIllager.IllagerSpell.BLINDNESS

    private var lastTargetId = 0

    override fun canUse(): Boolean {
        return if (super.canUse()) {
            false
        } else if (illusioner.target == null) {
            false
        } else if (illusioner.target!!.id == lastTargetId) {
            false
        } else {
            illusioner.level.getCurrentDifficultyAt(illusioner.blockPosition()).isHarderThan(
                Difficulty.NORMAL.ordinal.toFloat()
            )
        }
    }

    override fun start() {
        super.start()
        if (illusioner.target != null) {
            lastTargetId = illusioner.target!!.id
        }
    }

    override fun performSpellCasting() {
        illusioner.target?.addEffect(
            MobEffectInstance(MobEffects.BLINDNESS, 400),
            illusioner,
            EntityPotionEffectEvent.Cause.ATTACK
        ) // CraftBukkit
    }
}
