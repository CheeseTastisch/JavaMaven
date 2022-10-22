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
                this.core.logger.info("5")
                if (dbMessage == null) {
                    this.core.logger.info("6")
                    future.complete(MessageFormat.format(messageNotFound, key))
                    return@then
                }

                this.core.logger.info("7")
                var message: String = dbMessage

                this.core.logger.info("8")
                placeholders.forEach {
                    val split = it.split("=>")
                    message = message.replace("%${split[0]}%", it.substring(split[0].length + 2))
                }

                this.core.logger.info("9")

                var found = 0
                var run = 0
                this.core.logger.info("10")
                globalPlaceholders.forEach { (k, v) ->
                    this.core.logger.info("11 $k => $v")
                    if (message.contains("%$k%")) {
                        found++

                        this.getFromDatabase(v)
                            .then global@{ result ->
                                this.core.logger.info("12 $k")
                                run++

                                if (result == null) return@global
                                message = message.replace("%$k%", result)
                                this.core.logger.info("1^3")

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
                    this.core.logger.info("1")
                    if (result.isEmpty()) {
                        this.core.logger.info("2")
                        future.complete(null)
                        this.core.logger.info("3")
                        return@then
                    }

                    this.core.logger.info("4")
                    future.complete(result[0].message)
                }
        }

        return future
    }

}