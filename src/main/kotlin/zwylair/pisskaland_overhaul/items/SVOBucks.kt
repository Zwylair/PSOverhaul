package zwylair.pisskaland_overhaul.items

import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.ItemGroup
import net.minecraft.registry.RegistryKey
import zwylair.pisskaland_overhaul.ModObject.ModItem
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.itemgroups.ModItemGroups

class SVOBucks : ModItem(FabricItemSettings().maxCount(10)) {
    override var id = PSO.id("svobucks")
    override var itemGroupAddTo: RegistryKey<ItemGroup>? = ModItemGroups.PSO_ITEMGROUP_REG_KEY
}
