package zwylair.pisskaland_overhaul.blocks

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Blocks
import net.minecraft.item.ItemGroup
import net.minecraft.registry.RegistryKey
import zwylair.pisskaland_overhaul.ModObject.ModBlock
import zwylair.pisskaland_overhaul.PSO

class ExampleBlock : ModBlock(FabricBlockSettings.copy(Blocks.STONE)) {
    override var id = PSO.id("example_block")
    override var itemGroupAddTo: RegistryKey<ItemGroup>? = null
}
