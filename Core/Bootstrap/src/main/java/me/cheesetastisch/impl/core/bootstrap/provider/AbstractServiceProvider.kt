package me.cheesetastisch.impl.core.bootstrap.provider

import me.cheesetastisch.impl.core.bootstrap.ICore

abstract class AbstractServiceProvider(
    val core: ICore,
    var state: ServiceProviderState = ServiceProviderState.UNLOADED
) {

    fun load() {}

    fun enable() {}

    fun disable() {}

}