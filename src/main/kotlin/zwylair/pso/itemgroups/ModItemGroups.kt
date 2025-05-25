package zwylair.pso.itemgroups

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import zwylair.pso.PSO
import zwylair.pso.items.ModItems

object ModItemGroups {
    lateinit var PSO_ITEM_GROUP_ID: Identifier
    lateinit var PSO_ITEM_GROUP_REG_KEY: RegistryKey<ItemGroup>
    lateinit var PSO_ITEM_GROUP: ItemGroup

    fun init() {
        PSO_ITEM_GROUP_ID = PSO.id("main_item_group")
        PSO_ITEM_GROUP = FabricItemGroup.builder()
            .icon { ItemStack(ModItems.SVOBUCKS) }
            .displayName(Text.translatable("itemGroup.${PSO_ITEM_GROUP_ID}"))
            .build()

        PSO_ITEM_GROUP_REG_KEY = RegistryKey.of(RegistryKeys.ITEM_GROUP, PSO_ITEM_GROUP_ID)

        register(PSO_ITEM_GROUP, PSO_ITEM_GROUP_ID)
    }

    private fun register(itemGroup: ItemGroup, id: Identifier) {
        Registry.register(Registries.ITEM_GROUP, id, itemGroup)
        PSO.LOGGER.info("{} ItemGroup registered", id.toTranslationKey())
    }
}
