package br.bukkit.foxintensify

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object TemplateManager {
    var EditItemName: Boolean = true
    val templates = mutableMapOf<String, Template>()
    fun loadConfig() {
        val f = File(Main.getPlguin().dataFolder, "config.yml")
        if (!f.exists()) {
            Main.getPlguin().saveDefaultConfig()
        }
        val config = YamlConfiguration.loadConfiguration(f)
        EditItemName = config.getBoolean("EditItemName", true)
        val t = config.getConfigurationSection("Template")
        templates.clear()
        for (k in t.getKeys(false)) {
            templates[k] = Template(t.getStringList(k))
        }
    }
}