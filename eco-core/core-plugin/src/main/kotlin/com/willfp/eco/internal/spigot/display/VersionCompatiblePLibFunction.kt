package com.willfp.eco.internal.spigot.display

import com.google.common.base.Function
import java.util.function.UnaryOperator

interface VersionCompatiblePLibFunction<T> : Function<T, T>, UnaryOperator<T> {

}
