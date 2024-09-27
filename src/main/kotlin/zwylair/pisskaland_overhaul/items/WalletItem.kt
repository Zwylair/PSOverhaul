package zwylair.pisskaland_overhaul.items

import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
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
import zwylair.pisskaland_overhaul.items.ModItems.SVOBUCKS

class WalletItem : ModItem(FabricItemSettings().maxCount(1)) {
    override var id = PSO.id("wallet")
    override var itemGroupAddTo: RegistryKey<ItemGroup>? = ModItemGroups.PSO_ITEMGROUP_REG_KEY
    var ownerName = ""
    var moneyCount = 0

    override fun inventoryTick(stack: ItemStack?, world: World?, entity: Entity?, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, world, entity, slot, selected)
        if (ownerName.isEmpty()) { ownerName = entity?.name?.string!! }
    }

    override fun onCraft(stack: ItemStack?, world: World?, player: PlayerEntity?) {
        super.onCraft(stack, world, player)
        ownerName = player?.name?.string!!
    }

    override fun appendTooltip(stack: ItemStack?, world: World?, tooltip: MutableList<Text?>?, context: TooltipContext?) {
        val ownerTooltip = Text.translatable("item.${PSO.MODID}.wallet_tooltip_owner").append(": $ownerName")
        val moneyCountTooltip = Text.translatable("item.${PSO.MODID}.wallet_tooltip_money_count").append(": $moneyCount")

        tooltip?.add(ownerTooltip.formatted(Formatting.GRAY))
        tooltip?.add(moneyCountTooltip.formatted(Formatting.GRAY))
    }

    override fun onStackClicked(stack: ItemStack?, slot: Slot?, clickType: ClickType?, player: PlayerEntity?): Boolean {
        if (clickType == ClickType.LEFT) { return false }
        slot?: return false

        if (slot.stack.translationKey.contains("${PSO.MODID}.svobucks")) {
            moneyCount += slot.stack.count
            slot.stack.decrement(slot.stack.count)
        }
        return true
    }

    override fun use(world: World?, player: PlayerEntity?, hand: Hand?): TypedActionResult<ItemStack?>? {
        val itemStack = player!!.getStackInHand(hand)
        var coinItemStack = ItemStack(SVOBUCKS)
        if (world!!.isClient) return TypedActionResult.fail(itemStack)

        if (player.isSneaking && moneyCount >= 10) {
            moneyCount -= 10
            coinItemStack = coinItemStack.copyWithCount(10)

            player.inventory.insertStack(coinItemStack)
            return TypedActionResult.success(itemStack)
        }

        if (moneyCount >= 1) {
            moneyCount -= 1
            coinItemStack = coinItemStack.copyWithCount(1)

            player.inventory.insertStack(coinItemStack)
            return TypedActionResult.success(itemStack)
        }
        return TypedActionResult.fail(itemStack)
    }
}
