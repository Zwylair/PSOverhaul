package zwylair.pisskaland_overhaul.events

import kotlin.random.Random
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.minecraft.entity.EntityType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.item.ItemStack
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.items.ModItems.SVOBUCKS

object ServerLivingEntity {
    val mobCoinDropChance = mapOf(
        EntityType.WITHER to listOf(7, 13),
        EntityType.ELDER_GUARDIAN to listOf(20, 25),
        EntityType.WARDEN to listOf(20, 30),
        EntityType.BAT to listOf(1, 1)
    )

    fun register() {
        PSO.LOGGER.info("Registering ServerLivingEntity events")
        ServerLivingEntityEvents.AFTER_DEATH.register(::dropCoin)
    }

    private fun randRange(range: List<Int>): Int {
        return Random.nextInt(range[0], range[1])
    }

    private fun spawnItem(entity: LivingEntity, coinAmount: Int) {
        val coinItemStuck = ItemStack(SVOBUCKS, coinAmount)
        val coinEntity = ItemEntity(entity.world, entity.x, entity.y, entity.z, coinItemStuck)
        entity.world.spawnEntity(coinEntity)
    }

    private fun dropCoin(entity: LivingEntity, damageSource: DamageSource) {
        if (damageSource.source?.isPlayer == false)
            return

        if (entity.isPlayer || entity !is HostileEntity)
            return

        if (entity.type in mobCoinDropChance.keys) {
            for ((entityType, coinAmount) in mobCoinDropChance) {
                if (entity.type == entityType) {
                    spawnItem(entity, randRange(coinAmount))
                }
            }
            return
        }

        if (randRange(listOf(1, 100)) == 1)
            spawnItem(entity, 1)
    }
}