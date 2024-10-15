package zwylair.pisskaland_overhaul.config

import com.mojang.authlib.GameProfile
import zwylair.pisskaland_overhaul.config.Config.moneyData
import zwylair.pisskaland_overhaul.config.Config.saveConfig

object MoneyConfig {
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
}