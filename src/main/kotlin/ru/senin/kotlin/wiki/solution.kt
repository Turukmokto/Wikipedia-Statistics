package ru.senin.kotlin.wiki

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.concurrent.*

fun solution(inputs: List<File>, output: String, threads: Int) {
    inputs.forEach { file ->
        check(file.exists() && file.canRead() && file.name.endsWith(".bz2")) {
            "file " + file.name + " is not bzip2 or couldn't be read"
        }
    }

    val stats = ForkJoinPool(threads).invoke(FilesetStatistics(inputs))

    val sj = StringJoiner(System.lineSeparator())
    sj.add("Топ-${TotalStats.MAX_COUNT} слов в заголовках статей:")
        .add(stats.getResultTitle())
        .add("Топ-${TotalStats.MAX_COUNT} слов в статьях:")
        .add(stats.getResultText())
        .add("Распределение статей по размеру:")
        .add(stats.getResultBytes())
        .add("Распределение статей по времени:")
        .add(stats.getResultTimestamp())

    Files.writeString(Path.of(output), sj.toString(), StandardCharsets.UTF_8)
}