package com.willfp.eco.internal.price

import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.PriceFactory
import com.willfp.eco.core.price.impl.PriceEconomy
import java.util.function.Supplier

object PriceFactoryEconomy : PriceFactory {
    override fun getNames() = listOf(
        "coins",
        "$"
    )

    override fun create(function: Supplier<Double>): Price {
        return PriceEconomy(function)
    }
}
