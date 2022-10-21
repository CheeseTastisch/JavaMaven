package me.cheesetastisch.impl.core.bootstrap.bootstrap

import me.cheesetastisch.impl.core.bootstrap.Core
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