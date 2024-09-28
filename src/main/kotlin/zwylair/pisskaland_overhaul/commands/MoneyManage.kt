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
import zwylair.pisskaland_overhaul.items.ModItems

object MoneyManage {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        LOGGER.info("Trying to MoneyManage commands")

        dispatcher.register(literal("pso").then(literal("svobucks").then(literal("give").then(
            argument("amount", IntegerArgumentType.integer())
                    .executes{ giveMoney(it, IntegerArgumentType.getInteger(it, "amount"), null) }
                        .then(argument("player", EntityArgumentType.player())
                            .executes { giveMoney(it, IntegerArgumentType.getInteger(it, "amount"), EntityArgumentType.getPlayer(it, "player")) }
        ))))) }

    private fun giveMoney(
        ctx: CommandContext<ServerCommandSource>,
        amount: Int,
        playerSendTo: PlayerEntity?
    ): Int {
        val server = ctx.source.server
        val executorPlayer = ctx.source.player?: return 0

        if (!ctx.source.hasPermissionLevel(server.opPermissionLevel)) {
            ctx.source.sendFeedback(
                { Text.translatable("command.${PSO.MODID}.svobucks.give.no_permission").formatted(Formatting.RED) },
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

        executorPlayer.world.players.forEach{
            if (it == playerSendTo) {
                it.inventory.offerOrDrop(ItemStack(ModItems.SVOBUCKS).copyWithCount(amount))
                ctx.source.sendFeedback(
                    { Text.translatable("command.${PSO.MODID}.svobucks.give.success").formatted(Formatting.GRAY) },
                    false
                )
                return 1
            }
        }

        ctx.source.sendFeedback(
            { Text.translatable("command.${PSO.MODID}.svobucks.give.fail"
                .format(playerSendTo.name.string)
            ).formatted(Formatting.RED) },
            false
        )
        return 0
    }
}
