package ru.senin.kotlin.wiki

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import java.io.File
import java.util.concurrent.RecursiveTask
import javax.xml.parsers.SAXParserFactory

class FilesetStatistics(private val inputs: List<File>) : RecursiveTask<TotalStats>() {
    override fun compute(): TotalStats {
        if (inputs.size == 1) {
            return single()
        }
        val statFirstProblem = FilesetStatistics(inputs.subList(0, inputs.size / 2))
        val statSecondProblem = FilesetStatistics(inputs.subList(inputs.size / 2, inputs.size))

        statFirstProblem.fork()
        statSecondProblem.fork()

        return statFirstProblem
            .join()
            .apply { mergeWithOther(statSecondProblem.join()) }
            .also { println("Merging performed") }
    }

    private fun single(): TotalStats {
        val file = inputs.first()
        val subProblem = mutableListOf<RecursiveStatisticOfPage>()

        BZip2CompressorInputStream(file.inputStream()).use { inputStream ->
            val handler = WikiHandler { pageProblem: Page ->
                val statProblem = RecursiveStatisticOfPage(pageProblem)
                statProblem.fork()
                subProblem += statProblem
            }
            SAXParserFactory.newInstance().newSAXParser().parse(inputStream, handler)

            return TotalStats().apply {
                subProblem.forEach {
                    mergeWithPageStatistics(it.join())
                }
            }.also {
                println("File $file computed")
            }
        }
    }
}

class RecursiveStatisticOfPage(private val pageProblem: Page) : RecursiveTask<PageStats>() {
    override fun compute(): PageStats = StatisticsRunner(pageProblem).getStatistics()
}