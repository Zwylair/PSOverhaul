package zwylair.pso.blocks

import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemGroup
import net.minecraft.registry.RegistryKey
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import zwylair.pso.ModObject
import zwylair.pso.PSO
import zwylair.pso.config.PraySubConfig
import zwylair.pso.itemgroups.ModItemGroups

class AltarGlass : ModObject.ModBlock(Settings.copy(Blocks.GLASS).nonOpaque()) {
    override var id = PSO.id("altar_glass")
    override var itemGroupAddTo: RegistryKey<ItemGroup>? = ModItemGroups.PSO_ITEM_GROUP_REG_KEY

    private val altarStructure = listOf(
        // Layer -2
        BlockPos(-1, -2, -1) to Blocks.GOLD_BLOCK,
        BlockPos( 0, -2, -1) to Blocks.PINK_WOOL,
        BlockPos( 1, -2, -1) to Blocks.GOLD_BLOCK,
        BlockPos(-1, -2,  0) to Blocks.PINK_WOOL,
        BlockPos( 0, -2,  0) to Blocks.GOLD_BLOCK,
        BlockPos( 1, -2,  0) to Blocks.PINK_WOOL,
        BlockPos(-1, -2,  1) to Blocks.GOLD_BLOCK,
        BlockPos( 0, -2,  1) to Blocks.PINK_WOOL,
        BlockPos( 1, -2,  1) to Blocks.GOLD_BLOCK,
        // Layer -1
        BlockPos( 0, -1, -1) to Blocks.PINK_WOOL,
        BlockPos(-1, -1,  0) to Blocks.PINK_WOOL,
        BlockPos( 0, -1,  0) to Blocks.GOLD_BLOCK,
        BlockPos( 1, -1,  0) to Blocks.PINK_WOOL,
        BlockPos( 0, -1,  1) to Blocks.PINK_WOOL,
        // Layer 0
        BlockPos( 0,  0, -1) to Blocks.PINK_WOOL,
        BlockPos(-1, 0,  0) to Blocks.PINK_WOOL,
        BlockPos( 0,  0,  0) to this,
        BlockPos( 1,  0,  0) to Blocks.PINK_WOOL,
        BlockPos( 0,  0,  1) to Blocks.PINK_WOOL
    )

    private fun checkStructure(world: World, pos: BlockPos): Boolean {
        for ((offset, expectedBlock) in altarStructure) {
            val checkPos = pos.add(offset)
            if (world.getBlockState(checkPos).block != expectedBlock) {
                world.scheduleBlockTick(pos, this, 20)
                return false
            }
        }
        return true
    }

    private fun sendPrayFeedback(
        world: World,
        player: PlayerEntity,
        sound: net.minecraft.sound.SoundEvent,
        msgKey: String,
        format: Formatting
    ) {
        world.playSound(null, BlockPos.ofFloored(player.pos), sound, SoundCategory.AMBIENT)
        player.sendMessage(Text.translatable("${PSO.MODID}.$msgKey").formatted(format))
    }

    @Deprecated("https://www.reddit.com/r/fabricmc/comments/r8zi36/deprecation/")
    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        if (!checkStructure(world, pos)) return

        val players = world.getPlayers {
            it.squaredDistanceTo(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5) < 100
        }

        if (players.isNotEmpty()) {
            val player = players[0]
            val advancement = world.server.advancementLoader.get(Identifier("${PSO.MODID}/make_altar"))
            advancement?.let {
                val progress = player.advancementTracker.getProgress(it)
                for (criterion in progress.unobtainedCriteria) {
                    player.advancementTracker.grantCriterion(it, criterion)
                }
            }
        } else {
            world.scheduleBlockTick(pos, this, 20)
        }
    }

    @Deprecated("https://www.reddit.com/r/fabricmc/comments/r8zi36/deprecation/")
    override fun onBlockAdded(state: BlockState, world: World, pos: BlockPos, oldState: BlockState, notify: Boolean) {
        if (!world.isClient) {
            world.scheduleBlockTick(pos, this, 0)
        }
    }

    @Deprecated("https://www.reddit.com/r/fabricmc/comments/r8zi36/deprecation/")
    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (world.isClient) return ActionResult.SUCCESS

        if (!checkStructure(world, pos)) {
            sendPrayFeedback(world, player, SoundEvents.BLOCK_LARGE_AMETHYST_BUD_PLACE, "pray.failed", Formatting.RED)
            return ActionResult.SUCCESS
        }

        if (PraySubConfig.didPray(player.gameProfile)) {
            sendPrayFeedback(world, player, SoundEvents.BLOCK_LARGE_AMETHYST_BUD_PLACE, "pray.already", Formatting.RED)
            return ActionResult.SUCCESS
        }

        PraySubConfig.pray(player.gameProfile)
        sendPrayFeedback(world, player, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, "pray.success", Formatting.DARK_GREEN)

        return ActionResult.SUCCESS
    }
}