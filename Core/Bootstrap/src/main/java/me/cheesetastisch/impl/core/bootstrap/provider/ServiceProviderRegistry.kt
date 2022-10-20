package me.cheesetastisch.impl.core.bootstrap.provider

import me.cheesetastisch.core.bootstrap.ICore
import me.cheesetastisch.core.bootstrap.provider.*

class ServiceProviderRegistry(private val core: ICore) : IServiceProviderRegistry {

    private val serviceProviders = mutableMapOf<Class<out IServiceProvider>, AbstractServiceProvider>()

    override fun getServiceProviders(): Map<Class<out IServiceProvider>, AbstractServiceProvider> =
        this.serviceProviders

    fun registerProviders() {
        this.core.logger.info("Registering ServiceProviders...")
        val newServiceProviders = mutableListOf<Class<out IServiceProvider>>()

        this.core.annotationScanner.scanClasses(ServiceProvider::class.java, "*")
            .filter { it.superclass.equals(AbstractServiceProvider::class.java) }
            .map { it.asSubclass(AbstractServiceProvider::class.java) }
            .forEach {
                try {
                    val constructor = it.getConstructor(ICore::class.java)
                    val instance = constructor.newInstance(this.core)
                    val interfaceClass = it.getAnnotation(ServiceProvider::class.java).interfaceClass

                    if (this.serviceProviders.containsKey(interfaceClass.java)) {
                        this.core.logger.warn("ServiceProvider \"${interfaceClass.simpleName}\" is already registered.")
                        return@forEach
                    }

                    serviceProviders[interfaceClass.java] = instance
                    newServiceProviders + interfaceClass.java

                    this.core.logger.info("ServiceProvider \"${interfaceClass.simpleName}\" registered!")
                } catch (e: Exception) {
                    this.core.logger.error(
                        "An error occurred trying to register the ServiceProvider \"${it.simpleName}\"",
                        e
                    )
                }
            }

        this.core.logger.info("Registered ${newServiceProviders.size} ServiceProvider(s).")
        this.load()
    }

    private fun load() {
        this.core.logger.info("Loading ServiceProviders...")

        stateProviders(ServiceProviderState.UNLOADED, ServiceProviderState.LOADED, { name, provider ->
            try {
                provider.load()
            } catch (e: Throwable) {
                this.core.logger.error(
                    "An error occurred while trying to load the ServiceProvider \"$name\"",
                    e
                )
                return@stateProviders false
            }

            this.core.logger.info("ServiceProvider \"$name\" loaded!")
            return@stateProviders true
        }) {
            this.core.logger.warn("The ServiceProviders")
            it.forEach { provider -> this.core.logger.warn("- $provider") }
            this.core.logger.warn("could not be loaded because of dependency issues.")
            this.core.logger.warn("Do they depend each other, or depend not registered ServiceProviders?")
        }

        this.core.logger.info("Loaded ${this.serviceProviders.size} ServiceProvider(s).")
    }

    fun enableProviders() {
        this.core.logger.info("Enabling ServiceProviders...")

        stateProviders(ServiceProviderState.LOADED, ServiceProviderState.ENABLED, { name, provider ->
            try {
                provider.enable()
            } catch (e: Throwable) {
                this.core.logger.error(
                    "An error occurred while trying to enable the ServiceProvider \"$name\"",
                    e
                )
                return@stateProviders false
            }

            this.core.logger.info("ServiceProvider \"$name\" enabled!")
            return@stateProviders true
        }) { }

        this.core.logger.info("Enabled ${this.serviceProviders.size} ServiceProvider(s).")
    }

    fun disableProviders() {
        this.core.logger.info("Disabling ServiceProviders...")

        stateProviders(ServiceProviderState.ENABLED, ServiceProviderState.DISABLED, { name, provider ->
            try {
                provider.disable()
            } catch (e: Throwable) {
                this.core.logger.error(
                    "An error occurred while trying to disable the ServiceProvider \"$name\"",
                    e
                )
                return@stateProviders false
            }

            this.core.logger.info("ServiceProvider \"$name\" disabled!")
            return@stateProviders true
        }) { }

        this.core.logger.info("Disabled ${this.serviceProviders.size} ServiceProvider(s).")
    }

    private fun stateProviders(
        currentState: ServiceProviderState,
        toState: ServiceProviderState,
        call: (name: String, provider: AbstractServiceProvider) -> Boolean,
        dependencyIssue: (List<String>) -> Unit
    ) {
        val unchangedProviders = this.serviceProviders.keys.toMutableList()
        var failCount = 0

        while (unchangedProviders.isNotEmpty()) {
            if (failCount == 3) {
                dependencyIssue(unchangedProviders.map { it.simpleName })
                unchangedProviders.forEach { this.serviceProviders - it }
            }

            val newChanged = mutableListOf<Class<out IServiceProvider>>()
            unchangedProviders.forEach {
                val instance = this.serviceProviders[it] ?: return@forEach
                if (instance.state != currentState) {
                    newChanged + it
                    return@forEach
                }

                val serviceProviderClass = instance::class.java
                val annotation = serviceProviderClass.getAnnotation(ServiceProvider::class.java)!!

                if (annotation.dependProviders.all { dependency ->
                        this.serviceProviders[dependency.java]?.state == toState
                    }) {
                    if (!call(it.simpleName, instance)) {
                        this.serviceProviders - instance
                        return@forEach
                    }

                    instance.state = toState
                    newChanged + it
                }
            }

            if (newChanged.isEmpty()) failCount++
            else failCount = 0

            unchangedProviders.removeAll(newChanged.toSet())
        }
    }

    override fun <T : IServiceProvider> get(`class`: Class<T>): T? {
        TODO("Not yet implemented")
    }
}