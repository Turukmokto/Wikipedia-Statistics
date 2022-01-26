package ru.senin.kotlin.wiki

import kotlin.collections.HashMap

class TotalStats(titleMapSize: Int = 512, textMapSize: Int = 2048) {
    private val titleMap: MutableMap<String, Int> = HashMap(titleMapSize)
    private val textMap: MutableMap<String, Int> = HashMap(textMapSize)
    private val bytesMap: MutableMap<Int, Int> = mutableMapOf()
    private val yearMap: MutableMap<Int, Int> = mutableMapOf()

    fun getResultTitle(): String = getFormattedString(titleMap)

    fun getResultText(): String = getFormattedString(textMap)

    fun getResultBytes(): String = getFormattedInt(bytesMap)

    fun getResultTimestamp(): String = getFormattedInt(yearMap)

    fun mergeWithOther(other: TotalStats) {
        mergeMaps(titleMap, other.titleMap)
        mergeMaps(textMap, other.textMap)
        mergeMaps(bytesMap, other.bytesMap)
        mergeMaps(yearMap, other.yearMap)
    }

    fun mergeWithPageStatistics(stat: PageStats) {
        if (!stat.isCorrectStat) return

        mergeMaps(titleMap, stat.titleMap)
        mergeMaps(textMap, stat.textMap)
        add(bytesMap, countOfDigits(stat.bytes) - 1)
        add(yearMap, stat.year)
    }

    private fun countOfDigits(number: Int): Int = number.toString().length

    private fun <T : Comparable<T>> mergeMaps(mapFirst: MutableMap<T, Int>, mapSecond: Map<T, Int>) {
        mapSecond.forEach { (key, value) -> add(mapFirst, key, value) }
    }

    private fun <T : Comparable<T>> add(map: MutableMap<T, Int>, key: T, toAdd: Int = 1) {
        map.merge(key, toAdd, Int::plus)
    }

    private fun getFormattedString(map: Map<String, Int>): String =
        map.entries.map { WordCount(it) }.sorted().take(MAX_COUNT).joinToString("") {
            "${it.count} ${it.name}${System.lineSeparator()}"
        }

    private fun getFormattedInt(map: Map<Int, Int>): String {
        if (map.isEmpty()) return ""

        val keys = map.keys.toSortedSet()
        val minimum = keys.first()
        val maximum = keys.last()

        return (minimum..maximum).joinToString("") {
            "$it ${map.getOrDefault(it, 0)}${System.lineSeparator()}"
        }
    }

    companion object {
        const val MAX_COUNT: Int = 300
    }
}