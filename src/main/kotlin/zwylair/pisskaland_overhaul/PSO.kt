package zwylair.pisskaland_overhaul

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.util.Identifier
import zwylair.pisskaland_overhaul.blocks.ModBlocks
import zwylair.pisskaland_overhaul.commands.ConfigManage
import zwylair.pisskaland_overhaul.commands.DenyList
import zwylair.pisskaland_overhaul.itemgroups.ModItemGroups
import zwylair.pisskaland_overhaul.items.ModItems
import zwylair.pisskaland_overhaul.commands.MoneyManage
import zwylair.pisskaland_overhaul.config.Config
import zwylair.pisskaland_overhaul.config.DenyListSubConfig
import zwylair.pisskaland_overhaul.config.EatenSubConfig
import zwylair.pisskaland_overhaul.config.MoneySubConfig
import zwylair.pisskaland_overhaul.config.PraySubConfig
import zwylair.pisskaland_overhaul.events.DayChangeTicker
import zwylair.pisskaland_overhaul.events.SlotActions
import zwylair.pisskaland_overhaul.events.PlayerBlockBreak
import zwylair.pisskaland_overhaul.events.PlayerPickupItem
import zwylair.pisskaland_overhaul.soundevents.ModSoundEvents
import zwylair.pisskaland_overhaul.events.ServerPlayConnectionEvents
import zwylair.pisskaland_overhaul.events.ServerLivingEntity
import zwylair.pisskaland_overhaul.network.ModNetworking

class PSO : ModInitializer {
    companion object {
        const val MODID = "pisskaland_overhaul"
        val LOGGER: Logger = LoggerFactory.getLogger(MODID)
        fun id(path: String) = Identifier(MODID, path)
    }

    override fun onInitialize() {
        LOGGER.info("PisskaLandOverhaul initializing...")

        registerContent()
        registerCommands()
        registerEvents()
        loadConfigs()

        LOGGER.info("PisskaLandOverhaul initialized.")
    }

    private fun registerContent() {
        ModSoundEvents.init()
        ModItemGroups.init()
        ModBlocks.init()
        ModItems.init()
    }

    private fun registerCommands() {
        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, _ ->
            listOf(
                { MoneyManage.register(dispatcher) },
                { ConfigManage.register(dispatcher) },
                { DenyList.register(dispatcher, registryAccess) }
            ).forEach { it() }
        }
    }

    private fun registerEvents() {
        listOf(
            ServerPlayConnectionEvents::register,
            ServerLivingEntity::register,
            DayChangeTicker::register,
            ModNetworking::registerServer,
            PlayerBlockBreak::register,
            PlayerPickupItem::register,
            SlotActions::register
        ).forEach { it() }
    }

    private fun loadConfigs() {
        listOf(
            DenyListSubConfig,
            EatenSubConfig,
            MoneySubConfig,
            PraySubConfig
        ).forEach(Config::addSubConfig)

        Config.load()
    }
}