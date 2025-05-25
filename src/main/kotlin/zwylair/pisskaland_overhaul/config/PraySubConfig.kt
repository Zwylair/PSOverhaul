package zwylair.pisskaland_overhaul.config

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.mojang.authlib.GameProfile

object PraySubConfig : SubConfig() {
    override val fullConfigProperty = "eatenData"
    private const val MAX_DAYS_WITHOUT_PRAYING = 7
    private const val DAYS_TO_BECOME_A_DEVOUT = 7

    private fun getData(profileName: String): JsonObject? {
        return jsonData.getAsJsonObject(profileName)
    }

    private fun getData(profile: GameProfile): JsonObject? {
        return getData(profile.name)
    }

    private fun updateData(profileName: String, newData: JsonObject) {
        jsonData.add(profileName, newData)
        Config.save()
    }

    private fun updateData(profile: GameProfile, newData: JsonObject) {
        updateData(profile.name, newData)
    }

    fun didPray(profile: GameProfile): Boolean {
        val prayData = getData(profile)?: return false
        return prayData.get("prayed")?.asBoolean?: false
    }

    fun pray(profile: GameProfile) {
        val prayData = getData(profile)?: JsonObject()
        prayData.add("prayed", JsonPrimitive(true))
        updateData(profile, prayData)
    }

    fun resetAllPrays() {
        for (key in jsonData.keySet()) {
            val prayData = getData(key)?: continue
            prayData.add("prayed", JsonPrimitive(false))
            updateData(key, prayData)
        }
    }

    fun getNotPrayedStreak(profile: GameProfile): Int {
        val prayData = getData(profile)?: return 0
        return prayData.get("not_prayed_streak").asInt
    }

    fun updateNotPrayedStreak(profile: GameProfile, value: Int) {
        val prayData = getData(profile)?: JsonObject()
        prayData.add("not_prayed_streak", JsonPrimitive(value))
        updateData(profile, prayData)
    }

    fun getPrayStreak(profile: GameProfile): Int {
        val prayData = getData(profile)?: return 0
        return prayData.get("pray_streak").asInt
    }

    fun updatePrayStreak(profile: GameProfile, value: Int) {
        val prayData = getData(profile)?: JsonObject()
        prayData.add("pray_streak", JsonPrimitive(value))
        updateData(profile, prayData)
    }

    fun isDevout(profile: GameProfile): Boolean {
        val prayData = getData(profile)?: JsonObject()
        return prayData.get("is_devout")?.asBoolean?: false
    }

    fun makeDevout(profile: GameProfile) {
        val prayData = getData(profile)?: JsonObject()
        prayData.add("is_devout", JsonPrimitive(true))
        updateData(profile, prayData)
    }

    fun getMaxDaysWithoutPraying(): Int {
        return MAX_DAYS_WITHOUT_PRAYING
    }

    fun getDaysToBecomeDevout(): Int {
        return DAYS_TO_BECOME_A_DEVOUT
    }
}