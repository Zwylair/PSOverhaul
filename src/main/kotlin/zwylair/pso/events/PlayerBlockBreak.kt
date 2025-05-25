package zwylair.pso.events

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
import zwylair.pso.PSO
import zwylair.pso.items.ModItems.SVOBUCKS

object PlayerBlockBreak {
    private val blockCoinDropChance = mapOf(
        Blocks.GOLD_ORE to listOf(1, 30)
    )

    fun register() {
        PSO.LOGGER.info("Registering PlayerBlockBreak events")
        PlayerBlockBreakEvents.AFTER.register(::breakBlock)
    }

    private fun randRange(range: List<Int>): Int {
        return Random.nextInt(range[0], range[1])
    }

    private fun spawnItem(world: World, pos: BlockPos) {
        val coinItemStuck = ItemStack(SVOBUCKS, 1)
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

        for ((block, range) in blockCoinDropChance) {
            if (state.block == block && randRange(range) == 1)
                spawnItem(world, pos)
        }
    }
}