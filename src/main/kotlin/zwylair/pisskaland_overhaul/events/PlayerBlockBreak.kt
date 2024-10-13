package zwylair.pisskaland_overhaul.events

import kotlin.random.Random
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.items.ModItems.SVOBUCKS

object PlayerBlockBreak {
    val blockCoinDropChance = mapOf(
        Blocks.STONE to listOf(1, 300),
        Blocks.DEEPSLATE to listOf(1, 200)
    )

    fun register() {
        PSO.LOGGER.info("Trying to register PlayerBlockBreak events")

        PlayerBlockBreakEvents.AFTER.register(::breakBlock)
    }

    private fun spawnItem(world: World, pos: BlockPos) {
        val coinItemStuck = ItemStack(SVOBUCKS).copyWithCount(1)
        val (x, y, z) = listOf(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
        val coinEntity = ItemEntity(world, x, y, z, coinItemStuck)
        world.spawnEntity(coinEntity)
    }

    private fun breakBlock(
        world: World,
        player: PlayerEntity,
        pos: BlockPos,
        state: BlockState,
        blockEntity: BlockEntity?
    ) {
        if (world.isClient) return

        blockCoinDropChance.forEach {
            val (block, range) = it
            if (state.block == block)
                if (Random.nextInt(range[0], range[1]) == 1) spawnItem(world, pos)
        }
    }
}
