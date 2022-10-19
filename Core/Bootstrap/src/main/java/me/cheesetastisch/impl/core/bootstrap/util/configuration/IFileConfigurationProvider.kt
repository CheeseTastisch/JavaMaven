package me.cheesetastisch.impl.core.bootstrap.util.configuration

interface IFileConfigurationProvider {
    
    fun load(defaults: Map<String, Any>)

    fun getKeys(deep: Boolean): Set<String>

    fun getString(key: String): String

    fun getStringList(key: String): List<String>

    fun getBoolean(key: String): Boolean

    fun getInteger(key: String): Int

    fun getLong(key: String): Long

    fun getFloat(key: String): Float

    fun getChar(key: String): Char

    fun set(key: String, value: Any)

    fun save()
    
}
