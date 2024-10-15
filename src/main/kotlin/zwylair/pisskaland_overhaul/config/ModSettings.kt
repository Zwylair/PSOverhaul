package zwylair.pisskaland_overhaul.config

import java.io.File

object ModSettings {
    const val MOD_VERSION: String = "1.1.0"
    val COMPATIBLE_SERVER_MOD_VERSIONS = listOf(
        MOD_VERSION,
    )
    const val MAX_DAYS_WITHOUT_PRAYING = 7
    val CONFIG_FILE = File("config/pso_storage.json")
}
