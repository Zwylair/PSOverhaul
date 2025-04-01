package zwylair.pisskaland_overhaul.mixins;

//import zwylair.pisskaland_overhaul.callbacks.BeforeEatFoodCallback;
//import zwylair.pisskaland_overhaul.callbacks.AfterEatFoodCallback;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.ActionResult;
//import net.minecraft.world.World;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//@Mixin(LivingEntity.class)
//public class EatFoodMixin {
//    @Inject(method = "eatFood", at = @At("HEAD"), cancellable = true)
//    private void beforeEatFood(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
//        LivingEntity entity = (LivingEntity) (Object) this;
//        ActionResult result = BeforeEatFoodCallback.EVENT.invoker().beforeEatFood(entity, world, stack);
//        if (result == ActionResult.FAIL) {
//            cir.cancel();
//        }
//    }
//
//    @Inject(method = "eatFood", at = @At("RETURN"))
//    private void afterEatFood(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
//        LivingEntity entity = (LivingEntity) (Object) this;
//        AfterEatFoodCallback.EVENT.invoker().afterEatFood(entity, world, stack);
//    }
//}
