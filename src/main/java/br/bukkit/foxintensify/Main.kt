package br.bukkit.foxintensify

import Br.API.GUI.Ex.UIManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    override fun onEnable() {
        PLUGIN = this
        StoneManager.loadConfig()
        TemplateManager.loadConfig()
        SettingManager.loadConfig()
        IntensifyUI.init()
        Bukkit.getPluginManager().registerEvents(SettingManager, this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    companion object {
        private lateinit var PLUGIN: Main
        fun getPlguin(): Main = PLUGIN
    }

    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean {
        if(sender is Player){
            UIManager.openUI(sender,"FIUI")
        }

        return true
    }
}
