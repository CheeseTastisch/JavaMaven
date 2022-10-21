package me.cheesetastisch.core.bootstrap.provider

import me.cheesetastisch.core.bootstrap.ICore

abstract class AbstractServiceProvider(val core: ICore) {

    var state: ServiceProviderState = ServiceProviderState.UNLOADED

    open fun load() {}

    open fun enable() {}

    open fun disable() {}

}