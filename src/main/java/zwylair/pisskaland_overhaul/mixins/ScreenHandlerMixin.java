package zwylair.pisskaland_overhaul.mixins;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zwylair.pisskaland_overhaul.callbacks.HotbarSwapCallback;
import net.minecraft.util.ActionResult;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
    @Inject(method = "internalOnSlotClick", at = @At("HEAD"), cancellable = true)
    private void internalOnSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        ActionResult result = HotbarSwapCallback.EVENT.invoker().interact(((ScreenHandler) (Object) this), slotIndex, button, actionType, player);

        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }
}
