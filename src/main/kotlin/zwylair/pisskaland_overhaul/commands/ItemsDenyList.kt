package zwylair.pisskaland_overhaul.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.PSO.Companion.LOGGER
import zwylair.pisskaland_overhaul.config.DenyListConfig

object ItemsDenyList {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        LOGGER.info("Trying to register ItemsDenyList commands")

        dispatcher.register(literal("pso").then(literal("denylist").then(literal("add").then(
            argument("itemId", StringArgumentType.string())
                .executes{ addToDenyList(it, StringArgumentType.getString(it, "itemId"), reward=5) }
                .then(argument("reward", IntegerArgumentType.integer())
                    .executes { addToDenyList(it, StringArgumentType.getString(it, "itemId"), IntegerArgumentType.getInteger(it, "reward")) }
        )))))

        dispatcher.register(literal("pso").then(literal("denylist").then(literal("remove").then(
            argument("itemId", StringArgumentType.string())
                .executes{ removeFromDenyList(it, StringArgumentType.getString(it, "itemId")) }
        ))))
    }

    private fun addToDenyList(ctx: CommandContext<ServerCommandSource>, itemId: String, reward: Int): Int {
        val server = ctx.source.server

        if (!ctx.source.hasPermissionLevel(server.opPermissionLevel)) {
            ctx.source.sendFeedback(
                { Text.translatable("command.${PSO.MODID}.no_permission").formatted(Formatting.RED) },
                false
            )
            return 0
        }

        if (DenyListConfig.isAlreadyInDenyList(itemId)) {
            ctx.source.sendFeedback(
                { Text.translatable("command.${PSO.MODID}.denylist.add.already_in_list").formatted(Formatting.RED) },
                false
            )
            return 0
        }

        DenyListConfig.addToDenyList(itemId, reward)
        ctx.source.sendFeedback(
            { Text.translatable("command.${PSO.MODID}.denylist.add.success").formatted(Formatting.GRAY) },
            false
        )
        return 1
    }

    private fun removeFromDenyList(ctx: CommandContext<ServerCommandSource>, itemId: String): Int {
        val server = ctx.source.server

        if (!ctx.source.hasPermissionLevel(server.opPermissionLevel)) {
            ctx.source.sendFeedback(
                { Text.translatable("command.${PSO.MODID}.no_permission").formatted(Formatting.RED) },
                false
            )
            return 0
        }

        if (!DenyListConfig.isAlreadyInDenyList(itemId)) {
            ctx.source.sendFeedback(
                { Text.translatable("command.${PSO.MODID}.denylist.remove.not_in_list").formatted(Formatting.RED) },
                false
            )
            return 0
        }

        DenyListConfig.removeFromDenyList(itemId)
        ctx.source.sendFeedback(
            { Text.translatable("command.${PSO.MODID}.denylist.remove.success").formatted(Formatting.GRAY) },
            false
        )
        return 1
    }
}
