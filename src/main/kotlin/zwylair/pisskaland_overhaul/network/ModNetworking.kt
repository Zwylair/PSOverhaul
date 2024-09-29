package zwylair.pisskaland_overhaul.network

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.items.WalletItem

object ModNetworking {
    val WALLET_MONEY_CHANGE_PACKET = PSO.id("wallet_change_amount")
    val WALLET_CLEAR_SLOT_PACKET = PSO.id("wallet_clear_slot")

    fun register() {
        PSO.LOGGER.info("Trying to register networking module")

        ServerPlayNetworking.registerGlobalReceiver(WALLET_MONEY_CHANGE_PACKET) { server, player, handler, buf, responseSender ->
            val walletItemStack = buf.readItemStack()
            val newAmount = buf.readInt()

            server.execute {
                if (walletItemStack.item is WalletItem) {
                    val wallet = walletItemStack.item as WalletItem
                    wallet.setMoney(newAmount)
                }
            }
        }
        ServerPlayNetworking.registerGlobalReceiver(WALLET_CLEAR_SLOT_PACKET) { server, player, handler, buf, responseSender ->
            val playerGameProfile = buf.readGameProfile()
            val slotIndex = buf.readInt()

            server.execute {
                server.playerManager.playerList.forEach {
                    if (it.gameProfile == playerGameProfile) {
                        val slotStack = it.inventory.getStack(slotIndex)
                        slotStack.decrement(slotStack.count)
                        return@execute
                    }
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

    fun sendCleanInventorySlotPacket(player: PlayerEntity, slotIndex: Int) {
        val buf = PacketByteBufs.create()
        buf.writeGameProfile(player.gameProfile)
        buf.writeInt(slotIndex)

        ClientPlayNetworking.send(WALLET_CLEAR_SLOT_PACKET, buf)
    }
}
