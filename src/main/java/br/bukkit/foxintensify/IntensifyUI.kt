package br.bukkit.foxintensify

import Br.API.GUI.Ex.Snapshot
import Br.API.GUI.Ex.UIManager
import Br.API.GUI.Ex.kt.KtItem
import Br.API.GUI.Ex.kt.KtUIBuilder
import Br.API.Utils
import Br.API.ktsuger.ItemBuilder
import Br.API.ktsuger.msg
import Br.API.ktsuger.plusAssign
import Br.API.ktsuger.unaryPlus
import com.comphenix.protocol.wrappers.nbt.NbtFactory
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object IntensifyUI {
    //operator fun IntRange.invoke(): Array<Int> = this.toList().toTypedArray()

    const val ITEM_SLOT = 10
    val BUTTON_SLOT = arrayOf(43, 37)
    const val LUCKY_SLOT = 4
    const val STONE_SLOT = 16
    const val PROT_SLOT = 22
    const val RESULT_SLOT = 49
    const val FOX_LORE_PREFIX = "§r§e§f§d§3§a§r"
    fun init() {
        val ui = KtUIBuilder.createUI("FIUI", "§6强化", 6, false)
        for (i in arrayOf(0, 1, 2, 3, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 17, 18, 19, 20, 21, 23, 24, 25, 26, 30, 31, 32, 39, 40, 41, 42, 48, 50)) {
            ui + i += KtItem.newItem() display (ItemBuilder create Material.getMaterial(160) durability 15 name "")()
        }
        for (i in arrayOf(27, 28, 29, 33, 34, 35, 36, 38, 42, 44, 45, 46, 47, 51, 52, 53)) {
            ui + i += KtItem.newItem() display (ItemBuilder create Material.getMaterial(416) name "")()
        }
        ui + ITEM_SLOT += KtItem.newEmptySlot()
        ui + STONE_SLOT += KtItem.newEmptySlot()
        ui + PROT_SLOT += KtItem.newEmptySlot()
        ui + RESULT_SLOT += KtItem.newEmptySlot()
        ui + LUCKY_SLOT += KtItem.newEmptySlot()
        for (i in BUTTON_SLOT) {
            ui + i += KtItem.newItem().display(
                    (ItemBuilder create Material.getMaterial(388)
                            name "§b>>§7点击强化§b<<"
                            lore "§0xiangqian")()).click { p, s -> onClick(p, s) }
        }
        ui + 13 += KtItem.newItem() display (ItemBuilder create Material.getMaterial(160)
                name "§e§l[§c§l强化教程§e§l]" lore (
                mutableListOf(
                        "&7上面幸运石&c,&7下面保护符",
                        "&7左边放装备&c,&7右边放强化石",
                        "&0qianneng",
                        "",
                        "&b仅用强化石和装备也可以强化哦",
                        "&a装备超过&b6&a级,强化失败会掉级",
                        "&a装备超过&b10&a级,强化失败可能会爆掉",
                        "&c要利用好幸运石和保护符"
                ).map { ChatColor.translateAlternateColorCodes('&', it) }.toTypedArray()
                )
                )()
        ui onClose { p, s ->
            Utils.safeGiveItem(p, s.inventory.getItem(ITEM_SLOT))
            Utils.safeGiveItem(p, s.inventory.getItem(STONE_SLOT))
            Utils.safeGiveItem(p, s.inventory.getItem(PROT_SLOT))
            Utils.safeGiveItem(p, s.inventory.getItem(RESULT_SLOT))
            Utils.safeGiveItem(p, s.inventory.getItem(LUCKY_SLOT))
        }
        UIManager.RegisterUI(ui.build())
    }

    @JvmStatic
    fun onClick(p: Player, s: Snapshot<*>) {
        val result = s.inventory.getItem(RESULT_SLOT)
        if (result != null && result.type != Material.AIR) {
            p msg "§c请移除成果栏中的物品后再强化"
            return
        }
        val item = s.inventory.getItem(ITEM_SLOT)
        if (item == null || item.type == Material.AIR) {
            return
        }
        if (item.amount > 1) {
            p msg "§c被强化的物品数量不能超过1"
            return
        }
        val info = SettingManager.get(item)
        if (info == null) {
            p msg "§c这个物品不能强化"
            return
        }
        val stone = s.inventory.getItem(STONE_SLOT)
        if (stone == null || stone.type == Material.AIR) {
            return
        }
        val stonedata = StoneManager.getStone(stone)
        if (stonedata == null) {
            p msg "§c你没有放入强化石"
            return
        }
        val proitem = s.inventory.getItem(PROT_SLOT)
        var protect = false
        if (proitem != null && proitem.type != Material.AIR) {
            if (StoneManager.isProtection(proitem)) {
                protect = true
            } else {
                p msg "§c你放入的不是保护符"
                return
            }
        }
        val luckitem = s.inventory.getItem(LUCKY_SLOT)
        var luckdata: ChanceItem? = null
        if (luckitem != null && luckitem.type != Material.AIR) {
            luckdata = StoneManager.getLucky(luckitem)
            if (luckdata == null) {
                p msg "§c你放入的不是幸运石"
                return
            }
        }
        val targetlv = if (stonedata.targetLevel == 0) {info.level + 1} else {
            stonedata.targetLevel
        }
        val setting = info.setting
        if (targetlv > setting.maxLevel) {
            p msg "§c这个物品已经强化到最高等级了"
            return
        }
        val levelInfo = setting.levelInfos[targetlv]
        if (levelInfo == null) {
            p msg "§c无法强化 没有对应的强化等级 请咨询管理员"
            return
        }
        var chance = stonedata.chances[targetlv]
        if (chance == null) {
            p msg "§c这个物品不能用这个强化石强化"
            return
        }
        if (luckdata != null) {
            chance += luckdata.chance
        }

        if (stone.amount > 1) {
            stone.amount--
            s.inventory.setItem(STONE_SLOT, stone)
        } else {
            s.inventory.setItem(STONE_SLOT, null)
        }
        if (proitem != null) {
            if (proitem.amount > 1) {
                proitem.amount--
                s.inventory.setItem(PROT_SLOT, proitem)
            } else {
                s.inventory.setItem(PROT_SLOT, null)
            }
        }
        if (luckitem != null) {
            if (luckitem.amount > 1) {
                luckitem.amount--
                s.inventory.setItem(LUCKY_SLOT, luckitem)
            } else {
                s.inventory.setItem(LUCKY_SLOT, null)
            }
        }
        if (Math.random() <= chance) {
            p msg "§6强化成功"
            ++info.level
            val item = setItemLevel(item, info)
            s.inventory.setItem(RESULT_SLOT, item)
            s.inventory.setItem(ITEM_SLOT, null)
        } else {
            s.inventory.setItem(ITEM_SLOT, null)
            if (targetlv <= 7) {
                s.inventory.setItem(RESULT_SLOT, item)
            } else if (targetlv <= 11) {
                if (Math.random() < 0.5) {
                    info.level -= 2
                } else {
                    info.level--
                }
                val item = setItemLevel(item, info)
                s.inventory.setItem(RESULT_SLOT, item)
            } else {
                if (!protect) {
                    s.inventory.setItem(RESULT_SLOT, null)
                } else s.inventory.setItem(RESULT_SLOT, item)
            }
            p msg "§c强化失败"
        }
    }

    fun setItemLevel(item: ItemStack, info: Info): ItemStack {
        val item = item.clone()
        val wtag = NbtFactory.fromItemTag(item)
        val tag = if (wtag == null) {
            NbtFactory.ofCompound("tag")
        } else {
            NbtFactory.asCompound(wtag)
        }
        tag.put(NBT_LEVEL_KEY, info.level)
        NbtFactory.setItemTag(item, tag)
        val im = +item
        val lore = if (im.hasLore()) im.lore else mutableListOf()
        val iterator = lore.iterator()
        while (iterator.hasNext()) {
            val s = iterator.next()
            if (s.contains(FOX_LORE_PREFIX)) {
                iterator.remove()
            }
        }
        val levelInfo = info.getLevelInfo() ?: return item
        for (s in levelInfo.lore) {
            lore += FOX_LORE_PREFIX + s
        }
        im.lore = lore
        if (TemplateManager.EditItemName) {
            var name = im.displayName
            if (name == null) {
                name = "§e+${info.level}"
            } else {
                val tmpe = name.split(" §e[+]".toRegex(), 2)
                name = "$name §e+${info.level}"
                if (tmpe.size == 2) {
                    try {
                        val lv = tmpe.last().toInt()
                        name = "${tmpe[0]} §e+${info.level}"
                    } catch (e: NumberFormatException) {
                    }
                }
            }
            im.displayName = name
        }
        item += im
        return item
    }
}