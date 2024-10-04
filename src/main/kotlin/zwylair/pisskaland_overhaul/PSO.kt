package zwylair.pisskaland_overhaul

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.util.Identifier
import zwylair.pisskaland_overhaul.blocks.ModBlocks
import zwylair.pisskaland_overhaul.itemgroups.ModItemGroups
import zwylair.pisskaland_overhaul.items.ModItems
import zwylair.pisskaland_overhaul.commands.MoneyManage
import zwylair.pisskaland_overhaul.events.PlayerBlockBreak
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
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            MoneyManage.register(dispatcher)
        }
        LOGGER.info("")
        ServerPlayConnectionEvents.register()
        ServerLivingEntity.register()
        PlayerBlockBreak.register()
        ServerTick.register()
        ModNetworking.registerServer()
        ModConfig.loadConfig()

        LOGGER.info("")
        LOGGER.info("PisskaLandOverhaul has been initialized!")
        LOGGER.info("")
    }
}
