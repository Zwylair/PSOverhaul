package zwylair.pso.events

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.util.ActionResult
import zwylair.pso.PSO
import zwylair.pso.callbacks.HotbarSwapCallback
import zwylair.pso.config.DenyListSubConfig
import zwylair.pso.items.ModItems

object SlotActions {
    fun register() {
        PSO.LOGGER.info("Registering SlotActions events")
        HotbarSwapCallback.EVENT.register(::deniedItemsCheck)
    }

    fun deniedItemsCheck(screenHandler: ScreenHandler, slotIndex: Int, button: Int, actionType: SlotActionType, player: PlayerEntity): ActionResult {
        if (slotIndex == -999)
            return ActionResult.PASS

        val screenStack = screenHandler.getSlot(slotIndex).stack
        val itemId = screenStack.translationKey

        if (DenyListSubConfig.has(itemId)) {
            val reward = DenyListSubConfig.getReward(itemId)?: 0

            player.inventory.offerOrDrop(ItemStack(ModItems.SVOBUCKS, reward * screenStack.count))
            screenHandler.setStackInSlot(slotIndex, screenHandler.nextRevision(), ItemStack(Items.AIR))
            player.inventory.updateItems()

            return ActionResult.SUCCESS
        }

        return ActionResult.PASS
    }
}