package me.cheesetastisch.impl.core.utility.listener

import io.github.classgraph.ClassGraph
import me.cheesetastisch.core.bootstrap.ICore
import me.cheesetastisch.core.utility.listener.IListenerHandler
import me.cheesetastisch.core.utility.listener.ListenerTrigger
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.plugin.EventExecutor
import java.lang.reflect.Method
import kotlin.reflect.KClass

class ListenerHandler(private val core: ICore) : IListenerHandler {

    private val instances = mutableMapOf<Class<*>, Any>()
    private val listeners = mutableMapOf<Class<*>, MutableList<Method>>()

    fun loadListeners() {
        this.core.logger.info("Loading listeners...")
        this.core.annotationScanner.scanMethods(ListenerTrigger::class)
            .forEach {
                val annotation = it.getAnnotation(ListenerTrigger::class.java)

                val declaringClass = it.declaringClass
                if (it.parameterCount != 1 || it.parameterTypes[0] != annotation.event.java) {
                    this.core.logger.error(
                        "Couldn't register listener for " + annotation.event.simpleName + " in "
                                + declaringClass.simpleName + " because the method doesn't have the right parameters."
                    )
                }

                if (declaringClass in this.instances) {
                    this.listeners.putIfAbsent(annotation.event.java, mutableListOf())
                    this.listeners[annotation.event.java]!!.add(it)
                    return@forEach
                }

                val constructor = try {
                    declaringClass.getDeclaredConstructor(ICore::class.java)
                } catch (e: NoSuchMethodException) {
                    try {
                        declaringClass.getDeclaredConstructor()
                    } catch (ex: NoSuchMethodException) {
                        this.core.logger.error(
                            "Couldn't register listener for " + annotation.event.simpleName + " in "
                                    + declaringClass.simpleName + " because the class doesn't have the correct constructor."
                        )
                        null
                    }
                } ?: return@forEach

                val instance = constructor.newInstance(this.core)
                this.instances[declaringClass] = instance

                this.listeners.putIfAbsent(annotation.event.java, mutableListOf())
                this.listeners[annotation.event.java]!!.add(it)
            }
        this.core.logger.info("Loaded ${listeners.size} listeners.")
    }

    fun registerEvents() {
        this.core.logger.info("Registering events...")

        val listener: org.bukkit.event.Listener = object : org.bukkit.event.Listener {}
        val executor = EventExecutor { _, event -> fireEvent(event) }
        var counter = 0

        ClassGraph()
            .enableClassInfo()
            .scan()
            .getClassInfo(Event::class.java.name)
            .subclasses
            .asSequence()
            .filter { !it.isAbstract }
            .forEach {
                val eventClass = it.loadClass()
                if (!eventClass.declaredMethods
                        .any { method -> method.parameterCount == 0 && method.name == "getHandlers" } ||
                    this.listeners.none { (`class`, _) -> `class`.isAssignableFrom(eventClass) }
                ) return@forEach

                this.core.logger.info(eventClass.simpleName)

                @Suppress("UNCHECKED_CAST")
                Bukkit.getPluginManager().registerEvent(
                    eventClass as Class<out Event>,
                    listener,
                    EventPriority.NORMAL,
                    executor,
                    this.core.javaPlugin
                )

                counter++
            }

        this.core.logger.info("Registered $counter Events.")
    }

    private fun fireEvent(event: Event) {
        val eventClass = event::class.java

        this.listeners
            .filter { (`class`, _) -> `class`.isAssignableFrom(eventClass) }
            .map { it.value }
            .flatten()
            .forEach {
                val instance = this.instances[it.declaringClass] ?: return@forEach
                it.invoke(instance, event)
            }
    }

    override fun <T : Any> registerListenerInstance(`class`: KClass<out T>, instance: T) {
        this.instances[`class`.java] = instance
    }

}