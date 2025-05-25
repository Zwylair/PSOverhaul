package zwylair.pso

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider
import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.criterion.ImpossibleCriterion
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.function.Consumer
import zwylair.pso.blocks.ModBlocks

class PSODataGenerator() : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack = fabricDataGenerator.createPack()
        pack.addProvider(::AdvancementsProvider)
    }

    class AdvancementsProvider(dataGenerator: FabricDataOutput) : FabricAdvancementProvider(dataGenerator) {
        override fun generateAdvancement(consumer: Consumer<Advancement>) {
            Advancement.Builder.create()
                .display(
                    ModBlocks.ALTAR_GLASS, // The display icon
                    Text.translatable("advancement.${PSO.MODID}.make_altar.title"),
                    Text.translatable("advancement.${PSO.MODID}.make_altar.description"),
                    Identifier("textures/gui/advancements/backgrounds/adventure.png"), // Background image used
                    AdvancementFrame.TASK, // Options: TASK, CHALLENGE, GOAL
                    true, // Show toast top right
                    true, // Announce to chat
                    false // Hidden in the advancement tab
                )
                .criterion("impossible_lol", ImpossibleCriterion.Conditions())
                .build(consumer, "${PSO.MODID}/make_altar")
        }
    }
}