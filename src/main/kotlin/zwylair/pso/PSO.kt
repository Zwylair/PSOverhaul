package zwylair.pso

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.util.Identifier
import zwylair.pso.blocks.ModBlocks
import zwylair.pso.commands.ConfigManage
import zwylair.pso.commands.DenyList
import zwylair.pso.itemgroups.ModItemGroups
import zwylair.pso.items.ModItems
import zwylair.pso.commands.MoneyManage
import zwylair.pso.config.Config
import zwylair.pso.config.DenyListSubConfig
import zwylair.pso.config.EatenSubConfig
import zwylair.pso.config.MoneySubConfig
import zwylair.pso.config.PraySubConfig
import zwylair.pso.events.DayChangeTicker
import zwylair.pso.events.SlotActions
import zwylair.pso.events.PlayerBlockBreak
import zwylair.pso.events.PlayerPickupItem
import zwylair.pso.soundevents.ModSoundEvents
import zwylair.pso.events.ServerPlayConnectionEvents
import zwylair.pso.events.ServerLivingEntity
import zwylair.pso.network.ModNetworking

class PSO : ModInitializer {
    companion object {
        const val MODID = "pso"
        val LOGGER: Logger = LoggerFactory.getLogger(MODID)
        fun id(path: String) = Identifier(MODID, path)
    }

    override fun onInitialize() {
        LOGGER.info("PSO initializing...")

        registerContent()
        registerCommands()
        registerEvents()
        loadConfigs()

        LOGGER.info("PSO initialized.")
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