package ru.senin.kotlin.wiki

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

class StatisticsRunner(private val pageProblem: Page) {
    fun getStatistics(): PageStats {
        with(pageProblem) {
            if (title.isNotEmpty() && text.isNotEmpty() && timestamp.isNotEmpty()) {
                runCatching {
                    val parsedBytes = bytes.toInt()
                    val year = LocalDateTime.parse(timestamp, dateRule).year
                    return PageStats(parsedBytes, year, title, text)
                }
            }
        }
        return PageStats.incorrectInstance
    }

    companion object {
        private val dateRule: DateTimeFormatter = DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral('T')
            .append(DateTimeFormatter.ISO_LOCAL_TIME)
            .appendLiteral('Z')
            .toFormatter()
    }
}