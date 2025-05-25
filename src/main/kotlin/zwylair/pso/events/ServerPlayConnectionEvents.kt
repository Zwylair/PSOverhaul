package zwylair.pso.events

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import zwylair.pso.PSO
import zwylair.pso.Constants
import zwylair.pso.network.ModNetworking.sendServerVersion

object ServerPlayConnectionEvents {
    fun register() {
        PSO.LOGGER.info("Registering ServerPlayConnection events")
        ServerPlayConnectionEvents.INIT.register(::sendVersionToServer)
    }

    private fun sendVersionToServer(handler: ServerPlayNetworkHandler, server: MinecraftServer) {
        sendServerVersion(handler.player, Constants.MOD_VERSION)
    }
}