package me.cheesetastisch.impl.core.database

import com.surrealdb.driver.model.QueryResult
import com.surrealdb.driver.model.patch.Patch
import me.cheesetastisch.core.bootstrap.ICore
import me.cheesetastisch.core.bootstrap.provider.AbstractServiceProvider
import me.cheesetastisch.core.bootstrap.provider.ServiceProvider
import me.cheesetastisch.core.database.ISurrealServiceProvider
import me.cheesetastisch.core.database.ISyncSurrealServiceProvider

@Suppress("unused")
@ServiceProvider(ISyncSurrealServiceProvider::class, [ISurrealServiceProvider::class])
class SyncSurrealServiceProvider(core: ICore) : AbstractServiceProvider(core), ISyncSurrealServiceProvider {

    private val asyncProvider = this.core.getServiceProvider(ISurrealServiceProvider::class)
        ?: throw IllegalStateException("Async driver must be initialized for sync driver")

    override val connected: Boolean
        get() = this.asyncProvider.connected

    override fun let(key: String, value: String) = this.asyncProvider.let(key, value).getSync()

    override fun <T : Any> query(query: String, type: Class<T>, args: Map<String, String>): List<QueryResult<T>> =
        this.asyncProvider.query(query, type, args).getSync()

    override fun <T : Any> select(thing: String, type: Class<T>): List<T> =
        this.asyncProvider.select(thing, type).getSync()

    override fun <T : Any> create(thing: String, data: T) = this.asyncProvider.create(thing, data).getSync()

    override fun <T : Any> update(thing: String, data: T) = this.asyncProvider.update(thing, data).getSync()

    override fun <T : Any, P : Any> change(thing: String, data: T, type: Class<P>): List<P> =
        this.asyncProvider.change(thing, data, type).getSync()

    override fun patch(thing: String, patch: List<Patch>) = this.asyncProvider.patch(thing, patch).getSync()

    override fun delete(thing: String) = this.asyncProvider.delete(thing).getSync()
}