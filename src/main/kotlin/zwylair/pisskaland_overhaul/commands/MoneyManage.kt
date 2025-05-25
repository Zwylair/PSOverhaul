package zwylair.pisskaland_overhaul.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.PSO.Companion.LOGGER
import zwylair.pisskaland_overhaul.config.MoneySubConfig
import zwylair.pisskaland_overhaul.items.ModItems
import zwylair.pisskaland_overhaul.items.SVOBucks

object MoneyManage {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        LOGGER.info("Registering MoneyManage commands")

        dispatcher.register(
            literal("pso").then(
                literal("svobucks")
                    .then(buildGiveInvCoins())
                    .then(buildClearInvCoins())
            ).then(
                literal("wallet")
                    .then(buildGiveWalletCoins())
                    .then(buildTakeWalletCoins())
                    .then(buildClearWallet())
            )
        )
    }

    // ----- $SVO -----

    private fun buildGiveInvCoins(): LiteralArgumentBuilder<ServerCommandSource> {
        return literal("give")
            .then(argument("amount", IntegerArgumentType.integer())
                .executes {
                    val amount = IntegerArgumentType.getInteger(it, "amount")
                    giveCoins(it, amount, null).code
                }
                .then(argument("player", EntityArgumentType.player())
                    .executes {
                        val amount = IntegerArgumentType.getInteger(it, "amount")
                        val player = EntityArgumentType.getPlayer(it, "player")
                        giveCoins(it, amount, player).code
                    }
                )
            )
    }

    private fun buildClearInvCoins(): LiteralArgumentBuilder<ServerCommandSource> {
        return literal("erase")
            .then(argument("player", EntityArgumentType.player())
                .executes {
                    takeInventoryCoins(it, EntityArgumentType.getPlayer(it, "player")).code
                }
            )
    }

    // ----- Wallet -----

    private fun buildGiveWalletCoins(): LiteralArgumentBuilder<ServerCommandSource> {
        return literal("give")
            .then(argument("amount", IntegerArgumentType.integer())
                .executes {
                    giveWalletCoins(it, IntegerArgumentType.getInteger(it, "amount"), null).code
                }
                .then(argument("player", EntityArgumentType.player())
                    .executes {
                        giveWalletCoins(
                            it,
                            IntegerArgumentType.getInteger(it, "amount"),
                            EntityArgumentType.getPlayer(it, "player")
                        ).code
                    }
                )
            )
    }

    private fun buildTakeWalletCoins(): LiteralArgumentBuilder<ServerCommandSource> {
        return literal("erase")
            .then(argument("player", EntityArgumentType.player())
                .executes {
                    clearWalletCoins(it, EntityArgumentType.getPlayer(it, "player")).code
                }
            )
    }

    private fun buildClearWallet(): LiteralArgumentBuilder<ServerCommandSource> {
        return literal("reduce")
            .then(argument("amount", IntegerArgumentType.integer())
                .executes {
                    takeWalletCoins(it, IntegerArgumentType.getInteger(it, "amount"), null).code
                }
                .then(argument("player", EntityArgumentType.player())
                    .executes {
                        takeWalletCoins(
                            it,
                            IntegerArgumentType.getInteger(it, "amount"),
                            EntityArgumentType.getPlayer(it, "player")
                        ).code
                    }
                )
            )
    }

    // ----- Functions -----

    private fun giveCoins(
        ctx: CommandContext<ServerCommandSource>,
        amount: Int,
        playerSendTo: PlayerEntity?
    ): CommandResult {
        val source = ctx.source
        val server = source.server
        val executorPlayer = source.player?: return CommandResult.PASS
        val inventory = playerSendTo?.inventory?: executorPlayer.inventory

        if (!source.hasPermissionLevel(server.opPermissionLevel)) {
            source.sendFeedback(
                {
                    Text.translatable("command.${PSO.MODID}.no_permission")
                        .formatted(Formatting.RED)
                },
                false
            )
            return CommandResult.FAILURE
        }

        inventory.offerOrDrop(ItemStack(ModItems.SVOBUCKS, amount))
        source.sendFeedback(
            {
                Text.translatable("command.${PSO.MODID}.svobucks.give.success")
                    .formatted(Formatting.GRAY)
            },
            false
        )
        return CommandResult.SUCCESS
    }

    private fun takeInventoryCoins(
        ctx: CommandContext<ServerCommandSource>,
        playerEraseTo: PlayerEntity
    ): CommandResult {
        val source = ctx.source
        val server = source.server

        if (!source.hasPermissionLevel(server.opPermissionLevel)) {
            source.sendFeedback(
                {
                    Text.translatable("command.${PSO.MODID}.no_permission")
                        .formatted(Formatting.RED)
                },
                false
            )
            return CommandResult.FAILURE
        }

        val inventory = playerEraseTo.inventory
        for (i in 0 until inventory.size()) {
            val currentStack = inventory.getStack(i)
            if (currentStack.item is SVOBucks) { inventory.removeStack(i) }
        }

        source.sendFeedback(
            {
                Text.translatable("command.${PSO.MODID}.svobucks.erase.success")
                    .formatted(Formatting.GRAY)
            },
            false
        )
        return CommandResult.SUCCESS
    }

    private fun giveWalletCoins(
        ctx: CommandContext<ServerCommandSource>,
        amount: Int,
        playerGiveTo: PlayerEntity?
    ): CommandResult {
        val source = ctx.source
        val server = source.server
        val executorPlayer = source.player?: return CommandResult.PASS

        if (!source.hasPermissionLevel(server.opPermissionLevel)) {
            source.sendFeedback(
                {
                    Text.translatable("command.${PSO.MODID}.no_permission")
                        .formatted(Formatting.RED)
                },
                false
            )
            return CommandResult.FAILURE
        }

        if (playerGiveTo == null) {
            MoneySubConfig.updateBalance(
                executorPlayer.gameProfile,
                MoneySubConfig.getBalance(executorPlayer.gameProfile) + amount
            )
            source.sendFeedback(
                {
                    Text.translatable("command.${PSO.MODID}.wallet.increase.success")
                        .formatted(Formatting.GRAY)
                },
                false
            )
            return CommandResult.SUCCESS
        }

        MoneySubConfig.updateBalance(
            playerGiveTo.gameProfile,
            MoneySubConfig.getBalance(playerGiveTo.gameProfile) + amount
        )
        source.sendFeedback(
            {
                Text.translatable("command.${PSO.MODID}.wallet.increase.success")
                    .formatted(Formatting.GRAY)
            },
            false
        )
        return CommandResult.SUCCESS
    }

    private fun takeWalletCoins(
        ctx: CommandContext<ServerCommandSource>,
        amount: Int,
        playerReduceTo: PlayerEntity?
    ): CommandResult {
        val source = ctx.source
        val server = source.server
        val executorPlayer = source.player?: return CommandResult.PASS
        val balance = MoneySubConfig.getBalance((playerReduceTo?: executorPlayer).gameProfile)

        if (!source.hasPermissionLevel(server.opPermissionLevel)) {
            source.sendFeedback(
                {
                    Text.translatable("command.${PSO.MODID}.no_permission")
                        .formatted(Formatting.RED)
                },
                false
            )
            return CommandResult.FAILURE
        }

        if (amount > balance) {
            source.sendFeedback(
                {
                    Text.translatable("command.${PSO.MODID}.wallet.reduce.fail")
                        .formatted(Formatting.RED)
                },
                false
            )
            return CommandResult.FAILURE
        }

        if (playerReduceTo == null) {
            MoneySubConfig.updateBalance(executorPlayer.gameProfile, balance - amount)
            source.sendFeedback(
                {
                    Text.translatable("command.${PSO.MODID}.wallet.reduce.success")
                        .formatted(Formatting.GRAY)
                },
                false
            )
            return CommandResult.SUCCESS
        }

        MoneySubConfig.updateBalance(playerReduceTo.gameProfile, balance - amount)
        source.sendFeedback(
            {
                Text.translatable("command.${PSO.MODID}.wallet.reduce.success")
                    .formatted(Formatting.GRAY)
            },
            false
        )
        return CommandResult.SUCCESS
    }

    private fun clearWalletCoins(
        ctx: CommandContext<ServerCommandSource>,
        playerEraseTo: PlayerEntity
    ): CommandResult {
        val source = ctx.source
        val server = source.server

        if (!source.hasPermissionLevel(server.opPermissionLevel)) {
            source.sendFeedback(
                {
                    Text.translatable("command.${PSO.MODID}.no_permission")
                        .formatted(Formatting.RED)
                },
                false
            )
            return CommandResult.FAILURE
        }

        MoneySubConfig.updateBalance(playerEraseTo.gameProfile, 0)
        source.sendFeedback(
            {
                Text.translatable("command.${PSO.MODID}.wallet.erase.success")
                    .formatted(Formatting.GRAY)
            },
            false
        )
        return CommandResult.SUCCESS
    }
}