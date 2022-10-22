package me.cheesetastisch.core.utility.listener

import org.bukkit.event.Event
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ListenerTrigger(val event: KClass<out Event>, val subevents: Boolean = true)
