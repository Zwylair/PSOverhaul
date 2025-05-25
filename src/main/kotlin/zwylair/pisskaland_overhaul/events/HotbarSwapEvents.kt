package zwylair.pisskaland_overhaul.events

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.util.ActionResult
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.callbacks.HotbarSwapCallback
import zwylair.pisskaland_overhaul.config.DenyListConfig
import zwylair.pisskaland_overhaul.items.ModItems

object HotbarSwapEvents {
    fun register() {
        PSO.LOGGER.info("Trying to register HotbarSwapEvents events")

        HotbarSwapCallback.EVENT.register(::deniedItemsCheck)
    }

    fun deniedItemsCheck(screenHandler: ScreenHandler, slotIndex: Int, button: Int, actionType: SlotActionType, player: PlayerEntity): ActionResult {
        if (slotIndex == -999)
            return ActionResult.PASS

        val screenStack = screenHandler.getSlot(slotIndex).stack
        val itemId = screenStack.translationKey

        if (DenyListConfig.isAlreadyInDenyList(itemId)) {
            val reward = DenyListConfig.getReward(itemId)?: 0

            player.inventory.offerOrDrop(ItemStack(ModItems.SVOBUCKS, reward * screenStack.count))
            screenHandler.setStackInSlot(slotIndex, screenHandler.nextRevision(), ItemStack(Items.AIR))
            player.inventory.updateItems()

            return ActionResult.SUCCESS
        }

        return ActionResult.PASS
    }
}
