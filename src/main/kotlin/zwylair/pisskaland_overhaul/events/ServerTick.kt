package zwylair.pisskaland_overhaul.events

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.World
import zwylair.pisskaland_overhaul.items.ModItems.SVOBUCKS

object ServerTick {
    const val CHECK_FOR_ELYTRA_TIMEOUT_IN_TICKS = 1 * 20
    var checkForElytraTimeoutCount = 1 * 20

    fun register() {
        ServerTickEvents.END_WORLD_TICK.register(::checkAndRemoveElytra)
    }

    private fun checkAndRemoveElytra(world: World) {
        if (checkForElytraTimeoutCount < CHECK_FOR_ELYTRA_TIMEOUT_IN_TICKS) {
            checkForElytraTimeoutCount += 1
            return
        }

        checkForElytraTimeoutCount = 0

        world.players.forEach {
            if (it is ServerPlayerEntity) {
                val inventory = it.inventory

                for (slot in 0 until inventory.size()) {
                    val stack = inventory.getStack(slot)
                    if (stack.item == Items.ELYTRA) inventory.removeStack(slot)
                    inventory.insertStack(ItemStack(SVOBUCKS).copyWithCount(5))
                }
            }
        }
    }
}
