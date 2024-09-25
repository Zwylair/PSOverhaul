package zwylair.pisskaland_overhaul.items

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import zwylair.pisskaland_overhaul.ModObject.ModItem
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.itemgroups.ModItemGroups.PSO_ITEMGROUP_REG_KEY

object ModItems {
    lateinit var WALLET: ModItem
    lateinit var SVOBUCKS: ModItem
    var SERVER_ANTHEM_HORN = ServerAnthemHorn()

    fun init() {
        WALLET = register(WalletItem())
        SVOBUCKS = register(SVOBucks())
        register(SERVER_ANTHEM_HORN, PSO.id("server_anthem_horn"), PSO_ITEMGROUP_REG_KEY)
    }

    private fun register(item: ModItem): ModItem {
        Registry.register(Registries.ITEM, item.id, item)
        PSO.LOGGER.info("")
        PSO.LOGGER.info("{} Item registered", item.translationKey)
        if (item.itemGroupAddTo != null) addToGroup(item)
        return item
    }

    private fun register(item: Item, itemId: Identifier, itemGroupAddTo: RegistryKey<ItemGroup>?): Item {
        Registry.register(Registries.ITEM, itemId, item)
        PSO.LOGGER.info("")
        PSO.LOGGER.info("{} Item registered", item.translationKey)
        if (itemGroupAddTo != null) addToGroup(item, itemGroupAddTo)
        return item
    }

    private fun addToGroup(item: ModItem) {
        ItemGroupEvents.modifyEntriesEvent(item.itemGroupAddTo).register(ItemGroupEvents.ModifyEntries { it.add(item) })
        PSO.LOGGER.info("{} Item added into {} ItemGroup", item.translationKey, item.itemGroupAddTo?.value?.toTranslationKey())
    }

    private fun addToGroup(item: Item, itemGroupAddTo: RegistryKey<ItemGroup>) {
        ItemGroupEvents.modifyEntriesEvent(itemGroupAddTo).register(ItemGroupEvents.ModifyEntries { it.add(item) })
        PSO.LOGGER.info("{} Item added into {} ItemGroup", item.translationKey, itemGroupAddTo.value?.toTranslationKey())
    }
}
