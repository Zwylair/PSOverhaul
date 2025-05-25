package zwylair.pisskaland_overhaul

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.util.Identifier
import zwylair.pisskaland_overhaul.blocks.ModBlocks
import zwylair.pisskaland_overhaul.commands.ConfigManage
import zwylair.pisskaland_overhaul.commands.ItemsDenyList
import zwylair.pisskaland_overhaul.itemgroups.ModItemGroups
import zwylair.pisskaland_overhaul.items.ModItems
import zwylair.pisskaland_overhaul.commands.MoneyManage
import zwylair.pisskaland_overhaul.config.Config
import zwylair.pisskaland_overhaul.events.HotbarSwapEvents
import zwylair.pisskaland_overhaul.events.PlayerBlockBreak
import zwylair.pisskaland_overhaul.events.PlayerPickupItem
import zwylair.pisskaland_overhaul.soundevents.ModSoundEvents
import zwylair.pisskaland_overhaul.events.ServerPlayConnectionEvents
import zwylair.pisskaland_overhaul.events.ServerLivingEntity
import zwylair.pisskaland_overhaul.events.ServerTick
import zwylair.pisskaland_overhaul.network.ModNetworking

class PSO : ModInitializer {
    companion object {
        const val MODID = "pisskaland_overhaul"
        val LOGGER: Logger = LoggerFactory.getLogger(MODID)
        fun id(path: String) = Identifier(MODID, path)
    }

    override fun onInitialize() {
        LOGGER.info("PisskaLandOverhaul has started initialization...")

        ModSoundEvents.init()
        ModItemGroups.init()
        ModBlocks.init()
        ModItems.init()
        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, _ ->
            MoneyManage.register(dispatcher)
            ConfigManage.register(dispatcher)
            ItemsDenyList.register(dispatcher, registryAccess)
        }
        ServerPlayConnectionEvents.register()
        ServerLivingEntity.register()
        ServerTick.register()
        ModNetworking.registerServer()
        PlayerBlockBreak.register()
        PlayerPickupItem.register()
        HotbarSwapEvents.register()
        Config.loadConfig()

        LOGGER.info("PisskaLandOverhaul has been initialized!")
    }
}
