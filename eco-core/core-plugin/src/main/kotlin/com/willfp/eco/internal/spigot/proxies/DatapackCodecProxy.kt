package com.willfp.eco.internal.spigot.proxies

interface DatapackCodecProxy {
    /**
     * Decode [json] through the server's own codec for [registryDirPath].
     *
     * This is the tier of validation that matters: JSON syntax and path shape checks pass bad enum
     * values and missing required fields straight through to a server that then refuses to boot.
     *
     * @param registryDirPath The registry directory, e.g. `worldgen/biome`.
     * @param pending         What the same publish is writing, so that references to it resolve
     *                        instead of failing as not-yet-live.
     * @return null if valid, else a human-readable error.
     */
    fun validate(registryDirPath: String, json: String, pending: PendingContent): String?

    /**
     * Registry directory paths that can be codec-validated on this server.
     */
    fun validatableRegistries(): Set<String>
}
