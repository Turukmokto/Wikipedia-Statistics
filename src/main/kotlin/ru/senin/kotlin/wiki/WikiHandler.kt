package ru.senin.kotlin.wiki

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import ru.senin.kotlin.wiki.Tag.*

class WikiHandler(private val pageAction: (Page) -> Unit) : DefaultHandler() {
    private var curPageProblem: Page = Page()

    private var curTag: Tag = START
    private var balance: Int = 0

    private val getCurState: Tag
        get() = if (balance == 0) curTag else OTHER

    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        val name = qName.uppercase()
        if (name in getCurState.children()) {
            curTag = valueOf(name)
        } else {
            balance++
        }
        if (getCurState == TEXT) {
            curPageProblem.bytes = attributes.getValue("bytes")
        }
    }

    override fun endElement(uri: String, localName: String, qName: String) {
        if (getCurState == PAGE) {
            pageAction(curPageProblem)
            curPageProblem = Page()
        }
        if (getCurState != OTHER && qName.equals(getCurState.name, ignoreCase = true)) {
            curTag = getCurState.parent()
        } else {
            balance--
        }
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
        if (getCurState in listOf(TEXT, TITLE, TIMESTAMP)) {
            curPageProblem.updateValues(getCurState, ch.copyOfRange(start, start + length))
        }
    }
}