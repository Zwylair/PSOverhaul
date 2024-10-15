package zwylair.pisskaland_overhaul.events

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import zwylair.pisskaland_overhaul.config.Config
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.config.ModSettings
import zwylair.pisskaland_overhaul.network.ModNetworking.sendServerVersionPacket

object ServerPlayConnectionEvents {
    fun register() {
        PSO.LOGGER.info("Trying to register ServerPlayConnectionEvents events")

        ServerPlayConnectionEvents.INIT.register(::verifyPSOVersionWithServer)
    }

    private fun verifyPSOVersionWithServer(handler: ServerPlayNetworkHandler, server: MinecraftServer) {
        sendServerVersionPacket(handler.player, ModSettings.MOD_VERSION)
    }
}
