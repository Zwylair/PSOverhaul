package zwylair.pso.items

import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.ItemGroup
import net.minecraft.registry.RegistryKey
import zwylair.pso.ModObject.ModItem
import zwylair.pso.PSO
import zwylair.pso.itemgroups.ModItemGroups

class SVOBucks : ModItem(FabricItemSettings().maxCount(10)) {
    override var id = PSO.id("svobucks")
    override var itemGroupAddTo: RegistryKey<ItemGroup>? = ModItemGroups.PSO_ITEM_GROUP_REG_KEY
}