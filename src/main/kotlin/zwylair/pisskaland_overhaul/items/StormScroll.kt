package zwylair.pisskaland_overhaul.items

import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import zwylair.pisskaland_overhaul.ModObject.ModItem
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.itemgroups.ModItemGroups

class StormScroll : ModItem(FabricItemSettings().maxCount(8)) {
    override var id = PSO.id("storm_scroll")
    override var itemGroupAddTo: RegistryKey<ItemGroup>? = ModItemGroups.PSO_ITEMGROUP_REG_KEY

    override fun use(world: World?, player: PlayerEntity?, hand: Hand?): TypedActionResult<ItemStack> {
        val itemStack = player!!.getStackInHand(hand)
        if (world!!.isClient) return TypedActionResult.fail(itemStack)

        world.server?.overworld?.setWeather(20, 2000, false, true)
        itemStack.decrement(1)
        return TypedActionResult.success(itemStack)
    }
}
