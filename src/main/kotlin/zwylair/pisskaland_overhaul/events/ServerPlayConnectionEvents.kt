package zwylair.pisskaland_overhaul.events

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.Constants
import zwylair.pisskaland_overhaul.network.ModNetworking.sendServerVersion

object ServerPlayConnectionEvents {
    fun register() {
        PSO.LOGGER.info("Registering ServerPlayConnection events")
        ServerPlayConnectionEvents.INIT.register(::sendVersionToServer)
    }

    private fun sendVersionToServer(handler: ServerPlayNetworkHandler, server: MinecraftServer) {
        sendServerVersion(handler.player, Constants.MOD_VERSION)
    }
}