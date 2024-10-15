package zwylair.pisskaland_overhaul.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.arguments.IntegerArgumentType
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
import zwylair.pisskaland_overhaul.config.MoneyConfig
import zwylair.pisskaland_overhaul.items.ModItems
import zwylair.pisskaland_overhaul.items.SVOBucks

object MoneyManage {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        LOGGER.info("Trying to register MoneyManage commands")

        dispatcher.register(literal("pso").then(literal("svobucks").then(literal("give").then(
            argument("amount", IntegerArgumentType.integer())
                    .executes{ giveCoinsToPlayer(it, IntegerArgumentType.getInteger(it, "amount"), null) }
                        .then(argument("player", EntityArgumentType.player())
                            .executes { giveCoinsToPlayer(it, IntegerArgumentType.getInteger(it, "amount"), EntityArgumentType.getPlayer(it, "player")) }
        )))))

        dispatcher.register(literal("pso").then(literal("svobucks").then(literal("erase").then(
            argument("player", EntityArgumentType.player())
                .executes { eraseCoinsFromPlayerInventory(it, EntityArgumentType.getPlayer(it, "player")) }
        ))))

        dispatcher.register(literal("pso").then(literal("wallet").then(literal("erase").then(
            argument("player", EntityArgumentType.player())
                .executes { erasePlayerWallet(it, EntityArgumentType.getPlayer(it, "player")) }
            ))))

        dispatcher.register(literal("pso").then(literal("wallet").then(literal("give").then(
            argument("amount", IntegerArgumentType.integer())
                .executes{ increasePlayerWallet(it, IntegerArgumentType.getInteger(it, "amount"), null) }
                .then(argument("player", EntityArgumentType.player())
                    .executes { increasePlayerWallet(it, IntegerArgumentType.getInteger(it, "amount"), EntityArgumentType.getPlayer(it, "player")) }
        )))))

