package ru.senin.kotlin.wiki

data class WordCount(val name: String, val count: Int) : Comparable<WordCount> {
    constructor(entry: Map.Entry<String, Int>) : this(entry.key, entry.value)

    override operator fun compareTo(other: WordCount): Int =
        if (count == other.count) {
            name.compareTo(other.name)
        } else {
            other.count.compareTo(count)
        }
}