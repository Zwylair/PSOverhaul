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
import zwylair.pisskaland_overhaul.config.EatenSubConfig
import zwylair.pisskaland_overhaul.config.PraySubConfig

object DayChangeTicker {
    private val callbacks = mutableListOf<(ServerWorld) -> Unit>()
    private var lastDaySeen: Long = -1

    fun register() {
        PSO.LOGGER.info("Registering ServerTick events")

        subscribe(::chowDownWiper)
        subscribe(::prayerCheck)
        ServerTickEvents.END_WORLD_TICK.register(::onTick)
    }

    fun subscribe(callback: (ServerWorld) -> Unit) {
        callbacks.add(callback)
    }

    private fun onTick(world: ServerWorld) {
        val currentDay = world.timeOfDay / 24000
        if (currentDay != lastDaySeen) {
            lastDaySeen = currentDay
            callbacks.forEach { it(world) }
        }
    }

    private fun prayerCheck(world: ServerWorld) {
        for (player in world.server.playerManager.playerList) {
            val profile = player.gameProfile

            if (PraySubConfig.didPray(profile)) {
                val prayStreak = PraySubConfig.getPrayStreak(profile) + 1
                PraySubConfig.updateNotPrayedStreak(profile, 0)
                PraySubConfig.updatePrayStreak(profile, prayStreak)

                if (prayStreak >= PraySubConfig.getDaysToBecomeDevout() && !PraySubConfig.isDevout(profile)) {
                    PraySubConfig.makeDevout(profile)
                    player.sendMessage(
                        Text.translatable("${PSO.MODID}.pray.became_a_devout").formatted(Formatting.DARK_AQUA)
                    )
                }

                if (PraySubConfig.isDevout(profile)) {
                    player.addStatusEffect(StatusEffectInstance(StatusEffects.SATURATION, 15 * 20))
                    player.addExperience(7)
                    world.playSound(
                        player, BlockPos.ofFloored(player.pos),
                        SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.AMBIENT
                    )
                    player.sendMessage(
                        Text.translatable("${PSO.MODID}.pray.prayed_day_as_devout").formatted(Formatting.GREEN)
                    )
                }

                continue
            }

            PraySubConfig.updatePrayStreak(profile, 0)
            player.sendMessage(
                Text.translatable("${PSO.MODID}.pray.pray_streak_was_zeroed").formatted(Formatting.DARK_GRAY)
            )

            if (PraySubConfig.isDevout(profile)) {
                val notPrayedDays = PraySubConfig.getNotPrayedStreak(profile) + 1
                PraySubConfig.updateNotPrayedStreak(profile, notPrayedDays)

                if (notPrayedDays >= PraySubConfig.getMaxDaysWithoutPraying()) {
                    player.addStatusEffect(StatusEffectInstance(StatusEffects.POISON, 10 * 20))
                    player.sendMessage(
                        Text.translatable("${PSO.MODID}.pray.too_many_days_without_praying").formatted(Formatting.RED)
                    )
                }
            }
        }

        PraySubConfig.resetAllPrays()
    }

    private fun chowDownWiper(world: ServerWorld) {
        EatenSubConfig.wipe()
    }
}