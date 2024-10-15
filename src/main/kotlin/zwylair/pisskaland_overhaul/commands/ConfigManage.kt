package zwylair.pisskaland_overhaul.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import zwylair.pisskaland_overhaul.config.Config
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.PSO.Companion.LOGGER

object ConfigManage {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        LOGGER.info("Trying to register ConfigManage commands")

        dispatcher.register(literal("pso").then(literal("config").then(literal("reload")
            .executes { giveCoinsToPlayer(it) }
        )))
    }

    private fun giveCoinsToPlayer(ctx: CommandContext<ServerCommandSource>): Int {
        val server = ctx.source.server

        if (!ctx.source.hasPermissionLevel(server.opPermissionLevel)) {
            ctx.source.sendFeedback(
                { Text.translatable("command.${PSO.MODID}.no_permission").formatted(Formatting.RED) },
                false
            )
            return 0
        }

        Config.loadConfig()
        ctx.source.sendFeedback(
            { Text.translatable("command.${PSO.MODID}.config.reload.success").formatted(Formatting.GREEN) },
            false
        )
        return 1
    }
}
