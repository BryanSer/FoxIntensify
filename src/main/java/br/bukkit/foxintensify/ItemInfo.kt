package br.bukkit.foxintensify

import org.bukkit.ChatColor
import org.bukkit.configuration.ConfigurationSection

open class ItemInfo(config: ConfigurationSection) {
    val itemName: String = ChatColor.translateAlternateColorCodes('&', config.getString("ItemName"))
}

open class MChanceItem(config: ConfigurationSection) : ItemInfo(config) {
    val chances = mutableMapOf<Int, Double>()
    val targetLevel :Int

    init {
        val cs = config.getConfigurationSection("Chance")
        for(k in cs.getKeys(false)){
            val key = k.toInt()
            chances[key] = cs.getDouble(k)
        }
        if(config.contains("TargetLevel")){
            targetLevel = config.getInt("TargetLevel")
        }else{
            targetLevel = 0
        }
    }
}

open class ChanceItem(config: ConfigurationSection) : ItemInfo(config) {
    val chance: Double = config.getDouble("Chance")
}