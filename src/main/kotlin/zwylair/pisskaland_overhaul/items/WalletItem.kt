package zwylair.pisskaland_overhaul.items

import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.RegistryKey
import net.minecraft.util.ClickType
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import net.minecraft.screen.slot.Slot
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import zwylair.pisskaland_overhaul.ModConfig
import zwylair.pisskaland_overhaul.ModObject.ModItem
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.itemgroups.ModItemGroups
import zwylair.pisskaland_overhaul.items.ModItems.SVOBUCKS
import zwylair.pisskaland_overhaul.network.ModNetworking

class WalletItem : ModItem(FabricItemSettings().maxCount(1)) {
    override var id = PSO.id("wallet")
    override var itemGroupAddTo: RegistryKey<ItemGroup>? = ModItemGroups.PSO_ITEMGROUP_REG_KEY
    var moneyCount = 0
    var syncedWithServer = false

    fun setMoney(newCount: Int) { moneyCount = newCount }
    fun getMoney(): Int { return moneyCount }
    fun incrementMoneyCount(amount: Int) { setMoney(moneyCount + amount) }
    fun decrementMoneyCount(amount: Int) { setMoney(moneyCount - amount) }

    override fun inventoryTick(stack: ItemStack?, world: World?, entity: Entity?, slot: Int, selected: Boolean) {
        if (world!!.isClient) {
            if (!syncedWithServer) {
                ModNetworking.forceSyncWalletMoneyAmount((entity!! as PlayerEntity).gameProfile)
                syncedWithServer = true
            }
        }
    }

    override fun appendTooltip(stack: ItemStack?, world: World?, tooltip: MutableList<Text?>?, context: TooltipContext?) {
        val moneyCountTooltip = Text.translatable("item.${PSO.MODID}.wallet_tooltip_money_count").append(": ${getMoney()}")
        tooltip?.add(moneyCountTooltip.formatted(Formatting.GRAY))
    }

    override fun onStackClicked(stack: ItemStack?, slot: Slot?, clickType: ClickType?, player: PlayerEntity?): Boolean {
        // client-side event
        // returns:
        //     true: event was handled, any changes will be saved
        //     false: event was not handled, minecraft will try to revert any changes

        if (clickType == ClickType.LEFT) { return false }
        slot?: return false

        if (slot.stack.translationKey.contains("${PSO.MODID}.svobucks")) {
            incrementMoneyCount(slot.stack.count)
            ModNetworking.sendWalletMoneyChangePacket(stack!!, getMoney())
            slot.stack.decrement(slot.stack.count)
            ModNetworking.sendCleanInventorySlotPacket(player!!.gameProfile, slot.index)
        }
        return true
    }

    override fun use(world: World?, player: PlayerEntity?, hand: Hand?): TypedActionResult<ItemStack?>? {
        val itemStack = player!!.getStackInHand(hand)
        var coinItemStack = ItemStack(SVOBUCKS)

        world?: return TypedActionResult.fail(itemStack)

//        PSO.LOGGER.info(
//            "WalletItem use():\n" +
//            "\tworld.isClient: ${world.isClient}\n" +
//            "\tmoneyAmount: ${getMoney()}\n" +
//            "\tplayer.nick: ${player.name.string}"
//        )

        if (player.isSneaking && getMoney() >= 10) {
            if (world.isClient) {
                decrementMoneyCount(10)
                return TypedActionResult.success(itemStack)
            }

            decrementMoneyCount(10)
            coinItemStack = coinItemStack.copyWithCount(10)

            ModConfig.updateMoneyData(player.uuid, getMoney())
            player.inventory.insertStack(coinItemStack)
            return TypedActionResult.success(itemStack)
        }

        if (getMoney() >= 1) {
            if (world.isClient) {
                decrementMoneyCount(1)
                return TypedActionResult.success(itemStack)
            }

            decrementMoneyCount(1)
            coinItemStack = coinItemStack.copyWithCount(1)

            ModConfig.updateMoneyData(player.uuid, getMoney())
            player.inventory.insertStack(coinItemStack)
            return TypedActionResult.success(itemStack)
        }

        return TypedActionResult.success(itemStack)
    }
}
