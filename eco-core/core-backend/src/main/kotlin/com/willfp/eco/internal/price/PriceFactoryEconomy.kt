package com.willfp.eco.internal.price

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.context.PlaceholderContextSupplier
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.PriceFactory
import com.willfp.eco.core.price.impl.PriceEconomy

object PriceFactoryEconomy : PriceFactory {
    override fun getNames() = listOf(
        "coins",
        "$"
    )

    override fun create(baseContext: PlaceholderContext, function: PlaceholderContextSupplier<Double>): Price {
        return PriceEconomy(baseContext, function)
    }
}
