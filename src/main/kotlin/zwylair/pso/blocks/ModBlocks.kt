package zwylair.pso.blocks

import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import zwylair.pso.ModObject.GlintedBlockItem
import zwylair.pso.ModObject.ModBlock
import zwylair.pso.ModObject.ModBlockItem
import zwylair.pso.PSO

object ModBlocks {
    lateinit var ALTAR_GLASS: ModBlock

    fun init() {
        ALTAR_GLASS = register(AltarGlass())
    }

    private fun register(block: ModBlock): ModBlock {
        val blockItem: ModBlockItem = if (block.glinted) GlintedBlockItem(block, FabricItemSettings()) else ModBlockItem(block, FabricItemSettings())

        Registry.register(Registries.BLOCK, block.id, block)
        Registry.register(Registries.ITEM, block.id, blockItem)
        PSO.LOGGER.info("{} Block, BlockItem registered", block.translationKey)
        if (block.itemGroupAddTo != null) addToGroup(blockItem)

        return block
    }

    private fun addToGroup(blockItem: ModBlockItem) {
        ItemGroupEvents.modifyEntriesEvent(blockItem.itemGroupAddTo).register(ItemGroupEvents.ModifyEntries { it.add(blockItem) })
        PSO.LOGGER.info("{} BlockItem added into {} ItemGroup", blockItem.translationKey, blockItem.itemGroupAddTo?.value?.toTranslationKey())
    }
}