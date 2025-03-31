package zwylair.pisskaland_overhaul.events

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.config.EatenDataConfig
import zwylair.pisskaland_overhaul.config.PrayConfig

object ServerTick {
    const val CHECK_FOR_PRAY_TIMEOUT = 3 * 20
    var checkForPrayCount = 0
    var notFinishedDayTicks: Long = -1

    fun register() {
        PSO.LOGGER.info("Trying to register ServerTick events")

        ServerTickEvents.END_WORLD_TICK.register(::chowDownWiper)
        ServerTickEvents.END_WORLD_TICK.register(::prayerCheck)
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

    private fun chowDownWiper(world: ServerWorld) {
        if (checkForPrayCount < CHECK_FOR_PRAY_TIMEOUT) { return }
        val localNotFinishedDayTicks = world.timeOfDay % 24000
        if (localNotFinishedDayTicks > notFinishedDayTicks) { return }
        EatenDataConfig.wipe()
    }
}
