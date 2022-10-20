package me.cheesetastisch.core.bootstrap.bootstrap

import me.cheesetastisch.core.bootstrap.Core
import org.bukkit.plugin.java.JavaPlugin

class Bootstrap : JavaPlugin() {

    val core by lazy { Core(this) }

    override fun onLoad() {
        this.core.load()
    }

    override fun onEnable() {
        this.core.enable()
    }

    override fun onDisable() {
        this.core.disable()
    }

}