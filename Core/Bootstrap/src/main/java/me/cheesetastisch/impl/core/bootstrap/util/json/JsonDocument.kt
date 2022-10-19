package me.cheesetastisch.impl.core.bootstrap.util.json

import com.google.gson.*
import me.cheesetastisch.impl.core.bootstrap.util.scope.supplyNullable
import me.cheesetastisch.impl.core.bootstrap.util.scope.withThis
import org.jetbrains.annotations.Contract
import java.io.*
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.nio.file.Files

class JsonDocument(private var jsonObject: JsonObject) {

    private constructor() : this(JsonObject())

    constructor(jsonElement: JsonElement) : this(if (jsonElement.isJsonObject) jsonElement.asJsonObject else JsonObject())

    constructor(file: File) : this() {
        this.read(file)
    }

    constructor(string: String) : this() {
        this.read(string)
    }

    fun keys() = jsonObject.keySet().toList()

    fun size() = jsonObject.size()

    @Contract("-> this")
    fun clear() = this.withThis { jsonObject = JsonObject() }

    @Contract("_ -> this")
    fun remove(key: String) = this.withThis { jsonObject.remove(key) }

    @Contract("_ -> this")
    fun write(outputStream: OutputStream): JsonDocument =
        OutputStreamWriter(outputStream, StandardCharsets.UTF_8).use(this::write)

    @Contract("_ -> this")
    fun write(writer: Writer) = this.withThis { gson.toJson(jsonObject, writer) }

    @Contract("_ -> this")
    @Throws(IOException::class)
    fun write(file: File): JsonDocument = FileWriter(file).use(this::write)

    @Contract("_ -> this")
    fun read(inputStream: InputStream): JsonDocument =
        InputStreamReader(inputStream, StandardCharsets.UTF_8).use(this::read)

