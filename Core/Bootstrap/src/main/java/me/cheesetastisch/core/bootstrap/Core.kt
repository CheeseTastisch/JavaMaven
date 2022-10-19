package me.cheesetastisch.core.bootstrap

import me.cheesetastisch.core.bootstrap.provider.ServiceProviderRegistry
import me.cheesetastisch.core.bootstrap.util.annotation.AnnotationScanner
import me.cheesetastisch.core.bootstrap.util.log.Logger
import me.cheesetastisch.impl.core.bootstrap.ICore
import me.cheesetastisch.impl.core.bootstrap.util.log.ILogger
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Core(override val javaPlugin: JavaPlugin) : ICore {

    override val annotationScanner = AnnotationScanner()
    override val serviceProviderRegistry = ServiceProviderRegistry(this)
    override val logger: ILogger = Logger(this)
    override val dataFolder: File = this.javaPlugin.dataFolder

    fun load() = this.serviceProviderRegistry.registerProviders()
    fun enable() = this.serviceProviderRegistry.enableProviders()
    fun disable() = this.serviceProviderRegistry.disableProviders()


}