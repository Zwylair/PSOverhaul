package zwylair.pisskaland_overhaul.items

import com.mojang.authlib.GameProfile
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryKey
import net.minecraft.util.ClickType
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import zwylair.pisskaland_overhaul.ModConfig
import zwylair.pisskaland_overhaul.ModObject.ModItem
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.itemgroups.ModItemGroups
import zwylair.pisskaland_overhaul.network.ModNetworking

class WalletItem : ModItem(FabricItemSettings().maxCount(1)) {
    override var id = PSO.id("wallet")
    override var itemGroupAddTo: RegistryKey<ItemGroup>? = ModItemGroups.PSO_ITEMGROUP_REG_KEY

    fun incrementMoneyCount(playerGameProfile: GameProfile, amount: Int) {
        ModNetworking.sendIncrementMoneyPacket(playerGameProfile, amount)
    }

    fun decrementMoneyCount(playerGameProfile: GameProfile, amount: Int) {
        ModNetworking.sendIncrementMoneyPacket(playerGameProfile, -amount)
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        if (stack.hasNbt() && stack.nbt?.contains("moneyAmount") == true) {
            val moneyAmount = stack.nbt!!.getInt("moneyAmount")

            tooltip.add(Text
                .translatable("item.${PSO.MODID}.wallet_tooltip_money_count")
                .append(": $moneyAmount")
                .formatted(Formatting.GRAY)
            )
        } else {
            tooltip.add(Text
                .translatable("item.${PSO.MODID}.wallet_tooltip_money_count")
                .append(": x")
                .formatted(Formatting.GRAY)
            )
        }
    }

    override fun onStackClicked(stack: ItemStack, slot: Slot, clickType: ClickType, player: PlayerEntity): Boolean {
        // client-side event
        // arguments:
        //     stack: instance of WalletItem
        //     slot: Slot that contains clicked item
        // returns:
        //     true: event was handled, any changes will be saved
        //     false: event was not handled, minecraft will try to revert any changes

        if (clickType == ClickType.LEFT) { return false }
        if (!slot.stack.translationKey.contains("${PSO.MODID}.svobucks")) { return false }

        incrementMoneyCount(player.gameProfile, slot.stack.count)
        if (!stack.hasNbt()) { stack.nbt = NbtCompound() }
        stack.nbt!!.putInt("moneyAmount", stack.nbt!!.getInt("moneyAmount") + slot.stack.count)
        slot.stack.decrement(slot.stack.count)
        player.inventory.markDirty()

        return true
    }

    override fun use(world: World?, player: PlayerEntity?, hand: Hand?): TypedActionResult<ItemStack?>? {
        val itemStack = player!!.getStackInHand(hand)

        world?: return TypedActionResult.fail(itemStack)

        if (!world.isClient && player is ServerPlayerEntity) {
            val stack = player.getStackInHand(hand)
            val moneyAmount = ModConfig.getMoneyAmount(player.gameProfile)

            if (!stack.hasNbt()) { stack.nbt = NbtCompound() }

            if (player.isSneaking && moneyAmount >= 10) { stack.nbt!!.putInt("moneyAmount", moneyAmount - 10) }
            else if (moneyAmount >= 1) { stack.nbt!!.putInt("moneyAmount", moneyAmount - 1) }

            player.inventory.markDirty()
            return TypedActionResult.success(player.getStackInHand(hand))
        }

        ModNetworking.sendFetchMoneyRequest(player) { moneyAmount ->
            if (player.isSneaking && moneyAmount >= 10) {
                decrementMoneyCount(player.gameProfile, 10)
                ModNetworking.sendGetCoinsPacket(player.gameProfile, 10)
            } else if (moneyAmount >= 1) {
                decrementMoneyCount(player.gameProfile, 1)
                ModNetworking.sendGetCoinsPacket(player.gameProfile, 1)
            }
        }

        return TypedActionResult.success(itemStack)
    }
}
