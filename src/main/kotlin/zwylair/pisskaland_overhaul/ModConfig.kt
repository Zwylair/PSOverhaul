package zwylair.pisskaland_overhaul

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.mojang.authlib.GameProfile
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

object ModConfig {
    const val MOD_VERSION: String = "1.1.0"
    val COMPATIBLE_SERVER_MOD_VERSIONS = listOf(
        MOD_VERSION,
    )
    const val MAX_DAYS_WITHOUT_PRAYING = 7
    private val CONFIG_FILE = File("config/pso_storage.json")
    private val gson = Gson()
    private var moneyData = JsonObject()
    private var prayData = JsonObject()

    fun saveConfig() {
        val json = JsonObject()
        json.add("moneyData", moneyData)
        json.add("prayData", prayData)

        try { FileWriter(CONFIG_FILE).use { writer -> gson.toJson(json, writer) } }
        catch (e: IOException) { e.printStackTrace() }
    }

    fun loadConfig() {
        PSO.LOGGER.info("Loading config")

        if (CONFIG_FILE.exists()) {
            try {
                FileReader(CONFIG_FILE).use { reader ->
                    val json = gson.fromJson(reader, JsonObject::class.java)
                    var gotMoneyData: JsonObject? = json.getAsJsonObject("moneyData")
                    var gotPrayData: JsonObject? = json.getAsJsonObject("prayData")

                    if (gotMoneyData != null) { moneyData = gotMoneyData }
                    if (gotPrayData != null) { prayData = gotPrayData }
                }
            } catch (e: IOException) { e.printStackTrace() }
        }
    }

    fun updateMoneyAmount(playerGameProfile: GameProfile, moneyAmount: Int) {
        moneyData.addProperty(playerGameProfile.name.toString(), moneyAmount)
        saveConfig()
    }

    fun getMoneyAmount(playerGameProfile: GameProfile): Int {
        var stringMoneyAmount = moneyData.get(playerGameProfile.name.toString())
        var stringMoneyAmountByUUID = moneyData.get(playerGameProfile.id.toString())

        // code that converts the save key from uuid to player nickname
        if (stringMoneyAmount == null) {
            val moneyAmount = if (stringMoneyAmountByUUID == null) 0 else stringMoneyAmountByUUID.asInt

            moneyData.remove(playerGameProfile.id.toString())
            updateMoneyAmount(playerGameProfile, moneyAmount)
            stringMoneyAmount = stringMoneyAmountByUUID
        }

        return if (stringMoneyAmount == null) 0 else stringMoneyAmount.asInt
    }

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
}
