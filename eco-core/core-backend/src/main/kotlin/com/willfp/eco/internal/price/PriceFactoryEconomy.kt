package com.willfp.eco.internal.price

import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.PriceFactory
import com.willfp.eco.core.price.impl.PriceEconomy

object PriceFactoryEconomy : PriceFactory {
    override fun getNames() = listOf(
        "coins",
        "$"
    )

    override fun create(value: Double): Price = PriceEconomy(value)
}
