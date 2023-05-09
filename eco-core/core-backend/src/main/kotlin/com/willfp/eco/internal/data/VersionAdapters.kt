package com.willfp.eco.internal.data

import com.willfp.eco.core.data.ExternalDataStoreObjectAdapter
import com.willfp.eco.core.version.Version

object VersionToStringAdapter: ExternalDataStoreObjectAdapter<Version, String>(
    Version::class.java,
    String::class.java
) {
    override fun toAccessedObject(obj: String): Version = Version(obj)

    override fun toStoredObject(obj: Version): String = obj.toString()
}

class MavenVersionToStringAdapter(
    className: String
): ExternalDataStoreObjectAdapter<Any, String>(
    Class.forName(className),
    String::class.java
) {
    private val constructor = Class.forName(className)
        .getConstructor(String::class.java)

    override fun toAccessedObject(obj: String): Any = constructor.newInstance(obj)

    override fun toStoredObject(obj: Any): String = obj.toString()
}
