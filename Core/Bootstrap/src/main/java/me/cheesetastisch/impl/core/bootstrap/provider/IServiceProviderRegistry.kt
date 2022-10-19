package me.cheesetastisch.impl.core.bootstrap.provider

import kotlin.reflect.KClass

interface IServiceProviderRegistry {

    fun getKotlinServiceProviders(): Map<KClass<out IServiceProvider>, AbstractServiceProvider> =
        getServiceProviders().map { (k, v) -> Pair(k.kotlin, v) }.toMap()

    fun getServiceProviders(): Map<Class<out IServiceProvider>, AbstractServiceProvider>

    operator fun <T : IServiceProvider> get(`class`: KClass<T>): T? = get(`class`.java)

    operator fun <T : IServiceProvider> get(`class`: Class<T>): T?

}