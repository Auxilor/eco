package com.willfp.eco.internal.price

import com.willfp.eco.core.math.MathContext
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.PriceFactory
import com.willfp.eco.core.price.impl.PriceEconomy
import java.util.function.Function

object PriceFactoryEconomy : PriceFactory {
    override fun getNames() = listOf(
        "coins",
        "$"
    )

    override fun create(baseContext: MathContext, function: Function<MathContext, Double>): Price {
        return PriceEconomy(baseContext, function)
    }
}
