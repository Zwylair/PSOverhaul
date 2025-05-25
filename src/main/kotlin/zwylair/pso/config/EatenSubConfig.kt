package zwylair.pso.config

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.mojang.authlib.GameProfile
import net.minecraft.item.Item

object EatenSubConfig : SubConfig() {
    override val fullConfigProperty = "eaten_data"

    private fun getEatData(profile: GameProfile): JsonObject? {
        return jsonData.getAsJsonObject(profile.name)
    }

    fun incrementFoodCounter(profile: GameProfile, item: Item) {
        var playerEatData = getEatData(profile)

        if (playerEatData == null) {
            jsonData.add(profile.name, JsonObject())
            playerEatData = getEatData(profile)!!
        }

        val foodEatenTimes = playerEatData.get(item.translationKey)?: JsonPrimitive(0)

        playerEatData.add(item.translationKey, JsonPrimitive(foodEatenTimes.asInt + 1))
        jsonData.add(profile.name, playerEatData)
        Config.save()
    }

    fun getEatenCount(profile: GameProfile, item: Item): Int? {
        val playerData = getEatData(profile)?: return null
        val eatenItemData = playerData.get(item.translationKey)?: return null
        return eatenItemData.asInt
    }

    fun isOnceEaten(profile: GameProfile, item: Item): Boolean {
        val eatenCount = getEatenCount(profile, item)?: return false
        return eatenCount == 1
    }

    fun isTwiceEaten(profile: GameProfile, item: Item): Boolean {
        val eatenCount = getEatenCount(profile, item)?: return false
        return eatenCount == 2
    }

    fun wipe() {
        jsonData = JsonObject()
        Config.save()
    }
}