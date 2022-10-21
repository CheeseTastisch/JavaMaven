package me.cheesetastisch.impl.core.utility

import me.cheesetastisch.core.bootstrap.ICore
import me.cheesetastisch.core.bootstrap.provider.AbstractServiceProvider
import me.cheesetastisch.core.bootstrap.provider.ServiceProvider
import me.cheesetastisch.core.utility.IUtilityServiceProvider
import me.cheesetastisch.impl.core.utility.listener.ListenerHandler

@ServiceProvider(IUtilityServiceProvider::class)
class UtilityServiceProvider(core: ICore) : AbstractServiceProvider(core), IUtilityServiceProvider {

    private val listenerHandler = ListenerHandler(this.core)

    override fun enable() {
        this.listenerHandler.loadListeners()
        this.listenerHandler.registerEvents()
    }

}