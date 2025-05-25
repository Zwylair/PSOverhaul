package zwylair.pso.network

import com.mojang.authlib.GameProfile
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import zwylair.pso.PSO
import zwylair.pso.Constants
import zwylair.pso.config.MoneySubConfig
import zwylair.pso.items.ModItems.SVOBUCKS
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object ModNetworking {
    private val pendingCallbacks = ConcurrentHashMap<UUID, (Int) -> Unit>()

    val INCREMENT_WALLET_MONEY = PSO.id("increment_wallet_money")
    val UPDATE_ITEMSTACK_NBT = PSO.id("update_itemstack_nbt")
    val GET_COINS = PSO.id("get_coins")
    val FETCH_PLAYER_MONEY_AMOUNT = PSO.id("fetch_player_money_amount")
    val CLEAR_SLOT_PACKET = PSO.id("clear_slot_packet")
    val SEND_SERVER_MOD_VERSION_PACKET = PSO.id("send_server_mod_version")

    fun registerServer() {
        PSO.LOGGER.info("Registering server networking")

        serverHandler(INCREMENT_WALLET_MONEY, ::handleIncrementWallet)
        serverHandler(UPDATE_ITEMSTACK_NBT, ::handleUpdateItemNbt)
        serverHandler(GET_COINS, ::handleGetCoins)
        serverHandler(CLEAR_SLOT_PACKET, ::handleClearSlot)
        serverHandler(FETCH_PLAYER_MONEY_AMOUNT, ::handleFetchMoney)
    }

    fun registerClient() {
        PSO.LOGGER.info("Registering client networking")

        ClientPlayNetworking.registerGlobalReceiver(FETCH_PLAYER_MONEY_AMOUNT, ::handleMoneyResponse)
        ClientPlayNetworking.registerGlobalReceiver(SEND_SERVER_MOD_VERSION_PACKET, ::handleVersionMismatch)
    }

    private fun handleIncrementWallet(server: MinecraftServer, player: ServerPlayerEntity, buf: PacketByteBuf) {
        val profile = buf.readGameProfile()
        val amount = buf.readInt()
        server.execute {
            val newBalance = MoneySubConfig.getBalance(profile) + amount
            MoneySubConfig.updateBalance(profile, newBalance)
        }
    }

    private fun handleUpdateItemNbt(server: MinecraftServer, player: ServerPlayerEntity, buf: PacketByteBuf) {
        val itemStack = buf.readItemStack()
        val nbt = buf.readNbt()
        server.execute {
            itemStack.nbt = nbt
        }
    }

    private fun handleGetCoins(server: MinecraftServer, player: ServerPlayerEntity, buf: PacketByteBuf) {
        val profile = buf.readGameProfile()
        val amount = buf.readInt()
        server.execute {
            findPlayer(server, profile)?.let {
                val coins = ItemStack(SVOBUCKS).copyWithCount(amount)
                it.inventory.offerOrDrop(coins)
            }
        }
    }

    private fun handleClearSlot(server: MinecraftServer, player: ServerPlayerEntity, buf: PacketByteBuf) {
        val profile = buf.readGameProfile()
        val slot = buf.readInt()
        server.execute {
            findPlayer(server, profile)?.let {
                it.inventory.removeStack(slot)
                it.inventory.markDirty()
            }
        }
    }

    private fun handleFetchMoney(server: MinecraftServer, player: ServerPlayerEntity, buf: PacketByteBuf) {
        val profile = buf.readGameProfile()
        server.execute {
            val target = findPlayer(server, profile) ?: return@execute
            val amount = MoneySubConfig.getBalance(profile)
            val responseBuf = PacketByteBufs.create().apply { writeInt(amount) }
            ServerPlayNetworking.send(target, FETCH_PLAYER_MONEY_AMOUNT, responseBuf)
        }
    }

    private fun handleMoneyResponse(
        client: MinecraftClient,
        handler: ClientPlayNetworkHandler,
        buf: PacketByteBuf,
        sender: PacketSender
    ) {
        val amount = buf.readInt()
        val uuid = client.player?.gameProfile?.id
        client.execute {
            uuid?.let {
                pendingCallbacks[it]?.invoke(amount)
                pendingCallbacks.remove(it)
            }
        }
    }

    private fun handleVersionMismatch(
        client: MinecraftClient,
        handler: ClientPlayNetworkHandler,
        buf: PacketByteBuf,
        sender: PacketSender
    ) {
        val serverVersion = buf.readString()
        client.execute {
            if (serverVersion !in Constants.COMPATIBLE_SERVER_MOD_VERSIONS) {
                handler.connection.disconnect(
                    Text.translatable("${PSO.MODID}.version_mismatched")
                        .formatted(Formatting.BOLD)
                        .formatted(Formatting.BLUE)
                )
                handler.connection.handleDisconnection()
            }
        }
    }

    fun sendIncrementMoney(profile: GameProfile, amount: Int) = sendPacket(INCREMENT_WALLET_MONEY) {
        writeGameProfile(profile)
        writeInt(amount)
    }

    fun sendGetCoins(profile: GameProfile, amount: Int) = sendPacket(GET_COINS) {
        writeGameProfile(profile)
        writeInt(amount)
    }

    fun sendUpdateItemNbt(item: ItemStack, nbt: NbtCompound) = sendPacket(UPDATE_ITEMSTACK_NBT) {
        writeItemStack(item)
        writeNbt(nbt)
    }

    fun sendClearSlot(profile: GameProfile, slot: Int) = sendPacket(CLEAR_SLOT_PACKET) {
        writeGameProfile(profile)
        writeInt(slot)
    }

    fun sendFetchMoneyRequest(player: PlayerEntity, callback: (Int) -> Unit) {
        pendingCallbacks[player.gameProfile.id] = callback
        sendPacket(FETCH_PLAYER_MONEY_AMOUNT) {
            writeGameProfile(player.gameProfile)
        }
    }

    fun sendServerVersion(player: ServerPlayerEntity, version: String) {
        val buf = PacketByteBufs.create().apply { writeString(version) }
        ServerPlayNetworking.send(player, SEND_SERVER_MOD_VERSION_PACKET, buf)
    }

    private fun serverHandler(id: Identifier, handler: (MinecraftServer, ServerPlayerEntity, PacketByteBuf) -> Unit) {
        ServerPlayNetworking.registerGlobalReceiver(id) { server, player, _, buf, _ ->
            handler(server, player, buf)
        }
    }

    private fun sendPacket(id: Identifier, build: PacketByteBuf.() -> Unit) {
        val buf = PacketByteBufs.create().apply(build)
        ClientPlayNetworking.send(id, buf)
    }

    private fun findPlayer(server: MinecraftServer, profile: GameProfile): ServerPlayerEntity? {
        return server.playerManager.playerList.firstOrNull { it.gameProfile == profile }
    }
}