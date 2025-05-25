package zwylair.pso.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;

public interface HotbarSwapCallback {
    Event<HotbarSwapCallback> EVENT = EventFactory.createArrayBacked(HotbarSwapCallback.class,
            (listeners) -> (screenHandler, slotId, stack, hotbarIndex, player) -> {
                for (HotbarSwapCallback listener : listeners) {
                    ActionResult result = listener.interact(screenHandler, slotId, stack, hotbarIndex, player);
                    if (result != ActionResult.PASS) return result;
                }
                return ActionResult.PASS;
            });

    ActionResult interact(ScreenHandler screenHandler, int slotIndex, int button, SlotActionType actionType, PlayerEntity player);
}
