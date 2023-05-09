package com.willfp.eco.internal.fast

class ListViewOfCollection<T>(private val collection: Collection<T>) : List<T> {
    /*
    The backing list is lazy-loaded because it provides a performance hit
    to copy the contents of the contents of the collection into a list.

    Since the only required operator for most use-cases is .iterator(),
    we can just use the collection's iterator.
     */

    private val backingList: List<T> by lazy { collection.toList() }

    override val size: Int
        get() = collection.size

    override fun containsAll(elements: Collection<T>) =
        collection.containsAll(elements)

    override fun get(index: Int): T =
        backingList[index]

    override fun indexOf(element: T): Int = backingList.indexOf(element)

    override fun contains(element: T): Boolean = collection.contains(element)

    override fun isEmpty() =
        collection.isEmpty()

    override fun iterator() =
        collection.iterator()

    override fun listIterator() =
        backingList.listIterator()

    override fun listIterator(index: Int) =
        backingList.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int) =
        backingList.subList(fromIndex, toIndex)

    override fun lastIndexOf(element: T) =
        backingList.lastIndexOf(element)
}

inline fun <reified T> Collection<T>.listView(): List<T> {
    return ListViewOfCollection(this)
}
