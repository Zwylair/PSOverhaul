package zwylair.pso.config

import com.google.gson.JsonObject

abstract class SubConfig {
    protected var jsonData = JsonObject()
    protected abstract val fullConfigProperty: String

    fun save(propertySetter: (String, JsonObject?) -> Unit) {
        propertySetter(fullConfigProperty, jsonData)
    }

    fun load(propertyGetter: (String) -> JsonObject?) {
        jsonData = propertyGetter(fullConfigProperty)?: JsonObject()
    }
}