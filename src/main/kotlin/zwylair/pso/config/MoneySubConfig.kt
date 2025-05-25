package zwylair.pso.config

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
        val moneyData = getData(profile)?: return 0
        return moneyData.getAsJsonPrimitive("balance")?.asInt?: 0
    }

    fun updateBalance(profile: GameProfile, value: Int) {
        val moneyData = getData(profile)?: JsonObject()
        moneyData.add("balance", JsonPrimitive(value))
        updateData(profile, moneyData)
    }
}