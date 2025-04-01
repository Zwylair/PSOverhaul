package zwylair.pisskaland_overhaul.events

//import net.minecraft.entity.LivingEntity
//import net.minecraft.entity.player.PlayerEntity
//import net.minecraft.item.ItemStack
//import net.minecraft.text.Text
//import net.minecraft.util.ActionResult
//import net.minecraft.util.Formatting
//import net.minecraft.world.World
//import zwylair.pisskaland_overhaul.PSO
//import zwylair.pisskaland_overhaul.PSO.Companion.LOGGER
//import zwylair.pisskaland_overhaul.callbacks.AfterEatFoodCallback
//import zwylair.pisskaland_overhaul.config.EatenDataConfig
//
//object EatFood {
////    private val cancelMap: MutableMap<PlayerEntity, ItemStack> = mutableMapOf()
//
//    fun register() {
//        LOGGER.info("Trying to register EatFood events")
//
////        BeforeEatFoodCallback.EVENT.register(::beforeEatFood)
//        AfterEatFoodCallback.EVENT.register(::afterEatFood)
//    }
//
//    fun afterEatFood(entity: LivingEntity, world: World, stack: ItemStack): ActionResult {
//        if (!entity.isPlayer) { return ActionResult.SUCCESS }
//        var player: PlayerEntity = entity as PlayerEntity
//
//        if (!world.isClient) {
//            LOGGER.info(
//                "%s has eaten %s (%s more to chow down)"
//                    .format(player.gameProfile.name, stack.item.name.string, 2 - (EatenDataConfig.getEatenCount(player.gameProfile, stack.item)?: 0))
//            )
//
//            if (EatenDataConfig.isOnceEaten(player.gameProfile, stack.item)) {
//                player.sendMessage(
//                    Text
//                        .translatable("${PSO.MODID}.chow_down_on.eaten_once")
//                        .formatted(Formatting.LIGHT_PURPLE)
//                )
//            }
//
//            if (EatenDataConfig.isTwiceEaten(player.gameProfile, stack.item)) {
//                player.sendMessage(
//                    Text
//                        .translatable("${PSO.MODID}.chow_down_on.eaten_twice")
//                        .formatted(Formatting.RED)
//                )
//                return ActionResult.FAIL
//            }
//
//            EatenDataConfig.incrementFoodCounter(player.gameProfile, stack.item)
//        }
//
//        return ActionResult.SUCCESS
//    }
//}
