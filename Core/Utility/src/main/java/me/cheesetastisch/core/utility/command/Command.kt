package me.cheesetastisch.core.utility.command

import org.bukkit.command.CommandSender

abstract class Command {

    abstract val name: String
    open val aliases: Array<String> = emptyArray()
    open val permission: String? = null

    abstract fun execute(sender: CommandSender, label: String, args: Array<String>)

    open fun tabComplete(sender: CommandSender, args: Array<String>, completions: MutableList<String>) {}

}