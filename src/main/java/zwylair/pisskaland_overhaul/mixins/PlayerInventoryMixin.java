package zwylair.pisskaland_overhaul.mixins;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zwylair.pisskaland_overhaul.callbacks.PlayerPickupItemCallback;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Inject(method = "addStack(ILnet/minecraft/item/ItemStack;)I", at = @At("TAIL"), cancellable = true)
    private void onItemPickup(int slot, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        ActionResult result = PlayerPickupItemCallback.EVENT.invoker().interact((PlayerInventory) (Object) this, slot, stack);
        if (result == ActionResult.FAIL) {
            cir.cancel();
        }
    }
}
