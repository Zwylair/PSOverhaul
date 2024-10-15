package zwylair.pisskaland_overhaul.blocks

import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.item.ItemGroup
import net.minecraft.registry.RegistryKey
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import zwylair.pisskaland_overhaul.ModObject
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.itemgroups.ModItemGroups

class AltarGlass : ModObject.ModBlock(Settings.copy(Blocks.GLASS).nonOpaque()) {
    override var id = PSO.id("altar_glass")
    override var itemGroupAddTo: RegistryKey<ItemGroup>? = ModItemGroups.PSO_ITEMGROUP_REG_KEY

    private val firstStructureLayer = listOf(
        listOf(Blocks.GOLD_BLOCK, Blocks.PINK_WOOL, Blocks.GOLD_BLOCK),
        listOf(Blocks.PINK_WOOL, Blocks.GOLD_BLOCK, Blocks.PINK_WOOL),
        listOf(Blocks.GOLD_BLOCK, Blocks.PINK_WOOL, Blocks.GOLD_BLOCK)
    )
    private val secondStructureLayer = listOf(
        listOf(Blocks.AIR, Blocks.PINK_WOOL, Blocks.AIR),
        listOf(Blocks.PINK_WOOL, Blocks.GOLD_BLOCK, Blocks.PINK_WOOL),
        listOf(Blocks.AIR, Blocks.PINK_WOOL, Blocks.AIR)
    )
    private val thirdStructureLayer = listOf(
        listOf(Blocks.AIR, Blocks.PINK_WOOL, Blocks.AIR),
        listOf(Blocks.PINK_WOOL, this, Blocks.PINK_WOOL),
        listOf(Blocks.AIR, Blocks.PINK_WOOL, Blocks.AIR)
    )
    private val structureLayers = listOf(
        firstStructureLayer, secondStructureLayer, thirdStructureLayer
    )

    @Deprecated("Deprecated")
    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: net.minecraft.util.math.random.Random) {
        PSO.LOGGER.info("Custom block at {} is ticking!", pos)

        var layerCounter = 0
        for (layer in structureLayers) {
            layerCounter++

            var y = -(structureLayers.size - layerCounter)
            var z = -1

            for (line in layer) {
                var x = -1

                for (block in line) {
                    if (block != world.getBlockState(pos.add(x, y, z)).block) {
                        PSO.LOGGER.info(
                            "(AltarGlass tick) Wrong block:\n" +
                            "\tOn ${pos.add(x, y, z).toShortString()} (${world.getBlockState(BlockPos(x, y, z)).block.name.string})\n" +
                            "\tEternal indexes:\n" +
                            "\t\tlayer: ${structureLayers.indexOf(layer)}\n" +
                            "\t\tline: ${layer.indexOf(line)}\n" +
                            "\t\tblock: ${line.indexOf(block)}\n" +
                            "\t${block.name.string} expected"
                        )
                        world.setBlockState(pos.add(x, y, z), Blocks.DIAMOND_BLOCK.defaultState)
                        world.scheduleBlockTick(pos, this, 20)
                        return
                    }
                    x++
                }
                z++
            }
        }

        PSO.LOGGER.info("Structure checked successfully!")

        world.scheduleBlockTick(pos, this, 20)
    }

    @Deprecated("Deprecated")
    override fun onBlockAdded(state: BlockState, world: World, pos: BlockPos, oldState: BlockState, notify: Boolean) {
        if (!world.isClient) { world.scheduleBlockTick(pos, this, 0) }
    }
}
