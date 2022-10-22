package me.cheesetastisch.impl.core.message

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import me.cheesetastisch.core.bootstrap.ICore
import me.cheesetastisch.core.bootstrap.provider.AbstractServiceProvider
import me.cheesetastisch.core.bootstrap.provider.ServiceProvider
import me.cheesetastisch.core.database.ISurrealServiceProvider
import me.cheesetastisch.core.database.select
import me.cheesetastisch.core.xjkl.future.CompletableFuture
import me.cheesetastisch.core.xjkl.future.Future
import me.cheesetastisch.core.message.IMessageServiceProvider
import me.cheesetastisch.impl.core.message.model.Message
import net.md_5.bungee.api.ChatColor
import java.awt.Color
import java.text.MessageFormat
import java.util.concurrent.TimeUnit

@ServiceProvider(interfaceClass = IMessageServiceProvider::class, dependProviders = [ISurrealServiceProvider::class])
class MessageServiceProvider(core: ICore) : AbstractServiceProvider(core), IMessageServiceProvider {

    private val messageNotFound = "§cDiese Nachricht ({0}) konnte nicht gefunden werden."

    private val globalPlaceholders = mutableMapOf<String, String>()
    private val cache: Cache<String, String> = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build()

    override fun getMessageAsync(key: String, vararg placeholders: String): Future<String> {
        val future = CompletableFuture<String>()

        this.getFromDatabase(key)
            .then { dbMessage ->
                if (dbMessage == null) {
                    future.complete(MessageFormat.format(messageNotFound, key))
                    return@then
                }

                var message: String = dbMessage

                placeholders.forEach {
                    val split = it.split("=>")
                    message = message.replace("%${split[0]}%", it.substring(split[0].length + 2))
                }

                var found = 0
                var run = 0
                globalPlaceholders.forEach { (k, v) ->
                    if (message.contains("%$k%")) {
                        found++

                        this.getFromDatabase(v)
                            .then global@{ result ->
                                run++

                                if (result == null) return@global
                                message = message.replace("%$k%", result)

                                if (found == run) future.complete(this.replaceColors(message, '&'))
                            }
                    }
                }

                if (found == 0) future.complete(this.replaceColors(message, '&'))

            }

        return future
    }

    override fun replaceColors(message: String, code: Char): String {
        var mutMessage = ChatColor.translateAlternateColorCodes(code, message)
        Regex("\\$code#[0-9A-Fa-f]{6}")
            .findAll(mutMessage, 0)
            .sortedByDescending { it.range.first }
            .forEach {
                val before = mutMessage.substring(0, it.range.first)
                val after = mutMessage.substring(it.range.last + 1)
                val hex = it.value.replaceFirst(code.toString(), "")

                mutMessage = before + ChatColor.of(Color.decode(hex)) + after
            }


        return mutMessage
    }

    private fun getFromDatabase(key: String): Future<String?> {
        val future = CompletableFuture<String?>()
        val message = cache.getIfPresent(key)
        if (message != null) future.complete(message)
        else {
            this.core.getServiceProvider(ISurrealServiceProvider::class).select<Message>("message:$key")
                .then { result ->
                    if (result.isEmpty()) {
                        future.complete(null)
                        return@then
                    }

                    future.complete(result[0].message)
                }
        }

        return future
    }

}