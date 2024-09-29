package zwylair.pisskaland_overhaul

import com.google.gson.Gson
import com.google.gson.JsonObject
import zwylair.pisskaland_overhaul.PSO.Companion.LOGGER
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.UUID

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
        LOGGER.info("Loading config")

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

    fun updateMoneyData(playerUUID: UUID, moneyAmount: Int) {
        moneyData.addProperty(playerUUID.toString(), moneyAmount)
        saveConfig()
    }

    fun getPlayerMoneyAmount(playerUUID: UUID): Int {
        var stringMoneyAmount = moneyData.get(playerUUID.toString())
        return if (stringMoneyAmount == null) { 0 } else { stringMoneyAmount.asInt }
    }
}
