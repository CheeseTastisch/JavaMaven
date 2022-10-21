package me.cheesetastisch.core.database

import com.surrealdb.driver.model.QueryResult
import com.surrealdb.driver.model.patch.Patch
import me.cheesetastisch.core.bootstrap.provider.IServiceProvider
import kotlin.reflect.KClass

interface ISurrealServiceProvider : IServiceProvider {

    val connected: Boolean

    fun let(key: String, value: String): Completion<Unit>

    fun <T : Any> query(
        query: String,
        type: Class<T>,
        args: Map<String, String> = emptyMap()
    ): Completion<List<QueryResult<T>>>

    fun <T : Any> query(query: String, type: KClass<T>, args: Map<String, String> = emptyMap()) =
        this.query(query, type.java, args)

    fun <T : Any> select(thing: String, type: Class<T>): Completion<List<T>>

    fun <T : Any> select(thing: String, type: KClass<T>) = this.select(thing, type.java)

    fun <T : Any> create(thing: String, data: T): Completion<Unit>

    fun <T : Any> update(thing: String, data: T): Completion<Unit>

    fun <T : Any, P : Any> change(thing: String, data: T, type: Class<P>): Completion<List<P>>

    fun patch(thing: String, patch: List<Patch>): Completion<Unit>

    fun delete(thing: String): Completion<Unit>

}

// Extensions
inline fun <reified T : Any> ISurrealServiceProvider.query(
    query: String,
    args: Map<String, String> = emptyMap()
) = this.query(query, T::class.java, args)

inline fun <reified T : Any> ISurrealServiceProvider.select(thing: String) = this.select(thing, T::class.java)

inline fun <T : Any, reified P : Any> ISurrealServiceProvider.change(thing: String, data: T) =
    this.change(thing, data, P::class.java)