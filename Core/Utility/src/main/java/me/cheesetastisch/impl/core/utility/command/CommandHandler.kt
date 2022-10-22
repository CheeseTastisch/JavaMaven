package me.cheesetastisch.impl.core.utility.command

import io.github.classgraph.ClassGraph
import me.cheesetastisch.core.bootstrap.ICore
import me.cheesetastisch.core.message.IMessageServiceProvider
import me.cheesetastisch.core.utility.command.Command
import me.cheesetastisch.core.utility.command.ICommandHandler
import me.cheesetastisch.core.utility.listener.ListenerTrigger
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class CommandHandler(private val core: ICore) : ICommandHandler {

    private val execution = mutableListOf<Command>()

    fun registerCommands() {
        this.core.logger.info("Registering commands...")

        val count = ClassGraph()
            .enableClassInfo()
            .scan()
            .getClassInfo(Command::class.java.name)
            .subclasses
            .asSequence()
            .filter { !it.isAbstract }
            .map { it.loadClass() }
            .map { it.asSubclass(Command::class.java) }
            .filter { !this.isRegistered(it) }
            .map {
                try {
                    it.getDeclaredConstructor(ICore::class.java).newInstance(this.core)
                } catch (e: NoSuchMethodException) {
                    try {
                        it.getDeclaredConstructor().newInstance()
                    } catch (ex: NoSuchMethodException) {
                        this.core.logger.error(
                            "Couldn't register command ${it.simpleName} in because the class doesn't " +
                                    "have any correct constructor (empty or with single ICore)."
                        )
                        null
                    }
                }
            }
            .filterNotNull()
            .onEach { execution.add(it) }
            .count()

        this.core.logger.info("Registered $count commands.")
    }

    private fun getCommand(name: String): Command? = execution.firstOrNull { it.name == name || name in it.aliases }

    private fun isRegistered(command: Class<out Command>) = execution.any { it.javaClass == command }

    private fun isRegistered(instance: Command) = isRegistered(instance.javaClass)

    override fun registerCommandInstance(instance: Command) {
        if (this.isRegistered(instance)) throw IllegalArgumentException("Can't register command twice.")

        this.execution.add(instance)
    }

    @ListenerTrigger(PlayerCommandPreprocessEvent::class)
    fun playerCommandPreProcess(event: PlayerCommandPreprocessEvent) {
        event.isCancelled = true
        val split = event.message.split(" ")
        val command = this.getCommand(split[0])

        if (command == null) {
            event.player.sendMessage(this.core.getServiceProvider(IMessageServiceProvider::class)
                .getMessage("core.utility.command.unknown", "command=>${split[0]}"))
            return
        }

        command.execute(event.player, split[0], split.toTypedArray().copyOfRange(1, split.size))
    }

}