package zwylair.pisskaland_overhaul.blocks

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Blocks
import net.minecraft.item.ItemGroup
import net.minecraft.registry.RegistryKey
import zwylair.pisskaland_overhaul.ModObject
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.itemgroups.ModItemGroups

class AltarGlass : ModObject.ModBlock(FabricBlockSettings.copy(Blocks.GLASS)) {
    override var id = PSO.id("altar_glass")
    override var itemGroupAddTo: RegistryKey<ItemGroup>? = ModItemGroups.PSO_ITEMGROUP_REG_KEY
}
