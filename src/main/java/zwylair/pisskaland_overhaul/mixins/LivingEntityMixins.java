package zwylair.pisskaland_overhaul.mixins;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import zwylair.pisskaland_overhaul.PSO;
import zwylair.pisskaland_overhaul.config.EatenSubConfig;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixins {
    @Unique private final Logger LOGGER = PSO.Companion.getLOGGER();

    /**
     * @author Zwylair
     * @reason Implementing the restrictions of eating more than two types of food in a minecraft day
     */
    @Overwrite
    public ItemStack eatFood(World world, ItemStack stack) {
        if (!stack.isFood()) { return stack; }


        var its = ((LivingEntity) (Object) this);
        PlayerEntity player = (PlayerEntity) its;
        EatenSubConfig eatenDataConfig = EatenSubConfig.INSTANCE;

        if (!world.isClient()) {
            Integer eatenCount = eatenDataConfig.getEatenCount(player.getGameProfile(), stack.getItem());

            LOGGER.info(
                    "{} is trying to eat {} ({} more to chow down)",
                    player.getGameProfile().getName(),
                    stack.getItem().getName().getString(),
                    2 - (eatenCount == null ? 0 : eatenCount)
            );
        }

        if (
                !its.isPlayer() ||
                !eatenDataConfig.isTwiceEaten(player.getGameProfile(), stack.getItem())
        ) {
            // pso
            if (!world.isClient()) {
                if (eatenDataConfig.isOnceEaten(player.getGameProfile(), stack.getItem())) {
                    player.sendMessage(
                            Text.translatable(PSO.MODID + ".chow_down_on.eaten_once").formatted(Formatting.LIGHT_PURPLE)
                    );
                }

                eatenDataConfig.incrementFoodCounter(player.getGameProfile(), stack.getItem());
            }

            // vanilla
            world.playSound(
                    null,
                    its.getX(),
                    its.getY(),
                    its.getZ(),
                    its.getEatSound(stack),
                    SoundCategory.NEUTRAL,
                    1.0F,
                    1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F
            );

            ((LivingEntityAccessor) its).callApplyFoodEffects(stack, world, its);

            if (!its.isPlayer() || !player.getAbilities().creativeMode) {
                stack.decrement(1);
            }

            its.emitGameEvent(GameEvent.EAT);
        } else {
            if (!world.isClient()) {
                player.sendMessage(
                        Text.translatable(PSO.MODID + ".chow_down_on.eaten_twice").formatted(Formatting.RED)
                );
                LOGGER.info("Prevented. Reason: chew down restrictions");
            }
        }

        return stack;
    }
}
