package me.cheesetastisch.impl.core.bootstrap

import me.cheesetastisch.impl.core.bootstrap.provider.IServiceProvider
import me.cheesetastisch.impl.core.bootstrap.provider.IServiceProviderRegistry
import me.cheesetastisch.impl.core.bootstrap.util.annotation.IAnnotationScanner
import me.cheesetastisch.impl.core.bootstrap.util.log.ILogger
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.reflect.KClass

interface ICore {

    val annotationScanner: IAnnotationScanner

    val serviceProviderRegistry: IServiceProviderRegistry

    val logger: ILogger

    val javaPlugin: JavaPlugin

    val dataFolder: File

    fun <T : IServiceProvider> getServiceProvider(`class`: Class<T>): T? = serviceProviderRegistry[`class`]

    fun <T : IServiceProvider> getServiceProvider(`class`: KClass<T>): T? = serviceProviderRegistry[`class`]

}