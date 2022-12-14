package me.cheesetastisch.core.database

import com.surrealdb.driver.model.QueryResult
import com.surrealdb.driver.model.patch.Patch
import me.cheesetastisch.core.bootstrap.provider.IServiceProvider
import me.cheesetastisch.core.xjkl.future.Future
import kotlin.reflect.KClass

@Suppress("unused")
interface ISurrealServiceProvider : IServiceProvider {

    val connected: Boolean

    fun let(key: String, value: String): Future<Unit>

    fun <T : Any> query(
        query: String,
        type: Class<T>,
        args: Map<String, String> = emptyMap()
    ): Future<List<QueryResult<T>>>

    fun <T : Any> query(query: String, type: KClass<T>, args: Map<String, String> = emptyMap()) =
        this.query(query, type.java, args)

    fun <T : Any> select(thing: String, type: Class<T>): Future<List<T>>

    fun <T : Any> select(thing: String, type: KClass<T>) = this.select(thing, type.java)

    fun <T : Any> create(thing: String, data: T): Future<Unit>

    fun <T : Any> update(thing: String, data: T): Future<Unit>

    fun <T : Any, P : Any> change(thing: String, data: T, type: Class<P>): Future<List<P>>

    fun patch(thing: String, patch: List<Patch>): Future<Unit>

    fun delete(thing: String): Future<Unit>

}

// Extensions

@Suppress("unused")
inline fun <reified T : Any> ISurrealServiceProvider.query(
    query: String,
    args: Map<String, String> = emptyMap()
) = this.query(query, T::class.java, args)

@Suppress("unused")
inline fun <reified T : Any> ISurrealServiceProvider.select(thing: String) = this.select(thing, T::class.java)

@Suppress("unused")
inline fun <T : Any, reified P : Any> ISurrealServiceProvider.change(thing: String, data: T) =
    this.change(thing, data, P::class.java)