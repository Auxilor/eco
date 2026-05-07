package com.willfp.eco.internal.spigot.math.data

import com.willfp.eco.internal.spigot.math.ExpressionParser

class CharTree<T> {
    private val root = Node()

    fun set(str: String, value: T) {
        var node = root
        for (c in str) {
            node = node.getOrCreateNode(c)
        }
        node.setValue(value)
    }

    @Suppress("UNCHECKED_CAST")
    fun get(str: String): T? {
        var node: Node? = root
        for (c in str) {
            node = node?.getNode(c) ?: return null
        }
        return node?.getValue() as T?
    }

    fun containsFirstChar(c: Char): Boolean = root.getNode(c) != null

    @Suppress("UNCHECKED_CAST")
    fun getFrom(str: String, index: Int): Pair<T?, Int> {
        var node: Node? = root
        var value: T? = null
        for (i in index until str.length) {
            node = node?.getNode(str[i])
            if (node == null) {
                return Pair(value, i - index)
            }
            val nodeValue = node.getValue()
            if (nodeValue != null) {
                value = nodeValue as T?
            }
        }
        return Pair(value, str.length - index)
    }

    @Suppress("UNCHECKED_CAST")
    fun getWith(parser: ExpressionParser): T? {
        var node: Node? = root
        var value: T? = null
        var lastParsed = parser.cursor
        val input = parser.input

        for (i in lastParsed until input.length) {
            node = node?.getNode(input[i])
            if (node == null) {
                parser.cursor = if (value == null) parser.cursor else lastParsed + 1
                return value
            }
            val nodeValue = node.getValue()
            if (nodeValue != null) {
                lastParsed = i
                value = nodeValue as T?
            }
        }
        if (value != null) {
            parser.cursor = lastParsed + 1
        }
        return value
    }

    private class Node {
        private var value: Any? = null
        private val children = arrayOfNulls<Node>(256)

        fun getNode(c: Char): Node? = children[c.code]

        fun getOrCreateNode(c: Char): Node {
            if (children[c.code] == null) {
                children[c.code] = Node()
            }
            return children[c.code]!!
        }

        fun getValue(): Any? = value
        fun setValue(value: Any?) { this.value = value }
    }
}