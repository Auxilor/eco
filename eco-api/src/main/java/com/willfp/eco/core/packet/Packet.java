package com.willfp.eco.core.packet;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a packet.
 *
 * @param handle The NMS handle.
 */
public record Packet(@NotNull Object handle) {

}
