package me.cheesetastisch.core.utility

import me.cheesetastisch.core.bootstrap.provider.IServiceProvider
import me.cheesetastisch.core.utility.listener.IListenerHandler

interface IUtilityServiceProvider: IServiceProvider {

    val listenerHandler: IListenerHandler

}