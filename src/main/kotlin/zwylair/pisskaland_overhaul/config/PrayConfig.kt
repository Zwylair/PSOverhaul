package zwylair.pisskaland_overhaul.config

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.mojang.authlib.GameProfile
import zwylair.pisskaland_overhaul.config.Config.prayData
import zwylair.pisskaland_overhaul.config.Config.saveConfig

object PrayConfig {
    const val MAX_DAYS_WITHOUT_PRAYING = 7
    const val DAYS_TO_BECOME_A_DEVOUT = 7

    fun didPlayerPray(playerGameProfile: GameProfile): Boolean {
        val playerPrayInfo = prayData.getAsJsonObject(playerGameProfile.name)?: return false
        val playerPrayed = playerPrayInfo.get("prayed")?.asBoolean?: return false
        return playerPrayed
    }

    fun prayPlayer(playerGameProfile: GameProfile) {
        val playerPrayInfo = prayData.getAsJsonObject(playerGameProfile.name)?: JsonObject()
        playerPrayInfo.add("prayed", JsonPrimitive(true))
        prayData.add(playerGameProfile.name, playerPrayInfo)
        saveConfig()
    }

    fun resetAllPrays() {
        for (key in prayData.keySet()) {
            val playerPrayInfo = prayData.getAsJsonObject(key)?: continue
            playerPrayInfo.add("prayed", JsonPrimitive(false))
            prayData.add(key, playerPrayInfo)
        }
        saveConfig()
    }

    /**
     * Increases player's days without praying with 1.
     *
     * @return Count of player's days without praying.
     */
    fun increasePlayerNotPrayedCount(playerGameProfile: GameProfile): Int {
        val playerPrayInfo = prayData.getAsJsonObject(playerGameProfile.name)?: JsonObject()
        val notPrayedCount = JsonPrimitive(
            (playerPrayInfo.get("not_prayed_count")?.asInt?: 0) + 1
        )
        playerPrayInfo.add("not_prayed_count", notPrayedCount)
        prayData.add(playerGameProfile.name, playerPrayInfo)
        saveConfig()

        return notPrayedCount.asInt
    }

    fun setPlayerNotPrayedCount(playerGameProfile: GameProfile, count: Int) {
        val playerPrayInfo = prayData.getAsJsonObject(playerGameProfile.name)?: JsonObject()
        playerPrayInfo.add("not_prayed_count", JsonPrimitive(count))
        prayData.add(playerGameProfile.name, playerPrayInfo)
        saveConfig()
    }

    fun resetPlayerPrayStreak(playerGameProfile: GameProfile) {
        val playerPrayInfo = prayData.getAsJsonObject(playerGameProfile.name)?: JsonObject()
        playerPrayInfo.add("pray_streak", JsonPrimitive(0))
    }

    /**
     * Increases player's praying day streak with 1.
     *
     * @return Count of player's praying day streak.
     */
    fun incrementPlayerPrayStreak(playerGameProfile: GameProfile): Int {
        val playerPrayInfo = prayData.getAsJsonObject(playerGameProfile.name)?: JsonObject()
        val prayStreak = JsonPrimitive((playerPrayInfo.get("pray_streak")?.asInt?: 0) + 1)
        playerPrayInfo.add("pray_streak", prayStreak)
        prayData.add(playerGameProfile.name, playerPrayInfo)
        saveConfig()

        return prayStreak.asInt
    }

    fun isPlayerDevout(playerGameProfile: GameProfile): Boolean {
        val playerPrayInfo = prayData.getAsJsonObject(playerGameProfile.name)?: JsonObject()
        return playerPrayInfo.get("isDevout")?.asBoolean == true
    }

    fun makePlayerDevout(playerGameProfile: GameProfile) {
        val playerPrayInfo = prayData.getAsJsonObject(playerGameProfile.name)?: JsonObject()
        playerPrayInfo.add("isDevout", JsonPrimitive(true))
        prayData.add(playerGameProfile.name, playerPrayInfo)
        saveConfig()
    }
}
