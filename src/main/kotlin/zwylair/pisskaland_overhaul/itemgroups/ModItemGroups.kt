package zwylair.pisskaland_overhaul.itemgroups

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.blocks.ModBlocks

object ModItemGroups {
    lateinit var PSO_ITEMGROUP_ID: Identifier
    lateinit var PSO_ITEMGROUP_REG_KEY: RegistryKey<ItemGroup>
    lateinit var PSO_ITEMGROUP: ItemGroup

    fun init() {
        PSO_ITEMGROUP_ID = PSO.id("main_item_group")
        PSO_ITEMGROUP = FabricItemGroup.builder()
            .icon { ItemStack(ModBlocks.ELECTRIFIED_COPPER_BLOCK) }
            .displayName(Text.translatable("itemGroup.${PSO_ITEMGROUP_ID}"))
            .build()

        PSO_ITEMGROUP_REG_KEY = RegistryKey.of(RegistryKeys.ITEM_GROUP, PSO_ITEMGROUP_ID)

        register(PSO_ITEMGROUP, PSO_ITEMGROUP_ID)
    }

    private fun register(itemGroup: ItemGroup, id: Identifier) {
        Registry.register(Registries.ITEM_GROUP, id, itemGroup)
        PSO.LOGGER.info("")
        PSO.LOGGER.info("{} ItemGroup registered", id.toTranslationKey())
    }
}
