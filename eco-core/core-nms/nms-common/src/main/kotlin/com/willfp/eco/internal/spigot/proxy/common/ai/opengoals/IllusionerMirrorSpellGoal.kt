package com.willfp.eco.internal.spigot.proxy.common.ai.opengoals

import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.monster.Illusioner
import net.minecraft.world.entity.monster.SpellcasterIllager
import org.bukkit.event.entity.EntityPotionEffectEvent

class IllusionerMirrorSpellGoal(
    private val illusioner: Illusioner
) : OpenUseSpellGoal(illusioner) {
    override val castingInterval = 340
    override val castingTime = 20
    override val spellPrepareSound: SoundEvent = SoundEvents.ILLUSIONER_PREPARE_MIRROR
    override val spell: SpellcasterIllager.IllagerSpell = SpellcasterIllager.IllagerSpell.DISAPPEAR

    override fun canUse(): Boolean {
        return if (!super.canUse()) false else !illusioner.hasEffect(MobEffects.INVISIBILITY)
    }

    override fun performSpellCasting() {
        illusioner.addEffect(
            MobEffectInstance(MobEffects.INVISIBILITY, 1200),
            EntityPotionEffectEvent.Cause.ILLUSION
        )
    }
}
