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
        LOGGER.info("Registering ConfigManage commands")
        dispatcher.register(buildConfigCommands())
    }

    private fun buildConfigCommands() = literal("pso").then(
        literal("config").then(
            literal("reload")
                .executes { reloadConfig(it).code }
        )
    )

    private fun reloadConfig(ctx: CommandContext<ServerCommandSource>): CommandResult {
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

        Config.load()
        source.sendFeedback(
            {
                Text.translatable("command.${PSO.MODID}.config.reload.success")
                    .formatted(Formatting.GREEN)
            },
            false
        )
        return CommandResult.SUCCESS
    }
}
