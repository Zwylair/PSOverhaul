package zwylair.pisskaland_overhaul.config

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.config.ModSettings.CONFIG_FILE

object Config {
    private val gson = Gson()
    var moneyData = JsonObject()
    var prayData = JsonObject()
    var denyListData = JsonObject()
    var eatenData = JsonObject()

    fun saveConfig() {
        val json = JsonObject()
        json.add("moneyData", moneyData)
        json.add("prayData", prayData)
        json.add("denyListData", denyListData)

        try { FileWriter(CONFIG_FILE).use { writer -> gson.toJson(json, writer) } }
        catch (e: IOException) { e.printStackTrace() }
    }

    fun loadConfig() {
        PSO.Companion.LOGGER.info("Loading config")

        if (CONFIG_FILE.exists()) {
            try {
                FileReader(CONFIG_FILE).use { reader ->
                    val json = gson.fromJson(reader, JsonObject::class.java)

                    var gotMoneyData: JsonObject? = json.getAsJsonObject("moneyData")
                    var gotPrayData: JsonObject? = json.getAsJsonObject("prayData")
                    var gotDenyListData: JsonObject? = json.getAsJsonObject("denyListData")

                    if (gotMoneyData != null) { moneyData = gotMoneyData }
                    if (gotPrayData != null) { prayData = gotPrayData }
                    if (gotDenyListData != null) { denyListData = gotDenyListData }
                }
            } catch (e: IOException) { e.printStackTrace() }
        }
    }
}
