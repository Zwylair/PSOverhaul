package zwylair.pisskaland_overhaul.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.arguments.IntegerArgumentType
import net.minecraft.item.ItemStack
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import zwylair.pisskaland_overhaul.items.ModItems

object MoneyManage {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(literal("pso").then(literal("svobucks").then(literal("get").then(
            argument("amount", IntegerArgumentType.integer())
                .executes { giveMeMoney(it) } )
        ))) }

    private fun giveMeMoney(ctx: CommandContext<ServerCommandSource>): Int {
        val amount = IntegerArgumentType.getInteger(ctx, "amount")
        val server = ctx.source.server
        val player = ctx.source.player?: return 0

        if (ctx.source.hasPermissionLevel(server.opPermissionLevel)) {
            player.inventory.insertStack(ItemStack(ModItems.SVOBUCKS).copyWithCount(amount))
            return 1
        }
        return 0
    }
}
