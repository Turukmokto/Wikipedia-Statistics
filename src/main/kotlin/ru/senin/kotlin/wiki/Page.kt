package ru.senin.kotlin.wiki

import ru.senin.kotlin.wiki.Tag.*
import kotlin.text.StringBuilder

data class Page(
    var title: StringBuilder = StringBuilder(),
    var text: StringBuilder = StringBuilder(),
    var timestamp: StringBuilder = StringBuilder(),
    var bytes: String = ""
) {
    fun updateValues(curTag: Tag, copyOfRange: CharArray) {
        when (curTag) {
            TITLE -> title.append(copyOfRange)
            TIMESTAMP -> timestamp.append(copyOfRange)
            TEXT -> text.append(copyOfRange)
            START -> Unit
            MEDIAWIKI -> Unit
            PAGE -> Unit
            REVISION -> Unit
            OTHER -> Unit
        }
    }
}