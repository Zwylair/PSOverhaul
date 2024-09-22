package zwylair.pisskaland_overhaul.blocks

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Blocks
import net.minecraft.item.ItemGroup
import net.minecraft.registry.RegistryKey
import zwylair.pisskaland_overhaul.ModObject.ModBlock
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.itemgroups.ModItemGroups

class ElectrifiedCopperBlock : ModBlock(
    FabricBlockSettings.copy(Blocks.COPPER_BLOCK)
        .luminance { 7 }
) {
    override var id = PSO.id("electrified_copper_block")
    override var itemGroupAddTo: RegistryKey<ItemGroup>? = ModItemGroups.PSO_ITEMGROUP_REG_KEY
}
