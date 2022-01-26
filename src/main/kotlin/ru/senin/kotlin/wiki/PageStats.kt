package ru.senin.kotlin.wiki

data class PageStats(
    var bytes: Int,
    var year: Int,
    val titleMap: Map<String, Int>,
    val textMap: Map<String, Int>,
    val isCorrectStat: Boolean = true
) {
    constructor(bytes: Int, year: Int, title: CharSequence, text: CharSequence) :
            this(bytes, year, toMapWords(title), toMapWords(text))

    companion object {
        val incorrectInstance: PageStats =
            PageStats(bytes = 0, year = 0, titleMap = emptyMap(), textMap = emptyMap(), isCorrectStat = false)

        private fun toMapWords(s: CharSequence): Map<String, Int> =
            """[а-яА-Я]{3,}""".toRegex().findAll(s).groupingBy { it.value.lowercase() }.eachCount()
    }
}