package zwylair.pisskaland_overhaul.config

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import zwylair.pisskaland_overhaul.config.Config.denyListData
import zwylair.pisskaland_overhaul.config.Config.saveConfig

object DenyListConfig {
    fun addToDenyList(itemId: String, reward: Int) {
        denyListData.add(itemId, JsonPrimitive(reward))
        saveConfig()
    }

    fun removeFromDenyList(itemId: String) {
        denyListData.remove(itemId)
        saveConfig()
    }

    fun isAlreadyInDenyList(itemId: String): Boolean {
        return denyListData.get(itemId) != null
    }

    fun getDenyList(): Map<String, Int> {
        return denyListData.entrySet().associate { it.key to it.value.asInt }
    }
}
