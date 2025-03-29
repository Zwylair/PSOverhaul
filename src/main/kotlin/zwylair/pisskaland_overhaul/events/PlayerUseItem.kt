package zwylair.pisskaland_overhaul.events

import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.config.EatenDataConfig

object PlayerUseItem {
    fun register() {
        PSO.LOGGER.info("Trying to register PlayerUseItem events")

        UseItemCallback.EVENT.register(::useItemCallback)
    }

    private fun useItemCallback(player: PlayerEntity, world: World, hand: Hand): TypedActionResult<ItemStack> {
        var heldStack = player.getStackInHand(hand)
        if (!heldStack.isFood) return TypedActionResult.pass(heldStack)
//        heldStack.item.on()

        if (!world.isClient) {
            if (EatenDataConfig.isOnceEaten(player.gameProfile, heldStack.item)) {
                heldStack.item.onStoppedUsing(heldStack, world, player, heldStack.item.getMaxUseTime(heldStack))

                player.sendMessage(
                    Text
                        .translatable("${PSO.MODID}.chow_down_on.eaten_once")
                        .formatted(Formatting.LIGHT_PURPLE)
                )
            }

            if (EatenDataConfig.isTwiceEaten(player.gameProfile, heldStack.item)) {
                heldStack.item.onStoppedUsing(heldStack, world, player, heldStack.item.getMaxUseTime(heldStack))

                player.sendMessage(
                    Text
                        .translatable("${PSO.MODID}.chow_down_on.eaten_twice")
                        .formatted(Formatting.RED)
                )
                return TypedActionResult.success(heldStack)
            }

            EatenDataConfig.incrementFoodCounter(player.gameProfile, heldStack.item)
        }

        return TypedActionResult.success(heldStack)
    }
}
