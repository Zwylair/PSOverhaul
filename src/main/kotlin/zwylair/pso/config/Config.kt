package zwylair.pso.config

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import zwylair.pso.Constants

object Config {
    private val gson = Gson()
    private val subConfigs = ArrayList<SubConfig>()

    fun addSubConfig(configInstance: SubConfig) {
        subConfigs.add(configInstance)
    }

    fun save() {
        val json = JsonObject()

        subConfigs.forEach { it.save( json::add) }

        try {
            FileWriter(Constants.CONFIG_FILE).use {
                gson.toJson(json, it)
            }
        }
        catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun load() {
        if (!Constants.CONFIG_FILE.exists()) return

        try {
            FileReader(Constants.CONFIG_FILE).use { reader ->
                val json = gson.fromJson(reader, JsonObject::class.java)
                subConfigs.forEach { it.load( json::getAsJsonObject) }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}