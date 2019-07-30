package br.bukkit.foxintensify

import Br.RPGAttribute.Attribute
import java.util.*

class Template(list: List<String>) {
    val attr = EnumMap<Attribute.State, Double>(Attribute.State::class.java)

    init {
        for (s in list) {
            val arr = s.split(":( )*".toRegex())
            if (arr.size >= 2) {
                val st = Attribute.State.getState(arr[0])
                val value = if (arr[1].contains('%')) {
                    arr[1].replace("%","").toDouble()
                } else {
                    arr[1].toDouble()
                }
                attr[st] = value
            }
        }
    }

    operator fun times(lv: Int): Map<Attribute.State, Double> {
        val map = EnumMap<Attribute.State, Double>(Attribute.State::class.java)
        for ((k, v) in attr) {
            map[k] = v * lv
        }
        return map
    }

    override fun toString(): String {
        return "Template(attr=$attr)"
    }
}

