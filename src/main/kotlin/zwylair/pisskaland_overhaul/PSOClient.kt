package zwylair.pisskaland_overhaul

import net.fabricmc.api.ClientModInitializer
import zwylair.pisskaland_overhaul.network.ModNetworking

class PSOClient : ClientModInitializer {
    override fun onInitializeClient() {
        ModNetworking.registerClient()
    }
}
