package me.cheesetastisch.impl.core.utility

import me.cheesetastisch.core.bootstrap.ICore
import me.cheesetastisch.core.bootstrap.provider.AbstractServiceProvider
import me.cheesetastisch.core.bootstrap.provider.ServiceProvider
import me.cheesetastisch.core.utility.IUtilityServiceProvider
import me.cheesetastisch.impl.core.utility.command.CommandHandler
import me.cheesetastisch.impl.core.utility.listener.ListenerHandler

@ServiceProvider(IUtilityServiceProvider::class)
class UtilityServiceProvider(core: ICore) : AbstractServiceProvider(core), IUtilityServiceProvider {

    override val listenerHandler = ListenerHandler(this.core)
    override val commandHandler = CommandHandler(this.core)

    override fun load() {
        this.listenerHandler.registerListenerInstance(this.commandHandler)
    }

    override fun enable() {
        this.listenerHandler.loadListeners()
        this.listenerHandler.registerEvents()
    }

}