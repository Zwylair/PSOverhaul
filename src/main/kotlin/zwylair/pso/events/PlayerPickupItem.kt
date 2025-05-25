package zwylair.pso.events

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import zwylair.pso.PSO
import zwylair.pso.callbacks.PlayerPickupItemCallback
import zwylair.pso.config.DenyListSubConfig
import zwylair.pso.items.ModItems

object PlayerPickupItem {
    fun register() {
        PSO.LOGGER.info("Registering PlayerPickupItem events")
        PlayerPickupItemCallback.EVENT.register(::deniedItemsCheck)
    }

    fun deniedItemsCheck(inventory: PlayerInventory, slot: Int, stack: ItemStack): ActionResult {
        val itemId = stack.item.translationKey

        if (!DenyListSubConfig.has(itemId))
            return ActionResult.PASS

        val reward = DenyListSubConfig.getReward(itemId)?: 0
        val deniedItemsCount = inventory.getStack(slot).count

        inventory.removeStack(slot)
        inventory.offerOrDrop(ItemStack(ModItems.SVOBUCKS, reward * deniedItemsCount))
        inventory.updateItems()

        return ActionResult.SUCCESS
    }
}