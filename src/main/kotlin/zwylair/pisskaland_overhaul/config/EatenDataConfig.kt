package zwylair.pisskaland_overhaul.config

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.mojang.authlib.GameProfile
import net.minecraft.item.Item
import zwylair.pisskaland_overhaul.config.Config.eatenData

object EatenDataConfig {
    fun incrementFoodCounter(profile: GameProfile, item: Item) {
        var playerEatData = eatenData.getAsJsonObject(profile.name)
        if (playerEatData == null) {
            playerEatData = JsonObject()
            eatenData.add(profile.name, playerEatData)
        }

        var foodEatenTimes = playerEatData.get(item.translationKey)
        if (foodEatenTimes == null)
            foodEatenTimes = JsonPrimitive(0)

        playerEatData.add(item.translationKey, JsonPrimitive(foodEatenTimes.asInt + 1))
        eatenData.add(profile.name, playerEatData)
    }

    fun getEatenCount(profile: GameProfile, item: Item): Int? {
        var playerData = eatenData.getAsJsonObject(profile.name)
        playerData?: return null
        var eatenItemData = playerData.get(item.translationKey)
        eatenItemData?: return null
        return eatenItemData.asInt
    }

    fun isOnceEaten(profile: GameProfile, item: Item): Boolean {
        var eatenCount = getEatenCount(profile, item)?: return false
        return eatenCount == 1
    }

    fun isTwiceEaten(profile: GameProfile, item: Item): Boolean {
        var eatenCount = getEatenCount(profile, item)?: return false
        return eatenCount == 2
    }
}
