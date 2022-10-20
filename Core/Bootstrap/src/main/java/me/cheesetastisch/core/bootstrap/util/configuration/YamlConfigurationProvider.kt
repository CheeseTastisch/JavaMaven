package me.cheesetastisch.core.bootstrap.util.configuration

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class YamlConfigurationProvider(private val file: File) : IFileConfigurationProvider {

    private var yamlConfiguration: YamlConfiguration? = null

    override fun load(defaults: Map<String, Any>) {
        val exists = file.exists()

        if (file.parentFile != null && !file.parentFile.exists()) file.parentFile.mkdirs()
        if (!exists) file.createNewFile()

        this.yamlConfiguration = YamlConfiguration.loadConfiguration(file)

        if (!exists) {
            defaults.forEach { (key, value) -> this.yamlConfiguration!![key] = value }
            this.yamlConfiguration!!.save(file)
        }
    }

    override fun getKeys(deep: Boolean): Set<String> {
        return this.yamlConfiguration?.getKeys(deep) ?: throw IllegalStateException()
    }

    override fun getString(key: String): String {
        return this.yamlConfiguration?.getString(key) ?: throw IllegalStateException()
    }

    override fun getStringList(key: String): List<String> {
        return this.yamlConfiguration?.getStringList(key) ?: throw IllegalStateException()
    }

    override fun getBoolean(key: String): Boolean {
        return this.yamlConfiguration?.getBoolean(key) ?: throw IllegalStateException()
    }

    override fun getInteger(key: String): Int {
        return this.yamlConfiguration?.getInt(key) ?: throw IllegalStateException()
    }

    override fun getLong(key: String): Long {
        return this.yamlConfiguration?.getLong(key) ?: throw IllegalStateException()
    }

    override fun getFloat(key: String): Float {
        return (this.yamlConfiguration?.get(key) ?: throw IllegalStateException()) as Float
    }

    override fun getChar(key: String): Char {
        return (this.yamlConfiguration?.get(key) ?: throw IllegalStateException()) as Char
    }

    override fun set(key: String, value: Any) {
        this.yamlConfiguration?.set(key, value)
    }

    override fun save() {
        this.yamlConfiguration?.save(file)
    }
}