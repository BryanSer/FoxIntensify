package br.bukkit.foxintensify

import org.bukkit.ChatColor
import org.bukkit.configuration.ConfigurationSection

class Setting(config: ConfigurationSection) {
    val itemID: List<Int> = config.getString("ItemID")
            .split(",".toRegex()).map(String::toInt).toList()
    val maxLevel: Int = config.getInt("MaxLevel")
    val levelInfos = mutableMapOf<Int, LevelInfo>()

    inner class LevelInfo(config: ConfigurationSection) {
        val level = config.name.toInt()
        val tmp = mutableMapOf<String, Int>()
        val lore: List<String> = config.getStringList("Lore").map {
            ChatColor.translateAlternateColorCodes('&', it)
        }.toList()

        init {
            if (config.contains("usingTmp")) {
                val tu = config.getConfigurationSection("usingTmp")
                for (k in tu.getKeys(false)) {
                    tmp[k] = tu.getInt(k)
                }
            }
        }

    }

    init {
        val set = config.getConfigurationSection("Settings")
        for (k in set.getKeys(false)) {
            levelInfos[k.toInt()] = LevelInfo(set.getConfigurationSection(k))
        }
    }
}