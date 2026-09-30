package com.willfp.eco.internal.spigot.data

/**
 * Where eco keeps its own bookkeeping: which handler it migrated from, which migrations have run,
 * which keys have been backfilled, and which datapack entries a world has committed to.
 *
 * None of this is profile data, and none of it belongs in data.yml. It used to live there, which
 * meant a server that had finished migrating still carried a yaml config that was loaded on boot,
 * walked by the config updater on every reload, and deleted and rewritten in full on every
 * autosave -- for a handful of values. This is the store that replaces it, and it lives in the
 * same sqlite database the profiles do.
 *
 * Values are strings, and a list is a list of strings. The bookkeeping has never needed anything
 * richer, and keeping it flat keeps the store to one table with no serializers.
 */
interface Bookkeeping {
    /**
     * The value stored under [key], or null if there is none.
     */
    fun get(key: String): String?

    /**
     * Store [value] under [key], or remove the key when it is null.
     */
    fun set(key: String, value: String?)

    /**
     * The values stored under [key], or an empty list if there are none.
     */
    fun getList(key: String): List<String>

    /**
     * Store [values] under [key], replacing whatever was there.
     */
    fun setList(key: String, values: List<String>)

    /**
     * Every key currently stored that begins with [prefix].
     */
    fun keysStartingWith(prefix: String): Set<String>
}

/**
 * Whether [key] holds true. A key that was never written is false, as an absent config value was.
 */
fun Bookkeeping.getBool(key: String): Boolean =
    get(key)?.toBoolean() == true

fun Bookkeeping.setBool(key: String, value: Boolean) =
    set(key, value.toString())

/**
 * Whether anything at all has been stored under [key].
 */
fun Bookkeeping.has(key: String): Boolean =
    get(key) != null

/**
 * In-memory bookkeeping, for tests and for a handler that cannot provide its own.
 */
class MemoryBookkeeping(
    initial: Map<String, List<String>> = emptyMap()
) : Bookkeeping {
    private val values = LinkedHashMap<String, List<String>>().apply {
        for ((key, value) in initial) {
            put(key, value.toList())
        }
    }

    @Synchronized
    override fun get(key: String): String? = values[key]?.firstOrNull()

    @Synchronized
    override fun set(key: String, value: String?) {
        if (value == null) {
            values.remove(key)
        } else {
            values[key] = listOf(value)
        }
    }

    @Synchronized
    override fun getList(key: String): List<String> = values[key].orEmpty()

    @Synchronized
    override fun setList(key: String, values: List<String>) {
        if (values.isEmpty()) {
            this.values.remove(key)
        } else {
            this.values[key] = values.toList()
        }
    }

    @Synchronized
    override fun keysStartingWith(prefix: String): Set<String> =
        values.keys.filterTo(mutableSetOf()) { it.startsWith(prefix) }
}