    @Contract("_ -> this")
    fun read(reader: Reader): JsonDocument {
        try {
            BufferedReader(reader).use { bufferedReader ->
                this.append(JsonParser.parseReader(bufferedReader).asJsonObject)
                return this
            }
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    @Contract("_ -> this")
    fun read(input: String) = this.withThis { this.append(gson.fromJson(input, JsonObject::class.java)) }

    @Contract("_ -> this")
    @Throws(FileNotFoundException::class)
    fun read(file: File): JsonDocument {
        if (!Files.exists(file.toPath())) return this
        return this.read(FileInputStream(file))
    }


    @Contract(pure = true)
    operator fun contains(key: String): Boolean {
        return jsonObject.has(key)
    }

    @Contract(pure = true)
    fun getDocument(key: String) = supplyNullable(jsonObject[key]) { if (it.isJsonObject) JsonDocument(it) else null }

    @Contract(pure = true)
    fun getObject(key: String) = supplyNullable(jsonObject[key]) { if (it.isJsonObject) it.asJsonObject else null }

    @Contract(pure = true)
    fun getPrimitive(key: String) =
        supplyNullable(jsonObject[key]) { if (it.isJsonPrimitive) it.asJsonPrimitive else null }

    @Contract(pure = true)
    fun getInt(key: String) = supplyNullable(getPrimitive(key)) { if (it.isNumber) it.asInt else null }

    @Contract(pure = true)
    fun getDouble(key: String) = supplyNullable(getPrimitive(key)) { if (it.isNumber) it.asDouble else null }

    @Contract(pure = true)
    fun getFloat(key: String) = supplyNullable(getPrimitive(key)) { if (it.isNumber) it.asFloat else null }

    @Contract(pure = true)
    fun getByte(key: String) = supplyNullable(getPrimitive(key)) { if (it.isNumber) it.asByte else null }

    @Contract(pure = true)
    fun getShort(key: String) = supplyNullable(getPrimitive(key)) { if (it.isNumber) it.asShort else null }

    @Contract(pure = true)
    fun getLong(key: String) = supplyNullable(getPrimitive(key)) { if (it.isNumber) it.asLong else null }

    @Contract(pure = true)
    fun getBoolean(key: String) = supplyNullable(getPrimitive(key)) { if (it.isBoolean) it.asBoolean else null }

    @Contract(pure = true)
    fun getString(key: String) = supplyNullable(getPrimitive(key)) { if (it.isString) it.asString else null }

    @Contract(pure = true)
    fun getChar(key: String) = supplyNullable(getPrimitive(key)) { if (it.isNumber) it.asCharacter else null }

    @Contract(pure = true)
    fun getBigDecimal(key: String) = supplyNullable(getPrimitive(key)) { if (it.isNumber) it.asBigDecimal else null }

    @Contract(pure = true)
    fun getBigInteger(key: String) = supplyNullable(getPrimitive(key)) { if (it.isNumber) it.asBigInteger else null }

    @Contract(pure = true)
    fun getArray(key: String) = supplyNullable(jsonObject[key]) { if (it.isJsonArray) it.asJsonArray else null }

    @Contract(pure = true)
    operator fun get(key: String): JsonElement? = jsonObject[key]

    @Contract(pure = true)
    operator fun <U> get(key: String, clazz: Class<U>): U? =
        supplyNullable(jsonObject[key]) { gson.fromJson(it, clazz) }

    @Contract(pure = true)
    fun getDocument(key: String, def: JsonDocument): JsonDocument {
        if (!this.contains(key)) this.append(key, def)
        return this.getDocument(key)!!
    }

    @Contract(pure = true)
    fun getObject(key: String, def: JsonObject): JsonObject {
        if (!this.contains(key)) this.append(key, def)
        return this.getObject(key)!!
    }

    @Contract(pure = true)
    fun getPrimitive(key: String, def: JsonPrimitive): JsonPrimitive {
        if (!this.contains(key)) this.append(key, def)
        return this.getPrimitive(key)!!
    }

    @Contract(pure = true)
    fun getInt(key: String, def: Int): Int {
        if (!this.contains(key)) this.append(key, def)
        return this.getInt(key)!!
    }

    @Contract(pure = true)
    fun getDouble(key: String, def: Double): Double {
        if (!this.contains(key)) this.append(key, def)
        return this.getDouble(key)!!
    }

    @Contract(pure = true)
    fun getFloat(key: String, def: Float): Float {
        if (!this.contains(key)) this.append(key, def)
        return this.getFloat(key)!!
    }

    @Contract(pure = true)
    fun getByte(key: String, def: Byte): Byte {
        if (!this.contains(key)) this.append(key, def)
        return this.getByte(key)!!
    }

    @Contract(pure = true)
    fun getShort(key: String, def: Short): Short {
        if (!this.contains(key)) this.append(key, def)
        return this.getShort(key)!!
    }

    @Contract(pure = true)
    fun getLong(key: String, def: Short): Long {
        if (!this.contains(key)) this.append(key, def)
        return this.getLong(key)!!
    }

    @Contract(pure = true)
    fun getBoolean(key: String, def: Short): Boolean {
        if (!this.contains(key)) this.append(key, def)
        return this.getBoolean(key)!!
    }

    @Contract(pure = true)
    fun getString(key: String, def: String?): String {
        if (!this.contains(key)) this.append(key, def!!)
        return this.getString(key)!!
    }

    @Contract(pure = true)
    fun getChar(key: String, def: Char): Char {
        if (!this.contains(key)) this.append(key, def)
        return this.getChar(key)!!
    }

    @Contract(pure = true)
    fun getBigDecimal(key: String, def: BigDecimal?): BigDecimal {
        if (!this.contains(key)) this.append(key, def!!)
        return this.getBigDecimal(key)!!
    }

    @Contract(pure = true)
    fun getBigInteger(key: String, def: BigInteger?): BigInteger {
        if (!this.contains(key)) this.append(key, def!!)
        return this.getBigInteger(key)!!
    }

    @Contract(pure = true)
    fun getArray(key: String, def: JsonArray): JsonArray {
        if (!this.contains(key)) this.append(key, def)
        return this.getArray(key)!!
    }

    @Contract(pure = true)
    operator fun get(key: String, def: JsonElement?): JsonElement {
        if (!this.contains(key)) this.append(key, def!!)
        return jsonObject[key]
    }

    @Contract(pure = true)
    operator fun <U : Any> get(key: String, def: U, clazz: Class<U>): U {
        if (!this.contains(key)) this.append(key, def)
        return this[key, clazz]!!
    }

    @Contract("_, _ -> this")
    fun append(key: String, value: Any) = this.withThis { jsonObject.add(key, gson.toJsonTree(value)) }

    @Contract("_, _ -> this")
    fun append(key: String, value: String) = this.withThis { jsonObject.add(key, gson.toJsonTree(value)) }

    @Contract("_, _ -> this")
    fun append(key: String, value: Boolean) = this.withThis { jsonObject.add(key, gson.toJsonTree(value)) }

    @Contract("_, _ -> this")
    fun append(key: String, value: Number) = this.withThis { jsonObject.add(key, gson.toJsonTree(value)) }

    @Contract("_, _ -> this")
    fun append(key: String, value: Char) = this.withThis { jsonObject.add(key, gson.toJsonTree(value)) }

    @Contract("_, _ -> this")
    fun append(key: String, value: JsonDocument) = this.withThis { jsonObject.add(key, gson.toJsonTree(value)) }

    @Contract("_ -> this")
    fun append(jsonObject: JsonObject): JsonDocument {
        jsonObject.entrySet().forEach { (key, value) ->
            this.jsonObject.add(key, value)
        }

        return this
    }

    @Contract(pure = true)
    fun toJsonObject() = jsonObject

    @Contract(pure = true)
    fun toPrettyJson() = gson.toJson(jsonObject)

    @Contract(pure = true)
    fun toJson() = jsonObject.toString()

    companion object {
        private val GSON_SUPPLIER = GsonSupplier()

        val gson: Gson
            get() = GSON_SUPPLIER.getGson()

        fun registerTypeAdapter(clazz: Class<*>?, typeAdapter: Any?) {
            GSON_SUPPLIER.registerTypeAdapter(clazz!!, typeAdapter!!)
        }

    }
}