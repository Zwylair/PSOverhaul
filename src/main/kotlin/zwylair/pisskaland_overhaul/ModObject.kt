package zwylair.pisskaland_overhaul

import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier

object ModObject {
    open class ModBlock(settings: Settings) : Block(settings) {
        open lateinit var id: Identifier
        open var itemGroupAddTo: RegistryKey<ItemGroup>? = null
        open var glinted: Boolean = false

        fun id(id: Identifier): ModBlock {
            this.id = id
            return this
        }

        fun itemGroupAddTo(itemGroup: RegistryKey<ItemGroup>): ModBlock {
            this.itemGroupAddTo = itemGroup
            return this
        }

        fun glinted(state: Boolean): ModBlock {
            this.glinted = state
            return this
        }
    }

    open class ModItem(settings: Settings) : Item(settings) {
        open lateinit var id: Identifier
        open var itemGroupAddTo: RegistryKey<ItemGroup>? = null

        fun id(id: Identifier): ModItem {
            this.id = id
            return this
        }

        fun itemGroupAddTo(itemGroup: RegistryKey<ItemGroup>): ModItem {
            this.itemGroupAddTo = itemGroup
            return this
        }
    }

    open class ModBlockItem(block: ModBlock, settings: Settings) : BlockItem(block, settings) {
        open var id: Identifier = block.id
        open var itemGroupAddTo: RegistryKey<ItemGroup>? = block.itemGroupAddTo
        open var glinted: Boolean = block.glinted

        fun id(id: Identifier): ModBlockItem {
            this.id = id
            return this
        }

        fun itemGroupAddTo(itemGroup: RegistryKey<ItemGroup>): ModBlockItem {
            this.itemGroupAddTo = itemGroup
            return this
        }

        fun glinted(state: Boolean): ModBlockItem {
            this.glinted = state
            return this
        }
    }

    open class GlintedItem(settings: Settings) : ModItem(settings) {
        override fun hasGlint(stack: ItemStack?): Boolean { return true }
    }

    open class GlintedBlockItem(block: ModBlock, settings: Settings) : ModBlockItem(block, settings) {
        override fun hasGlint(stack: ItemStack?): Boolean { return true }
    }
}
