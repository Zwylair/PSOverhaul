package zwylair.pisskaland_overhaul.blocks

import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import zwylair.pisskaland_overhaul.ModObject.GlintedBlockItem
import zwylair.pisskaland_overhaul.ModObject.ModBlock
import zwylair.pisskaland_overhaul.ModObject.ModBlockItem
import zwylair.pisskaland_overhaul.PSO

object ModBlocks {
    lateinit var EXAMPLE_BLOCK: ModBlock

    fun init() {
        EXAMPLE_BLOCK = register(ExampleBlock())
    }

    private fun register(block: ModBlock): ModBlock {
        val blockItem: ModBlockItem = if (block.glinted) GlintedBlockItem(block, FabricItemSettings()) else ModBlockItem(block, FabricItemSettings())

        Registry.register(Registries.BLOCK, block.id, block)
        Registry.register(Registries.ITEM, block.id, blockItem)
        PSO.LOGGER.info("")
        PSO.LOGGER.info("{} Block, BlockItem registered", block.translationKey)
        if (block.itemGroupAddTo != null) addToGroup(blockItem)

        return block
    }

    private fun addToGroup(blockItem: ModBlockItem) {
        ItemGroupEvents.modifyEntriesEvent(blockItem.itemGroupAddTo).register(ItemGroupEvents.ModifyEntries { it.add(blockItem) })
        PSO.LOGGER.info("{} BlockItem added into {} ItemGroup", blockItem.translationKey, blockItem.itemGroupAddTo?.value?.toTranslationKey())
    }
}
