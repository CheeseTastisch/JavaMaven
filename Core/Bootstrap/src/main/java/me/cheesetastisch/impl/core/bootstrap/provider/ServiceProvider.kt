package me.cheesetastisch.impl.core.bootstrap.provider

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ServiceProvider(
    val interfaceClass: KClass<out IServiceProvider>,
    val dependProviders: Array<KClass<out IServiceProvider>>
)
