package zwylair.pisskaland_overhaul.network

import com.mojang.authlib.GameProfile
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.item.ItemStack
import zwylair.pisskaland_overhaul.ModConfig
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.items.WalletItem

object ModNetworking {
    val WALLET_MONEY_CHANGE_PACKET = PSO.id("wallet_change_amount")
    val WALLET_CLEAR_SLOT_PACKET = PSO.id("wallet_clear_slot")
    val WALLET_SYNC_MONEY_WITH_SERVER = PSO.id("wallet_sync_money_with_server")
    val CLIENT_WALLET_SYNC_MONEY_WITH_SERVER = PSO.id("client_wallet_sync_money_with_server")

    fun registerServer() {
        PSO.LOGGER.info("Trying to register networking module")

        ServerPlayNetworking.registerGlobalReceiver(WALLET_MONEY_CHANGE_PACKET) { server, player, handler, buf, responseSender ->
            val walletItemStack = buf.readItemStack()
            val newAmount = buf.readInt()

            server.execute {
                if (walletItemStack.item is WalletItem) {
                    val wallet = walletItemStack.item as WalletItem
                    wallet.setMoney(newAmount)
                }

                ModConfig.updateMoneyData(player!!.uuid, newAmount)
            }
        }
        ServerPlayNetworking.registerGlobalReceiver(WALLET_CLEAR_SLOT_PACKET) { server, player, handler, buf, responseSender ->
            val playerUUID = buf.readUuid()
            val slotIndex = buf.readInt()

            server.execute {
                server.playerManager.playerList.forEach {
                    if (it.uuid == playerUUID) {
                        val slotStack = it.inventory.getStack(slotIndex)
                        slotStack.decrement(slotStack.count)
                        return@execute
                    }
                }
            }
        }
        ServerPlayNetworking.registerGlobalReceiver(WALLET_SYNC_MONEY_WITH_SERVER) { server, player, handler, buf, responseSender ->
            PSO.LOGGER.info("server was received WALLET_SYNC_MONEY_WITH_SERVER")
            val playerUUID = buf.readUuid()

            server.execute {
                val targetPlayer = server.playerManager.playerList.firstOrNull {
                    it.uuid == playerUUID
                }

                if (targetPlayer != null) {
                    val buf = PacketByteBufs.create()
                    val loadedMoneyAmount = ModConfig.getPlayerMoneyAmount(targetPlayer.uuid)

                    val inventory = targetPlayer.inventory

                    for (i in 0 until inventory.size()) {
                        val itemStack = inventory.getStack(i)
                        if (itemStack.item is WalletItem) {
                            (itemStack.item as WalletItem).setMoney(loadedMoneyAmount)
                            PSO.LOGGER.info("[server] got money amount: $loadedMoneyAmount")
                            break
                        }
                    }

                    buf.writeInt(loadedMoneyAmount)
                    ServerPlayNetworking.send(player, CLIENT_WALLET_SYNC_MONEY_WITH_SERVER, buf)
                }
            }
        }
    }

    fun registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(CLIENT_WALLET_SYNC_MONEY_WITH_SERVER) { client, handler, buf, packetSender ->
            PSO.LOGGER.info("client was received CLIENT_WALLET_SYNC_MONEY_WITH_SERVER")
            val newMoneyAmount = buf.readInt()
            val inventory = client.player!!.inventory

            for (i in 0 until inventory.size()) {
                val itemStack = inventory.getStack(i)
                if (itemStack.item is WalletItem) {
                    (itemStack.item as WalletItem).setMoney(newMoneyAmount)
                    PSO.LOGGER.info("[client] got money amount: $newMoneyAmount")
                    break
                }
            }
        }
    }

    fun sendWalletMoneyChangePacket(stack: ItemStack, newAmount: Int) {
        val buf = PacketByteBufs.create()
        buf.writeItemStack(stack)
        buf.writeInt(newAmount)

        ClientPlayNetworking.send(WALLET_MONEY_CHANGE_PACKET, buf)
    }

    fun sendCleanInventorySlotPacket(playerGameProfile: GameProfile, slotIndex: Int) {
        val buf = PacketByteBufs.create()
        buf.writeUuid(playerGameProfile.id)
        buf.writeInt(slotIndex)

        ClientPlayNetworking.send(WALLET_CLEAR_SLOT_PACKET, buf)
    }

    fun forceSyncWalletMoneyAmount(playerGameProfile: GameProfile) {
        val buf = PacketByteBufs.create()
        buf.writeUuid(playerGameProfile.id)

        ClientPlayNetworking.send(WALLET_SYNC_MONEY_WITH_SERVER, buf)
    }
}
