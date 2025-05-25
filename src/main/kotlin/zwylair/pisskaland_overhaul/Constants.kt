package zwylair.pisskaland_overhaul

import java.io.File

object Constants {
    const val MOD_VERSION: String = "1.1.0"
    val COMPATIBLE_SERVER_MOD_VERSIONS = listOf(
        MOD_VERSION,
    )
    val CONFIG_FILE = File("config/pso_storage.json")
}