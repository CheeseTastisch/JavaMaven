package me.cheesetastisch.impl.core.bootstrap

import me.cheesetastisch.impl.core.bootstrap.provider.IServiceProvider
import me.cheesetastisch.impl.core.bootstrap.provider.IServiceProviderRegistry
import me.cheesetastisch.impl.core.bootstrap.util.annotation.IAnnotationScanner
import me.cheesetastisch.impl.core.bootstrap.util.log.ILogger
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.annotations.Contract
import java.io.File
import kotlin.reflect.KClass

interface ICore {

    val annotationScanner: IAnnotationScanner
        @Contract(pure = true)
        get

    val serviceProviderRegistry: IServiceProviderRegistry
        @Contract(pure = true)
        get

    val logger: ILogger
        @Contract(pure = true)
        get

    val javaPlugin: JavaPlugin
        @Contract(pure = true)
        get

    val dataFolder: File
        @Contract(pure = true)
        get

    @Contract(pure = true)
    fun <T : IServiceProvider> getServiceProvider(`class`: Class<T>): T? = serviceProviderRegistry[`class`]

    @Contract(pure = true)
    fun <T : IServiceProvider> getServiceProvider(`class`: KClass<T>): T? = serviceProviderRegistry[`class`]

}