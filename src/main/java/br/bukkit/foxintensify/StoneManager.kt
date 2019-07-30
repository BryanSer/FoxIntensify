package br.bukkit.foxintensify

import Br.API.Utils
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.io.File

object StoneManager {
    val strones = mutableListOf<MChanceItem>()
    val luckyStones = mutableListOf<ChanceItem>()
    lateinit var protection: ItemInfo

    fun loadConfig(){
        val sf = File(Main.getPlguin().dataFolder,"stone.yml")
        if(!sf.exists()){
            Utils.saveResource(Main.getPlguin(),"stone.yml",null)
        }
        val config = YamlConfiguration.loadConfiguration(sf)
        val st = config.getConfigurationSection("Stone")
        strones.clear()
        for(key in st.getKeys(false)){
            strones += MChanceItem(st.getConfigurationSection(key))
        }
        val luck = config.getConfigurationSection("Lucky")
        luckyStones.clear()
        for(key in luck.getKeys(false)){
            luckyStones += ChanceItem(luck.getConfigurationSection(key))
        }
        protection = ItemInfo(config.getConfigurationSection("Protection"))
    }

    fun getStone(item: ItemStack):MChanceItem?{
        if(!item.hasItemMeta() || !item.itemMeta.hasDisplayName()){
            return null
        }
        val name = item.itemMeta.displayName
        return strones.find {
            name.contains(it.itemName)
        }
    }

    fun getLucky(item: ItemStack):ChanceItem?{
        if(!item.hasItemMeta() || !item.itemMeta.hasDisplayName()){
            return null
        }
        val name = item.itemMeta.displayName
        return luckyStones.find {
            name.contains(it.itemName)
        }
    }

    fun isProtection(item:ItemStack):Boolean{
        if(!item.hasItemMeta() || !item.itemMeta.hasDisplayName()){
            return false
        }
        val name = item.itemMeta.displayName
        return name.contains(protection.itemName)
    }
}