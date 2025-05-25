package zwylair.pisskaland_overhaul.config

import com.google.gson.JsonPrimitive

object DenyListSubConfig : SubConfig() {
    override val fullConfigProperty = "denylist_data"

    fun append(itemId: String, reward: Int) {
        jsonData.add(itemId, JsonPrimitive(reward))
        Config.save()
    }

    fun remove(itemId: String) {
        jsonData.remove(itemId)
        Config.save()
    }

    fun has(itemId: String): Boolean {
        return jsonData.get(itemId) != null
    }

    fun getReward(itemId: String): Int? {
        return jsonData.get(itemId).asInt
    }
}