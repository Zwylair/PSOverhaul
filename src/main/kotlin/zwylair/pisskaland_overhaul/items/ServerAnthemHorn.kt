package zwylair.pisskaland_overhaul.items

import zwylair.pisskaland_overhaul.PSO
import zwylair.pisskaland_overhaul.soundevents.ModSoundEvents

class ServerAnthemHorn : HornTemplate(
    Settings().maxCount(1),
    20 * 5 * 60,
    ModSoundEvents.SERVER_ANTHEM_SOUND_EVENT,
    "item.${PSO.MODID}.server_anthem_horn_tooltip"
)
