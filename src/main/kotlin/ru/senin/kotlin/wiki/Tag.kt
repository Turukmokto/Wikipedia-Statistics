package ru.senin.kotlin.wiki

enum class Tag {
    START, MEDIAWIKI, PAGE, TITLE, REVISION, TIMESTAMP, TEXT, OTHER;

    fun children(): List<String> = when (this) {
        START -> listOf("MEDIAWIKI")
        MEDIAWIKI -> listOf("PAGE")
        PAGE -> listOf("TITLE", "REVISION")
        REVISION -> listOf("TIMESTAMP", "TEXT")
        else -> emptyList()
    }

    fun parent(): Tag = when (this) {
        MEDIAWIKI -> START
        PAGE -> MEDIAWIKI
        TITLE, REVISION -> PAGE
        TIMESTAMP, TEXT -> REVISION
        else -> OTHER
    }
}


