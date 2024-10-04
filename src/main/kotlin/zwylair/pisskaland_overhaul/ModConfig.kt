package zwylair.pisskaland_overhaul

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mojang.authlib.GameProfile
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

object ModConfig {
    private val CONFIG_FILE = File("config/pso_storage.json")
    private val gson = Gson()
    private var moneyData = JsonObject()

    fun saveConfig() {
        val json = JsonObject()
        json.add("moneyData", moneyData)

        try {
            FileWriter(CONFIG_FILE).use { writer ->
                gson.toJson(json, writer)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadConfig() {
        PSO.LOGGER.info("Loading config")

        if (CONFIG_FILE.exists()) {
            try {
                FileReader(CONFIG_FILE).use { reader ->
                    val json = gson.fromJson(reader, JsonObject::class.java)
                    moneyData = json.getAsJsonObject("moneyData")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun updateMoneyAmount(playerGameProfile: GameProfile, moneyAmount: Int) {
        moneyData.addProperty(playerGameProfile.id.toString(), moneyAmount)
        saveConfig()
    }

    fun getMoneyAmount(playerGameProfile: GameProfile): Int {
        var stringMoneyAmount = moneyData.get(playerGameProfile.id.toString())
        return if (stringMoneyAmount == null) { 0 } else { stringMoneyAmount.asInt }
    }
}
