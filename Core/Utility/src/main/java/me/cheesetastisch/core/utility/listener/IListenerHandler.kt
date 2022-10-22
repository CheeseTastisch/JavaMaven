package me.cheesetastisch.core.utility.listener

import kotlin.reflect.KClass

interface IListenerHandler {

    fun <T : Any> registerListenerInstance(`class`: KClass<out T>, instance: T)

}