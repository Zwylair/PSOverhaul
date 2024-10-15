package zwylair.pisskaland_overhaul.events

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.World
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.items.ModItems.SVOBUCKS

object ServerTick {
    private val forbiddenItems = mapOf(
        Items.ELYTRA.translationKey to 20
    )

    const val CHECK_FOR_FORBIDDEN_ITEMS_TIMEOUT = 1 * 20
    var checkForForbiddenItemsTimeoutCount = 1 * 20

    fun register() {
        PSO.LOGGER.info("Trying to register ServerTick events")

        ServerTickEvents.END_WORLD_TICK.register(::checkForForbiddenItems)
    }

    private fun checkForForbiddenItems(world: World) {
        if (checkForForbiddenItemsTimeoutCount < CHECK_FOR_FORBIDDEN_ITEMS_TIMEOUT) {
            checkForForbiddenItemsTimeoutCount += 1
            return
        }

        checkForForbiddenItemsTimeoutCount = 0

        world.players.forEach {
            if (it is ServerPlayerEntity) {
                val inventory = it.inventory

                for (slot in 0 until inventory.size()) {
                    val stack = inventory.getStack(slot)
                    val itemTranslationKey = stack.item.translationKey

                    if (itemTranslationKey in forbiddenItems.keys) {
                        inventory.removeStack(slot)
                        inventory.insertStack(ItemStack(SVOBUCKS).copyWithCount(forbiddenItems[itemTranslationKey]!!))
                    }
                }
            }
        }
    }
}
