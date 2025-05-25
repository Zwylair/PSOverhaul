package zwylair.pisskaland_overhaul.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.command.argument.ItemStackArgumentType
import net.minecraft.item.ItemStack
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.PSO.Companion.LOGGER
import zwylair.pisskaland_overhaul.config.DenyListSubConfig

object DenyList {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>, registryAccess: CommandRegistryAccess) {
        LOGGER.info("Registering DenyList commands")
        dispatcher.register(buildCommands(registryAccess))
    }

    private fun buildCommands(registryAccess: CommandRegistryAccess) = literal("pso").then(
        literal("denylist")
            .then(buildAddSubcommand(registryAccess))
            .then(buildRemoveSubcommand())
    )

    private fun buildAddSubcommand(registryAccess: CommandRegistryAccess): LiteralArgumentBuilder<ServerCommandSource> {
        return literal("add")
            .then(
                argument("item", ItemStackArgumentType.itemStack(registryAccess))
                    .executes {
                        val stack = ItemStackArgumentType.getItemStackArgument(it, "item").createStack(1, false)
                        addToDenyList(it, stack, reward = 5).code
                    }
                    .then(
                        argument("reward", IntegerArgumentType.integer())
                            .executes {
                                val stack = ItemStackArgumentType.getItemStackArgument(it, "item").createStack(1, false)
                                val reward = IntegerArgumentType.getInteger(it, "reward")
                                addToDenyList(it, stack, reward).code
                            }
                    )
            )
    }

    private fun buildRemoveSubcommand(): LiteralArgumentBuilder<ServerCommandSource> {
        return literal("remove")
            .then(
                argument("itemId", StringArgumentType.string())
                    .executes {
                        val itemId = StringArgumentType.getString(it, "itemId")
                        removeFromDenyList(it, itemId).code
                    }
            )
    }

    private fun addToDenyList(ctx: CommandContext<ServerCommandSource>, item: ItemStack, reward: Int): CommandResult {
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

        if (DenyListSubConfig.has(item.translationKey)) {
            source.sendFeedback(
                {
                    Text.translatable("command.${PSO.MODID}.denylist.add.already_in_list")
                        .formatted(Formatting.RED)
                },
                false
            )
            return CommandResult.FAILURE
        }

        DenyListSubConfig.append(item.translationKey, reward)
        source.sendFeedback(
            {
                Text.translatable("command.${PSO.MODID}.denylist.add.success")
                    .formatted(Formatting.GRAY)
            },
            false
        )
        return CommandResult.SUCCESS
    }

    private fun removeFromDenyList(ctx: CommandContext<ServerCommandSource>, itemId: String): CommandResult {
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

        if (!DenyListSubConfig.has(itemId)) {
            source.sendFeedback(
                {
                    Text.translatable("command.${PSO.MODID}.denylist.remove.not_in_list")
                        .formatted(Formatting.RED)
                },
                false
            )
            return CommandResult.FAILURE
        }

        DenyListSubConfig.remove(itemId)
        source.sendFeedback(
            {
                Text.translatable("command.${PSO.MODID}.denylist.remove.success")
                    .formatted(Formatting.GRAY)
            },
            false
        )
        return CommandResult.SUCCESS
    }
}