        dispatcher.register(literal("pso").then(literal("wallet").then(literal("reduce").then(
            argument("amount", IntegerArgumentType.integer())
                .executes{ reducePlayerWallet(it, IntegerArgumentType.getInteger(it, "amount"), null) }
                .then(argument("player", EntityArgumentType.player())
                    .executes { reducePlayerWallet(it, IntegerArgumentType.getInteger(it, "amount"), EntityArgumentType.getPlayer(it, "player")) }
        )))))
    }

    private fun giveCoinsToPlayer(
        ctx: CommandContext<ServerCommandSource>,
        amount: Int,
        playerSendTo: PlayerEntity?
    ): Int {
        val server = ctx.source.server
        val executorPlayer = ctx.source.player?: return 0

        if (!ctx.source.hasPermissionLevel(server.opPermissionLevel)) {
            ctx.source.sendFeedback(
                { Text.translatable("command.pisskaland_overhaul.no_permission").formatted(Formatting.RED) },
                false
            )
            return 0
        }

        if (playerSendTo == null) {
            executorPlayer.inventory.offerOrDrop(ItemStack(ModItems.SVOBUCKS).copyWithCount(amount))
            ctx.source.sendFeedback(
                { Text.translatable("command.${PSO.MODID}.svobucks.give.success").formatted(Formatting.GRAY) },
                false
            )
            return 1
        }

        playerSendTo.inventory.offerOrDrop(ItemStack(ModItems.SVOBUCKS).copyWithCount(amount))
        ctx.source.sendFeedback(
            { Text.translatable("command.${PSO.MODID}.svobucks.give.success").formatted(Formatting.GRAY) },
            false
        )
        return 1
    }

    private fun eraseCoinsFromPlayerInventory(
        ctx: CommandContext<ServerCommandSource>,
        playerEraseTo: PlayerEntity
    ): Int {
        if (!ctx.source.hasPermissionLevel(ctx.source.server.opPermissionLevel)) {
            ctx.source.sendFeedback(
                { Text.translatable("command.pisskaland_overhaul.no_permission").formatted(Formatting.RED) },
                false
            )
            return 1
        }

        for (i in 0 until playerEraseTo.inventory.size()) {
            val currentStack = playerEraseTo.inventory.getStack(i)
            if (currentStack.item is SVOBucks) { playerEraseTo.inventory.removeStack(i) }
        }

        ctx.source.sendFeedback(
            { Text.translatable("command.${PSO.MODID}.svobucks.erase.success").formatted(Formatting.GRAY) },
            false
        )
        return 0
    }

    private fun increasePlayerWallet(
        ctx: CommandContext<ServerCommandSource>,
        amount: Int,
        playerGiveTo: PlayerEntity?
    ): Int {
        val executorPlayer = ctx.source.player?: return 0

        if (!ctx.source.hasPermissionLevel(ctx.source.server.opPermissionLevel)) {
            ctx.source.sendFeedback(
                { Text.translatable("command.pisskaland_overhaul.no_permission").formatted(Formatting.RED) },
                false
            )
            return 1
        }

        if (playerGiveTo == null) {
            MoneyConfig.updateMoneyAmount(
                executorPlayer.gameProfile,
                MoneyConfig.getMoneyAmount(executorPlayer.gameProfile) + amount
            )
            ctx.source.sendFeedback(
                { Text.translatable("command.${PSO.MODID}.wallet.increase.success").formatted(Formatting.GRAY) },
                false
            )
            return 1
        }

        MoneyConfig.updateMoneyAmount(
            playerGiveTo.gameProfile,
            MoneyConfig.getMoneyAmount(playerGiveTo.gameProfile) + amount
        )
        ctx.source.sendFeedback(
            { Text.translatable("command.${PSO.MODID}.wallet.increase.success").formatted(Formatting.GRAY) },
            false
        )
        return 1
    }

    private fun reducePlayerWallet(
        ctx: CommandContext<ServerCommandSource>,
        amount: Int,
        playerReduceTo: PlayerEntity?
    ): Int {
        val executorPlayer = ctx.source.player?: return 0
        val balance = MoneyConfig.getMoneyAmount((playerReduceTo?: executorPlayer).gameProfile)

        if (!ctx.source.hasPermissionLevel(ctx.source.server.opPermissionLevel)) {
            ctx.source.sendFeedback(
                { Text.translatable("command.pisskaland_overhaul.no_permission").formatted(Formatting.RED) },
                false
            )
            return 1
        }

        if (amount > balance) {
            ctx.source.sendFeedback(
                { Text.translatable("command.${PSO.MODID}.wallet.reduce.fail").formatted(Formatting.RED) },
                false
            )
            return 0
        }

        if (playerReduceTo == null) {
            MoneyConfig.updateMoneyAmount(executorPlayer.gameProfile, balance - amount)
            ctx.source.sendFeedback(
                { Text.translatable("command.${PSO.MODID}.wallet.reduce.success").formatted(Formatting.GRAY) },
                false
            )
            return 1
        }

        MoneyConfig.updateMoneyAmount(playerReduceTo.gameProfile, balance - amount)
        ctx.source.sendFeedback(
            { Text.translatable("command.${PSO.MODID}.wallet.reduce.success").formatted(Formatting.GRAY) },
            false
        )
        return 1
    }

    private fun erasePlayerWallet(
        ctx: CommandContext<ServerCommandSource>,
        playerEraseTo: PlayerEntity
    ): Int {
        if (!ctx.source.hasPermissionLevel(ctx.source.server.opPermissionLevel)) {
            ctx.source.sendFeedback(
                { Text.translatable("command.pisskaland_overhaul.no_permission").formatted(Formatting.RED) },
                false
            )
            return 1
        }

        MoneyConfig.updateMoneyAmount(playerEraseTo.gameProfile, 0)
        ctx.source.sendFeedback(
            { Text.translatable("command.${PSO.MODID}.wallet.erase.success").formatted(Formatting.GRAY) },
            false
        )
        return 0
    }
}
