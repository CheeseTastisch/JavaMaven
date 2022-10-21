package me.cheesetastisch.impl.core.database

import com.surrealdb.connection.SurrealConnection
import com.surrealdb.connection.SurrealWebSocketConnection
import com.surrealdb.driver.AsyncSurrealDriver
import com.surrealdb.driver.model.QueryResult
import com.surrealdb.driver.model.patch.Patch
import me.cheesetastisch.core.bootstrap.ICore
import me.cheesetastisch.core.bootstrap.provider.AbstractServiceProvider
import me.cheesetastisch.core.bootstrap.provider.ServiceProvider
import me.cheesetastisch.core.bootstrap.util.configuration.ConfigurationFile
import me.cheesetastisch.core.bootstrap.util.configuration.YamlConfigurationProvider
import me.cheesetastisch.core.database.Completion
import me.cheesetastisch.core.database.ISurrealServiceProvider
import me.cheesetastisch.core.database.UnitCompletion
import java.io.File

@Suppress("unused")
@ServiceProvider(ISurrealServiceProvider::class)
class SurrealServiceProvider(core: ICore) : AbstractServiceProvider(core), ISurrealServiceProvider {

    private val configurationFile: ConfigurationFile

    private var connection: SurrealConnection? = null
    private var driver: AsyncSurrealDriver? = null

    init {
        this.configurationFile =
            ConfigurationFile(File(core.dataFolder, "surreal.yml"), YamlConfigurationProvider::class.java)
        this.configurationFile.configurationProvider.load(
            mapOf(
                "host" to "127.0.0.1",
                "port" to 8080,
                "tls" to false,
                "timeout" to 30,
                "username" to "root",
                "password" to "root",
                "namespace" to "project",
                "database" to "project"
            )
        )
    }

    override fun load() {
        this.core.logger.info("Connection to SurrealDB...")

        this.connection = SurrealWebSocketConnection(
            this.configurationFile.configurationProvider.getString("host"),
            this.configurationFile.configurationProvider.getInteger("port"),
            this.configurationFile.configurationProvider.getBoolean("tls")
        )
        this.connection!!.connect(this.configurationFile.configurationProvider.getInteger("timeout"))

        this.driver = AsyncSurrealDriver(this.connection)
        this.driver!!.signIn(
            this.configurationFile.configurationProvider.getString("username"),
            this.configurationFile.configurationProvider.getString("password")
        ).get()

        this.driver!!.use(
            this.configurationFile.configurationProvider.getString("namespace"),
            this.configurationFile.configurationProvider.getString("password")
        ).get()

        if (this.connected) this.core.logger.info("Connected to SurrealDB.")
        else this.core.logger.error("Couldn't connect to SurrealDB.")
    }

    override fun disable() {
        this.core.logger.info("Disconnecting from SurrealDB...")

        if (!this.connected) {
            this.core.logger.warn("SurrealDB isn't connected...")
            return
        }

        try {
            this.connection!!.disconnect()
            this.core.logger.info("Disconnected from SurrealDB.")
        } catch (e: Exception) {
            this.core.logger.error("Couldn't disconnect from SurrealDB.")
        } finally {
            this.connection = null
            this.driver = null
        }
    }

    private val saveDriver: AsyncSurrealDriver
        get() = this.driver ?: throw NullPointerException("The SurrealDB driver isn't initialized")

    override val connected: Boolean
        get() = this.connection != null && this.driver != null

    override fun let(key: String, value: String) = UnitCompletion(this.saveDriver.let(key, value))

    override fun <T : Any> query(
        query: String,
        type: Class<T>,
        args: Map<String, String>
    ): Completion<List<QueryResult<T>>> = Completion(this.saveDriver.query(query, args, type))

    override fun <T : Any> select(thing: String, type: Class<T>): Completion<List<T>> =
        Completion(this.saveDriver.select(thing, type))

    override fun <T : Any> create(thing: String, data: T): Completion<Unit> =
        UnitCompletion(this.saveDriver.create(thing, data))

    override fun <T : Any> update(thing: String, data: T): Completion<Unit> =
        UnitCompletion(this.saveDriver.update(thing, data))

    override fun <T : Any, P : Any> change(thing: String, data: T, type: Class<P>): Completion<List<P>> =
        Completion(this.saveDriver.change(thing, data, type))

    override fun patch(thing: String, patch: List<Patch>): Completion<Unit> =
        UnitCompletion(this.saveDriver.patch(thing, patch))

    override fun delete(thing: String): Completion<Unit> =
        UnitCompletion(this.saveDriver.delete(thing))

}