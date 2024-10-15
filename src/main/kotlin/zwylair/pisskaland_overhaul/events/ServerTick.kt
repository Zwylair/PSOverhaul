package zwylair.pisskaland_overhaul.events

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.config.PrayConfig
import zwylair.pisskaland_overhaul.items.ModItems.SVOBUCKS

object ServerTick {
    private val forbiddenItems = mapOf(
        Items.ELYTRA.translationKey to 20
    )

    const val CHECK_FOR_FORBIDDEN_ITEMS_TIMEOUT = 1 * 20
    var checkForForbiddenItemsTimeoutCount = 0
    const val CHECK_FOR_PRAY_TIMEOUT = 3 * 20
    var checkForPrayCount = 0
    var notFinishedDayTicks: Long = -1

    fun register() {
        PSO.LOGGER.info("Trying to register ServerTick events")

        ServerTickEvents.END_WORLD_TICK.register(::checkForForbiddenItems)
        ServerTickEvents.END_WORLD_TICK.register(::prayerCheck)
    }

    private fun checkForForbiddenItems(world: ServerWorld) {
        if (checkForForbiddenItemsTimeoutCount < CHECK_FOR_FORBIDDEN_ITEMS_TIMEOUT) {
            checkForForbiddenItemsTimeoutCount += 1
            return
        }

        checkForForbiddenItemsTimeoutCount = 0

        world.players.forEach {
            if (it is ServerPlayerEntity) {
                val inventory = it.inventory

                for (slot in 0 until inventory.size()) {
                    val stack = inventory.getStack(slot)
                    val itemTranslationKey = stack.item.translationKey

                    if (itemTranslationKey in forbiddenItems.keys) {
                        inventory.removeStack(slot)
                        inventory.insertStack(ItemStack(SVOBUCKS).copyWithCount(forbiddenItems[itemTranslationKey]!!))
                    }
                }
            }
        }
    }

    private fun prayerCheck(world: ServerWorld) {
        if (checkForPrayCount < CHECK_FOR_PRAY_TIMEOUT) {
            checkForPrayCount += 1
            return
        } else { checkForPrayCount = 0 }

        val localNotFinishedDayTicks = world.timeOfDay % 24000

        if (localNotFinishedDayTicks > notFinishedDayTicks) {
            notFinishedDayTicks = localNotFinishedDayTicks
            return
        }

        notFinishedDayTicks = -1

        world.server.playerManager.playerList.forEach {
            if (PrayConfig.didPlayerPray(it.gameProfile)) {
                PrayConfig.setPlayerNotPrayedCount(it.gameProfile, 0)
                val prayStreak = PrayConfig.incrementPlayerPrayStreak(it.gameProfile)

                if (
                    prayStreak >= PrayConfig.DAYS_TO_BECOME_A_DEVOUT &&
                    !PrayConfig.isPlayerDevout(it.gameProfile)
                ) {
                    PrayConfig.makePlayerDevout(it.gameProfile)
                    it.sendMessage(
                        Text
                            .translatable("${PSO.MODID}.pray.became_a_devout")
                            .formatted(Formatting.DARK_AQUA)
                    )
                }

                if (PrayConfig.isPlayerDevout(it.gameProfile)) {
                    it.addStatusEffect(StatusEffectInstance(StatusEffects.SATURATION, 15 * 20))
                    it.addExperience(7)
                    world.playSound(
                        it, BlockPos.ofFloored(it.pos),
                        SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.AMBIENT
                    )
                    it.sendMessage(
                        Text
                            .translatable("${PSO.MODID}.pray.prayed_day_as_devout")
                            .formatted(Formatting.GREEN)
                    )
                }
            } else {
                PrayConfig.resetPlayerPrayStreak(it.gameProfile)
                if (!PrayConfig.isPlayerDevout(it.gameProfile)) {
                    return
                }

                it.sendMessage(
                    Text
                        .translatable("${PSO.MODID}.pray.pray_streak_was_zeroed")
                        .formatted(Formatting.DARK_GRAY)
                )

                val notPrayedDays = PrayConfig.increasePlayerNotPrayedCount(it.gameProfile)
                if (notPrayedDays >= PrayConfig.MAX_DAYS_WITHOUT_PRAYING) {
                    it.addStatusEffect(StatusEffectInstance(StatusEffects.POISON, 10 * 20))
                    it.sendMessage(
                        Text
                            .translatable("${PSO.MODID}.pray.too_many_days_without_praying")
                            .formatted(Formatting.RED)
                    )
                    PrayConfig.setPlayerNotPrayedCount(it.gameProfile, 0)
                }
            }
        }

        PrayConfig.resetAllPrays()
    }
}
