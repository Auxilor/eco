package com.willfp.eco.internal.spigot.proxy.common.ai

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.willfp.eco.internal.spigot.proxy.common.commonsProvider
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.ambient.AmbientCreature
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.entity.monster.AbstractIllager
import net.minecraft.world.entity.monster.SpellcasterIllager
import net.minecraft.world.entity.monster.piglin.AbstractPiglin
import net.minecraft.world.entity.player.Player
import org.bukkit.entity.AbstractHorse
import org.bukkit.entity.AbstractSkeleton
import org.bukkit.entity.AbstractVillager
import org.bukkit.entity.Ageable
import org.bukkit.entity.Ambient
import org.bukkit.entity.Animals
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Illager
import org.bukkit.entity.Mob
import org.bukkit.entity.Monster
import org.bukkit.entity.PiglinAbstract
import org.bukkit.entity.Spellcaster
import org.bukkit.entity.Tameable
import java.util.Optional

private val mappedClasses = mapOf(
    Pair(AbstractHorse::class.java, net.minecraft.world.entity.animal.horse.AbstractHorse::class.java),
    Pair(AbstractSkeleton::class.java, net.minecraft.world.entity.monster.AbstractSkeleton::class.java),
    Pair(AbstractVillager::class.java, net.minecraft.world.entity.npc.AbstractVillager::class.java),
    Pair(Ageable::class.java, AgeableMob::class.java),
    Pair(Ambient::class.java, AmbientCreature::class.java),
    Pair(Animals::class.java, Animal::class.java),
    Pair(HumanEntity::class.java, Player::class.java), // Can't spawn players
    Pair(Illager::class.java, AbstractIllager::class.java),
    Pair(Mob::class.java, net.minecraft.world.entity.Mob::class.java),
    Pair(Monster::class.java, net.minecraft.world.entity.monster.Monster::class.java),
    Pair(PiglinAbstract::class.java, AbstractPiglin::class.java),
    Pair(org.bukkit.entity.Player::class.java, Player::class.java), // Can't spawn players
    Pair(Spellcaster::class.java, SpellcasterIllager::class.java),
    Pair(Tameable::class.java, TamableAnimal::class.java)
)

private val cache: LoadingCache<Class<out org.bukkit.entity.LivingEntity>, Optional<Class<out LivingEntity>>> =
    Caffeine.newBuilder()
        .build {
            val mapped = mappedClasses[it]
            if (mapped != null) {
                return@build Optional.of(mapped)
            }

            return@build Optional.ofNullable(commonsProvider.toNMSClass(it))
        }

fun <T : org.bukkit.entity.LivingEntity> Class<T>.toNMSClass(): Class<out LivingEntity> =
    cache.get(this).orElseThrow { IllegalArgumentException("Invalid/Unsupported entity type!") }

fun LivingEntity.toBukkitEntity(): org.bukkit.entity.LivingEntity? =
    commonsProvider.toBukkitEntity(this)