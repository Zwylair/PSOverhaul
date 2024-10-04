package zwylair.pisskaland_overhaul.items

import com.mojang.authlib.GameProfile
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
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
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import zwylair.pisskaland_overhaul.ModObject.ModItem
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.itemgroups.ModItemGroups
import zwylair.pisskaland_overhaul.network.ModNetworking

class WalletItem : ModItem(FabricItemSettings().maxCount(1)) {
    override var id = PSO.id("wallet")
    override var itemGroupAddTo: RegistryKey<ItemGroup>? = ModItemGroups.PSO_ITEMGROUP_REG_KEY
    val fetchCoinsAmountTicksTimeout = 20
    var fetchCoinsAmountTicksCounter = 0

    fun incrementMoneyCount(
        playerGameProfile: GameProfile,
        amount: Int
    ) {
        ModNetworking.sendIncrementMoneyPacket(playerGameProfile, amount)
    }

    fun getEmptyNbt(): NbtCompound {
        val nbt = NbtCompound()
        nbt.putInt("moneyAmount", 0)

        return nbt
    }

    fun updateMoneyNbt(player: PlayerEntity, itemStack: ItemStack) {
        ModNetworking.sendFetchMoneyRequest(player) { moneyAmount ->
            val nbt = if (itemStack.hasNbt()) { NbtCompound().copyFrom(itemStack.nbt!!) } else { getEmptyNbt() }
            nbt.putInt("moneyAmount", moneyAmount)
            PSO.LOGGER.info("WalletItem updateMoneyNbt(): moneyAmount: $moneyAmount; put nbt moneyAmount: ${nbt.getInt("moneyAmount")}")

            itemStack.nbt = nbt
            ModNetworking.sendUpdateItemStackNbtPacket(itemStack, nbt)
        }
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

        if (clickType == ClickType.LEFT) { return false }
        if (!slot.stack.translationKey.contains("${PSO.MODID}.svobucks")) { return true }

        incrementMoneyCount(player.gameProfile, slot.stack.count)
        player.inventory.removeStack(slot.index)
        ModNetworking.sendClearSlotPacket(player.gameProfile, slot.index)
        return true
    }

    override fun inventoryTick(itemStack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        if (!world.isClient) { return }
        fetchCoinsAmountTicksCounter++

        if (fetchCoinsAmountTicksCounter >= fetchCoinsAmountTicksTimeout) {
            updateMoneyNbt(entity as PlayerEntity, itemStack)
            fetchCoinsAmountTicksCounter = 0
        }
    }

    override fun use(world: World, player: PlayerEntity, hand: Hand): TypedActionResult<ItemStack?>? {
        val itemStack = player.getStackInHand(hand)
        if (!itemStack.hasNbt()) {
            itemStack.nbt = NbtCompound()
            itemStack.nbt!!.putInt("moneyAmount", 0)
        }

        if (!world.isClient) { return TypedActionResult.pass(itemStack) }

        ModNetworking.sendFetchMoneyRequest(player) { moneyAmount ->
            if (player.isSneaking && moneyAmount >= 10) {
                itemStack.nbt!!.putInt("moneyAmount", moneyAmount - 10)
                incrementMoneyCount(player.gameProfile, -10)
                ModNetworking.sendGetCoinsPacket(player.gameProfile, 10)
                updateMoneyNbt(player, itemStack)
            } else if (moneyAmount >= 1) {
                itemStack.nbt!!.putInt("moneyAmount", moneyAmount - 1)
                incrementMoneyCount(player.gameProfile, -1)
                ModNetworking.sendGetCoinsPacket(player.gameProfile, 1)
                updateMoneyNbt(player, itemStack)
            }
        }

        return TypedActionResult.success(itemStack)
    }
}
