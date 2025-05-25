package zwylair.pisskaland_overhaul.config

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.mojang.authlib.GameProfile

object MoneySubConfig : SubConfig() {
    override val fullConfigProperty = "money_data"

    private fun getData(profile: GameProfile): JsonObject? {
        return jsonData.getAsJsonObject(profile.name)
    }

    private fun updateData(profile: GameProfile, newData: JsonObject) {
        jsonData.add(profile.name, newData)
        Config.save()
    }

    fun getBalance(profile: GameProfile): Int {
        val moneyData = getData(profile)?: JsonObject()
        return moneyData.asInt
    }

    fun updateBalance(profile: GameProfile, value: Int) {
        val moneyData = getData(profile)?: JsonObject()
        moneyData.add(profile.name, JsonPrimitive(value))
        updateData(profile, moneyData)
    }
}