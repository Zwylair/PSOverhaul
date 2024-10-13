package zwylair.pisskaland_overhaul

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.render.RenderLayer
import zwylair.pisskaland_overhaul.blocks.ModBlocks
import zwylair.pisskaland_overhaul.network.ModNetworking

@Environment(EnvType.CLIENT)
class PSOClient : ClientModInitializer {
    override fun onInitializeClient() {
        ModNetworking.registerClient()
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ALTAR_GLASS, RenderLayer.getCutout())
    }
}
