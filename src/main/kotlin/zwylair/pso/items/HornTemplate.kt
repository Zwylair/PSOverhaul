package zwylair.pso.items

import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.registry.tag.InstrumentTags
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.GoatHornItem
import net.minecraft.item.Instrument
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.stat.Stats
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import net.minecraft.world.event.GameEvent.Emitter
import java.util.Optional

open class HornTemplate(
    settings: Settings,
    val tickDuration: Int,
    val soundEvent: SoundEvent,
    val tooltipTranslationKey: String
    ) : GoatHornItem(settings, InstrumentTags.GOAT_HORNS)
{
    override fun appendTooltip(stack: ItemStack?, world: World?, tooltip: MutableList<Text?>?, context: TooltipContext?) {
        val mutableText = Text.translatable(tooltipTranslationKey)
        tooltip?.add(mutableText.formatted(Formatting.GRAY))
    }

    override fun getMaxUseTime(stack: ItemStack): Int { return tickDuration }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack = user.getStackInHand(hand)
        val optional: Optional<out RegistryEntry<Instrument>> = getInstrument(itemStack)
        return if (optional.isPresent) {
            val instrument = optional.get().value() as Instrument
            user.setCurrentHand(hand)
            playSound(world, user, instrument)
            user.itemCooldownManager.set(this, tickDuration)
            user.incrementStat(Stats.USED.getOrCreateStat(this))
            TypedActionResult.consume(itemStack)
        } else {
            TypedActionResult.fail(itemStack)
        }
    }

    private fun getInstrument(stack: ItemStack): Optional<out RegistryEntry<Instrument>> {
        val nbtCompound: NbtCompound? = stack.nbt
        if (nbtCompound != null && nbtCompound.contains("instrument", 8)) {
            val identifier: Identifier? = Identifier.tryParse(nbtCompound.getString("instrument"))
            if (identifier != null) {
                return Registries.INSTRUMENT.getEntry(RegistryKey.of(RegistryKeys.INSTRUMENT, identifier))
            }
        }

        val iterator = Registries.INSTRUMENT.iterateEntries(InstrumentTags.GOAT_HORNS).iterator()
        return if (iterator.hasNext()) Optional.of(iterator.next() as RegistryEntry<Instrument>) else Optional.empty()
    }

    private fun playSound(world: World, player: PlayerEntity, instrument: Instrument) {
        val f = instrument.range() / 16.0F
        world.playSoundFromEntity(player, player, soundEvent, SoundCategory.RECORDS, f, 1.0F)
        world.emitGameEvent(GameEvent.INSTRUMENT_PLAY, player.pos, Emitter.of(player))
    }
}
