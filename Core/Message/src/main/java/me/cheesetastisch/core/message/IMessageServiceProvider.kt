package me.cheesetastisch.core.message

import me.cheesetastisch.core.bootstrap.provider.IServiceProvider
import me.cheesetastisch.core.kotlin.future.Future

interface IMessageServiceProvider: IServiceProvider {

    fun getMessage(key: String, vararg placeholders: String = emptyArray()) =
        this.getMessageAsync(key, *placeholders).getSync()

    fun getMessageAsync(key: String, vararg placeholders: String = emptyArray()): Future<String>

    fun replaceColors(message: String, code: Char): String

    operator fun get(key: String, vararg placeholders: String = emptyArray()) =
        getMessage(key, *placeholders)

}