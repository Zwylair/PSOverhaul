package zwylair.pisskaland_overhaul.damagetypes

import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageType
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.world.World

object ModDamageTypes {
//    val ELECTRICITY_DAMAGE_TYPE: RegistryKey<DamageType> = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, ZwyM.id("electricity"))

    fun of(world: World, key: RegistryKey<DamageType>?): DamageSource {
        return DamageSource(world.registryManager.get(RegistryKeys.DAMAGE_TYPE).entryOf(key))
    }
}
