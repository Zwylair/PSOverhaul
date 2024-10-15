package zwylair.pisskaland_overhaul.config

import com.mojang.authlib.GameProfile
import zwylair.pisskaland_overhaul.config.Config.moneyData
import zwylair.pisskaland_overhaul.config.Config.saveConfig

object MoneyConfig {
    fun updateMoneyAmount(playerGameProfile: GameProfile, moneyAmount: Int) {
        moneyData.addProperty(playerGameProfile.name, moneyAmount)
        saveConfig()
    }

    fun getMoneyAmount(playerGameProfile: GameProfile): Int {
        var stringMoneyAmount = moneyData.get(playerGameProfile.name)
        var stringMoneyAmountByUUID = moneyData.get(playerGameProfile.id.toString())

        // code that converts the save key from uuid to player nickname
        if (stringMoneyAmount == null) {
            val moneyAmount = stringMoneyAmountByUUID?.asInt?: 0

            moneyData.remove(playerGameProfile.id.toString())
            updateMoneyAmount(playerGameProfile, moneyAmount)
            stringMoneyAmount = stringMoneyAmountByUUID
        }

        return stringMoneyAmount?.asInt?: 0
    }
}