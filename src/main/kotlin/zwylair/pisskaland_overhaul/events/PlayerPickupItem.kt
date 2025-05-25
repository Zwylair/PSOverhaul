package zwylair.pisskaland_overhaul.events

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.callbacks.PlayerPickupItemCallback
import zwylair.pisskaland_overhaul.config.DenyListConfig
import zwylair.pisskaland_overhaul.items.ModItems

object PlayerPickupItem {
    fun register() {
        PSO.LOGGER.info("Trying to register PlayerPickupItem events")

        PlayerPickupItemCallback.EVENT.register(::deniedItemsCheck)
    }

    fun deniedItemsCheck(inventory: PlayerInventory, slot: Int, stack: ItemStack): ActionResult {
        val itemId = stack.item.translationKey

        if (!DenyListConfig.isAlreadyInDenyList(itemId))
            return ActionResult.PASS

        val reward = DenyListConfig.getReward(itemId)?: 0
        val deniedItemsCount = inventory.getStack(slot).count

        inventory.removeStack(slot)
        inventory.offerOrDrop(ItemStack(ModItems.SVOBUCKS, reward * deniedItemsCount))
        inventory.updateItems()

        return ActionResult.SUCCESS
    }
}
