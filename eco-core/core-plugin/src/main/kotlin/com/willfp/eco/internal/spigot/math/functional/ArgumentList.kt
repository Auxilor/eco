package com.willfp.eco.internal.spigot.math.functional

import com.willfp.eco.internal.spigot.math.token.Token
import com.willfp.eco.internal.spigot.math.token.TokenType
import com.willfp.eco.internal.spigot.math.token.Value

class ArgumentList(private val arguments: Array<Value>) : Token {
    override fun getType() = TokenType.ARGUMENT_LIST
    fun getArguments() = arguments
}