package br.bukkit.foxintensify

import Br.API.Utils
import Br.RPGAttribute.Attribute
import Br.RPGAttribute.AttributeEvent
import Br.RPGAttribute.Tool.MinMax
import com.comphenix.protocol.wrappers.nbt.NbtFactory
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.logging.Level

const val NBT_LEVEL_KEY = "foxintensify"

object SettingManager : Listener {
    lateinit var settings: List<Setting>
    fun loadConfig() {
        val f = File(Main.getPlguin().dataFolder, "Intensify")
        if (!f.exists()) {
            f.mkdirs()
            Utils.saveResource(Main.getPlguin(), "example.yml", f)
        }
        settings = f.listFiles()
                .map(YamlConfiguration::loadConfiguration).map(::Setting).toList()
    }

    operator fun get(item: ItemStack): Info? {
        val find = settings.find { it.itemID.contains(item.typeId) }
        if (find != null) {
            try {
                val wtag = NbtFactory.fromItemTag(item)
                if (wtag == null) {
                    return Info(find, 0)
                }
                val tag = NbtFactory.asCompound(wtag)
                if (!tag.containsKey(NBT_LEVEL_KEY)) {
                    return Info(find, 0)
                }
                val lv = tag.getInteger(NBT_LEVEL_KEY)
                return Info(find, lv)
            } catch (e: Throwable) {
                //Bukkit.getLogger().log(Level.WARNING,"异常物品: $item")
            }
        }
        return null
    }

    fun AttributeEvent.ItemAttributeEvent.add(map: Map<Attribute.State, Double>) {
        for ((k, v) in map) {
            val t = this.addition[k]
            if (t == null) {
                this.addition[k] = MinMax(v, v)
            } else {
                t.addMax(v)
                t.addMin(v)
            }
        }
    }

    val DEBUG = false
    @EventHandler
    fun onAttr(evt: AttributeEvent.ItemAttributeEvent) {
        if (DEBUG) {
            Bukkit.broadcastMessage("§c尝试读取物品强化: ${evt.item.toString()}")
        }
        if (evt.item.type == Material.AIR) {
            return
        }
        val info = this[evt.item]
        if (info != null) {
            if (DEBUG) {
                Bukkit.broadcastMessage("§c物品强化等级: ${info.level}")
            }
            if (info.level > 0) {
                val linfo = info.setting.levelInfos[info.level]
                if (DEBUG)
                    Bukkit.broadcastMessage("§linfo: ${linfo != null}")
                if (linfo != null) {
                    for ((tn, lv) in linfo.tmp) {
                        val template = TemplateManager.templates[tn] ?: continue
                        if (DEBUG) {
                            Bukkit.broadcastMessage("追加属性: $template * $lv")
                        }
                        val map = template * lv
                        evt.add(map)
                    }
                }
            }
        }
    }
}

data class Info(
        val setting: Setting,
        var level: Int
) {
    fun getLevelInfo() = setting.levelInfos[level]
}