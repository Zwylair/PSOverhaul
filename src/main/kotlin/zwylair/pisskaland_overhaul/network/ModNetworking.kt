package zwylair.pisskaland_overhaul.network

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import com.mojang.authlib.GameProfile
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import zwylair.pisskaland_overhaul.ModConfig
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.items.ModItems.SVOBUCKS

object ModNetworking {
    val INCREMENT_WALLET_MONEY = PSO.id("increment_wallet_money")
    val UPDATE_ITEMSTACK_NBT = PSO.id("update_itemstack_nbt")
    val GET_COINS = PSO.id("get_coins")
    val FETCH_PLAYER_MONEY_AMOUNT = PSO.id("fetch_player_money_amount")
    val CLEAR_SLOT_PACKET = PSO.id("clear_slot_packet")
    val SEND_SERVER_MOD_VERSION_PACKET = PSO.id("send_server_mod_version")
    private val pendingCallbacks = ConcurrentHashMap<UUID, (Int) -> Unit>()

    fun registerServer() {
        PSO.LOGGER.info("Trying to register server networking module")

        ServerPlayNetworking.registerGlobalReceiver(INCREMENT_WALLET_MONEY) { server, player, handler, buf, responseSender ->
            PSO.LOGGER.info("[PSO] received INCREMENT_WALLET_MONEY")

            val playerGameProfile = buf.readGameProfile()
            val amount = buf.readInt()

            server.execute {
                ModConfig.updateMoneyAmount(
                    playerGameProfile,
                    ModConfig.getMoneyAmount(playerGameProfile) + amount
                )
            }
        }
        ServerPlayNetworking.registerGlobalReceiver(UPDATE_ITEMSTACK_NBT) { server, player, handler, buf, responseSender ->
            PSO.LOGGER.info("[PSO] received UPDATE_ITEMSTACK_NBT")

            val itemStack = buf.readItemStack()
            val nbt = buf.readNbt()

            server.execute {
                itemStack.nbt = nbt
//                player.inventory.markDirty()
                PSO.LOGGER.info("[PSO] itemStack nbt: ${itemStack.nbt?.getInt("moneyAmount")}")
            }
        }
        ServerPlayNetworking.registerGlobalReceiver(GET_COINS) { server, player, handler, buf, responseSender ->
            PSO.LOGGER.info("[PSO] received GET_COINS")
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
        ServerPlayNetworking.registerGlobalReceiver(CLEAR_SLOT_PACKET) { server, player, handler, buf, responseSender ->
            PSO.LOGGER.info("[PSO] received CLEAR_SLOT_PACKET")

            val playerGameProfile = buf.readGameProfile()
            val slotIndex = buf.readInt()
            server.execute {
                server.playerManager.playerList.forEach {
                    if (it.gameProfile == playerGameProfile) {
                        it.inventory.removeStack(slotIndex)
                        it.inventory.markDirty()
                        return@execute
                    }
                }
            }
        }
        ServerPlayNetworking.registerGlobalReceiver(FETCH_PLAYER_MONEY_AMOUNT) { server, player, handler, buf, responseSender ->
            PSO.LOGGER.info("[PSO] received FETCH_PLAYER_MONEY_AMOUNT")
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
            PSO.LOGGER.info("[PSO Client] received FETCH_PLAYER_MONEY_AMOUNT")
            val moneyAmount = buf.readInt()
            val playerUUID = client.player?.gameProfile?.id

            client.execute {
                playerUUID?.let {
                    pendingCallbacks[it]?.invoke(moneyAmount)
                    pendingCallbacks.remove(it)
                }
            }
        }
        ClientPlayNetworking.registerGlobalReceiver(SEND_SERVER_MOD_VERSION_PACKET) { client, handler, buf, responseSender ->
            val serverModVersion = buf.readString()

            PSO.LOGGER.info("[PSO Client] received SEND_SERVER_MOD_VERSION_PACKET")
            PSO.LOGGER.info("[PSO Client] serverVersion: $serverModVersion; compatibleServerVersions: ${ModConfig.COMPATIBLE_SERVER_MOD_VERSIONS}")

            client.execute {
                if (!ModConfig.COMPATIBLE_SERVER_MOD_VERSIONS.contains(serverModVersion)) {
                    handler.connection.disconnect(
                        Text.translatable("pisskaland_overhaul.version_mismatched")
                            .formatted(Formatting.BOLD)
                            .formatted(Formatting.BLUE)
                    )
                    handler.connection.handleDisconnection()
                }
            }
        }
    }

    fun sendIncrementMoneyPacket(
        playerGameProfile: GameProfile,
        amount: Int
    ) {
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

    fun sendUpdateItemStackNbtPacket(itemStack: ItemStack, nbt: NbtCompound) {
        val buf = PacketByteBufs.create()
        buf.writeItemStack(itemStack)
        buf.writeNbt(nbt)

        ClientPlayNetworking.send(UPDATE_ITEMSTACK_NBT, buf)
    }

    fun sendFetchMoneyRequest(player: PlayerEntity, callback: (Int) -> Unit) {
        val buf = PacketByteBufs.create()
        buf.writeGameProfile(player.gameProfile)

        pendingCallbacks[player.gameProfile.id] = callback

        ClientPlayNetworking.send(FETCH_PLAYER_MONEY_AMOUNT, buf)
    }

    fun sendClearSlotPacket(playerGameProfile: GameProfile, slotIndex: Int) {
        val buf = PacketByteBufs.create()
        buf.writeGameProfile(playerGameProfile)
        buf.writeInt(slotIndex)

        ClientPlayNetworking.send(CLEAR_SLOT_PACKET, buf)
    }

    fun sendServerVersionPacket(player: ServerPlayerEntity, serverModVersion: String) {
        val buf = PacketByteBufs.create()
        buf.writeString(serverModVersion)

        ServerPlayNetworking.send(player, SEND_SERVER_MOD_VERSION_PACKET, buf)
    }
}
