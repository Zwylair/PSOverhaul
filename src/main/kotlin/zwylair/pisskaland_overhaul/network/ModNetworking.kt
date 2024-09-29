package zwylair.pisskaland_overhaul.network

import com.mojang.authlib.GameProfile
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import zwylair.pisskaland_overhaul.ModConfig
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.items.ModItems.SVOBUCKS
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object ModNetworking {
    val INCREMENT_WALLET_MONEY = PSO.id("increment_wallet_money")
    val GET_COINS = PSO.id("get_coins")
    val FETCH_PLAYER_MONEY_AMOUNT = PSO.id("fetch_player_money_amount")
    private val pendingCallbacks = ConcurrentHashMap<UUID, (Int) -> Unit>()

    fun registerServer() {
        PSO.LOGGER.info("Trying to register server networking module")

        ServerPlayNetworking.registerGlobalReceiver(INCREMENT_WALLET_MONEY) { server, player, handler, buf, responseSender ->
            PSO.LOGGER.info("INCREMENT_WALLET_MONEY")

            val playerGameProfile = buf.readGameProfile()
            val amount = buf.readInt()

            server.execute {
                ModConfig.updateMoneyAmount(
                    playerGameProfile,
                    ModConfig.getMoneyAmount(playerGameProfile) + amount
                )
            }
        }
        ServerPlayNetworking.registerGlobalReceiver(GET_COINS) { server, player, handler, buf, responseSender ->
            PSO.LOGGER.info("GET_COINS")
            val playerGameProfile = buf.readGameProfile()
            val amount = buf.readInt()

            server.execute {
                server.playerManager.playerList.forEach {
                    if (it.gameProfile == playerGameProfile) {
                        val coinItemStack = ItemStack(SVOBUCKS).copyWithCount(amount)
                        player.inventory.offerOrDrop(coinItemStack)
                        return@execute
                    }
                }
            }
        }
        ServerPlayNetworking.registerGlobalReceiver(FETCH_PLAYER_MONEY_AMOUNT) { server, player, handler, buf, responseSender ->
            PSO.LOGGER.info("SERVER FETCH_PLAYER_MONEY_AMOUNT")
            val playerGameProfile = buf.readGameProfile()

            server.execute {
                val targetPlayer = server.playerManager.playerList.firstOrNull { it.gameProfile == playerGameProfile }

                if (targetPlayer != null) {
                    val moneyAmount = ModConfig.getMoneyAmount(targetPlayer.gameProfile)

                    val responseBuf = PacketByteBufs.create()
                    responseBuf.writeInt(moneyAmount)

                    ServerPlayNetworking.send(targetPlayer, FETCH_PLAYER_MONEY_AMOUNT, responseBuf)
                }
            }
        }
    }

    fun registerClient() {
        PSO.LOGGER.info("Trying to register client networking module")

        ClientPlayNetworking.registerGlobalReceiver(FETCH_PLAYER_MONEY_AMOUNT) { client, handler, buf, responseSender ->
            PSO.LOGGER.info("CLIENT FETCH_PLAYER_MONEY_AMOUNT")
            val moneyAmount = buf.readInt()
            val playerUUID = client.player?.gameProfile?.id

            client.execute {
                playerUUID?.let {
                    pendingCallbacks[it]?.invoke(moneyAmount)
                    pendingCallbacks.remove(it)
                }
            }
        }
    }

    fun sendIncrementMoneyPacket(playerGameProfile: GameProfile, amount: Int) {
        val buf = PacketByteBufs.create()
        buf.writeGameProfile(playerGameProfile)
        buf.writeInt(amount)

        ClientPlayNetworking.send(INCREMENT_WALLET_MONEY, buf)
    }

    fun sendGetCoinsPacket(playerGameProfile: GameProfile, amount: Int) {
        val buf = PacketByteBufs.create()
        buf.writeGameProfile(playerGameProfile)
        buf.writeInt(amount)

        ClientPlayNetworking.send(GET_COINS, buf)
    }

    fun sendFetchMoneyRequest(player: PlayerEntity, callback: (Int) -> Unit) {
        val buf = PacketByteBufs.create()
        buf.writeGameProfile(player.gameProfile)

        pendingCallbacks[player.gameProfile.id] = callback

        ClientPlayNetworking.send(FETCH_PLAYER_MONEY_AMOUNT, buf)
    }
}
