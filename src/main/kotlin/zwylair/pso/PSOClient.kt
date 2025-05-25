package zwylair.pso

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.render.RenderLayer
import zwylair.pso.blocks.ModBlocks
import zwylair.pso.network.ModNetworking

@Environment(EnvType.CLIENT)
class PSOClient : ClientModInitializer {
    override fun onInitializeClient() {
        ModNetworking.registerClient()
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ALTAR_GLASS, RenderLayer.getCutout())
    }
}
