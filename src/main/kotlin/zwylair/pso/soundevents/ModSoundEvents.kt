package zwylair.pso.soundevents

import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import zwylair.pso.PSO

object ModSoundEvents {
    lateinit var NO_SOUND_SOUND_EVENT: SoundEvent
    lateinit var SERVER_ANTHEM_SOUND_EVENT: SoundEvent

    fun init() {
        NO_SOUND_SOUND_EVENT = register(PSO.id("no_sound"))
        SERVER_ANTHEM_SOUND_EVENT = register(PSO.id("server_anthem"))
    }

    private fun register(identifier: Identifier): SoundEvent {
        val soundEvent = Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier))
        PSO.LOGGER.info("{} SoundEvent registered", identifier.toTranslationKey())
        return soundEvent
    }
}
