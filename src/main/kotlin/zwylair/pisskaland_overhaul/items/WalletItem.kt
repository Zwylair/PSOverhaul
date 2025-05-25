package zwylair.pisskaland_overhaul.items

import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryKey
import net.minecraft.screen.slot.Slot
import net.minecraft.text.Text
import net.minecraft.util.*
import net.minecraft.world.World
import zwylair.pisskaland_overhaul.ModObject.ModItem
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.itemgroups.ModItemGroups
import zwylair.pisskaland_overhaul.network.ModNetworking

class WalletItem : ModItem(FabricItemSettings().maxCount(1)) {
    override var id = PSO.id("wallet")
    override var itemGroupAddTo: RegistryKey<ItemGroup>? = ModItemGroups.PSO_ITEM_GROUP_REG_KEY

    private var fetchTickCounter = 0
    private val fetchTickTimeout = 20

    private fun getOrCreateNbt(stack: ItemStack): NbtCompound {
        if (!stack.hasNbt()) stack.nbt = NbtCompound()
        return stack.nbt!!
    }

    private fun getMoney(stack: ItemStack): Int =
        stack.nbt?.getInt("moneyAmount") ?: 0

    private fun setMoney(stack: ItemStack, amount: Int) {
        getOrCreateNbt(stack).putInt("moneyAmount", amount)
        ModNetworking.sendUpdateItemNbt(stack, stack.nbt!!)
    }

    private fun syncMoneyFromServer(player: PlayerEntity, stack: ItemStack) {
        ModNetworking.sendFetchMoneyRequest(player) { amount -> setMoney(stack, amount) }
    }

    private fun adjustMoney(player: PlayerEntity, delta: Int) {
        ModNetworking.sendIncrementMoney(player.gameProfile, delta)
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        val amount = getMoney(stack)
        tooltip.add(
            Text.translatable("item.${PSO.MODID}.wallet_tooltip_money_count")
                .append(": $amount")
                .formatted(Formatting.GRAY)
        )
    }

    override fun onStackClicked(stack: ItemStack, slot: Slot, clickType: ClickType, player: PlayerEntity): Boolean {
        if (
            clickType != ClickType.RIGHT ||
            !player.world.isClient ||
            slot.stack.translationKey != ModItems.SVOBUCKS.translationKey
        ) return true

        val coinCount = slot.stack.count
        adjustMoney(player, coinCount)
        player.inventory.removeStack(slot.index)
        ModNetworking.sendClearSlot(player.gameProfile, slot.index)
        return true
    }

    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        if (!world.isClient || entity !is PlayerEntity) return

        fetchTickCounter++
        if (fetchTickCounter >= fetchTickTimeout) {
            syncMoneyFromServer(entity, stack)
            fetchTickCounter = 0
        }
    }

    override fun use(world: World, player: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = player.getStackInHand(hand)
        getOrCreateNbt(stack) // ensure NBT exists

        if (!world.isClient) return TypedActionResult.pass(stack)

        ModNetworking.sendFetchMoneyRequest(player) { currentAmount ->
            val take = if (player.isSneaking && currentAmount >= 10) 10 else 1
            if (currentAmount >= take) {
                setMoney(stack, currentAmount - take)
                adjustMoney(player, -take)
                ModNetworking.sendGetCoins(player.gameProfile, take)
            }
        }

        return TypedActionResult.success(stack)
    }
}