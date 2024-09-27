package zwylair.pisskaland_overhaul.events

import kotlin.random.Random
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.minecraft.entity.EntityType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.item.ItemStack
import zwylair.pisskaland_overhaul.items.ModItems.SVOBUCKS

object ServerLivingEntity {
    val bossCoinDropChance = mapOf(
        EntityType.WITHER to listOf(7, 13),
        EntityType.ELDER_GUARDIAN to listOf(20, 25),
        EntityType.WARDEN to listOf(20, 30)
    )

    fun register() {
        ServerLivingEntityEvents.AFTER_DEATH.register(::dropCoin)
    }

    private fun spawnItem(entity: LivingEntity, coinAmount: Int) {
        val coinItemStuck = ItemStack(SVOBUCKS).copyWithCount(coinAmount)
        val coinEntity = ItemEntity(entity.world, entity.x, entity.y, entity.z, coinItemStuck)
        entity.world.spawnEntity(coinEntity)
    }

    private fun dropCoin(entity: LivingEntity, damageSource: DamageSource) {
        damageSource.source?: return
        if (!damageSource.source!!.isPlayer) return

        bossCoinDropChance.forEach {
            val (entityType, coinAmount) = it
            if (entity.type == entityType) { spawnItem(entity, Random.nextInt(coinAmount[0], coinAmount[1])) }
        }

        if (entity.isPlayer) return
        if (entity !is HostileEntity) return
        if (Random.nextInt(1, 20) == 1) spawnItem(entity, 1)
    }
}
