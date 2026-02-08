package com.aethertv.epg

import com.aethertv.data.local.entity.EpgChannelEntity
import com.aethertv.data.local.entity.EpgProgramEntity
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class XmltvParser @Inject constructor() {

    private val dateFormat = SimpleDateFormat("yyyyMMddHHmmss Z", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun parse(
        inputStream: InputStream,
        onChannel: (EpgChannelEntity) -> Unit,
        onProgram: (EpgProgramEntity) -> Unit,
    ) {
        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(inputStream, null)

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                when (parser.name) {
                    "channel" -> parseChannel(parser)?.let(onChannel)
                    "programme" -> parseProgramme(parser)?.let(onProgram)
                }
            }
            eventType = parser.next()
        }
    }

    private fun parseChannel(parser: XmlPullParser): EpgChannelEntity? {
        val xmltvId = parser.getAttributeValue(null, "id") ?: return null
        var displayName: String? = null
        var iconUrl: String? = null
        var language: String? = null

        var depth = 1
        while (depth > 0) {
            when (parser.next()) {
                XmlPullParser.START_TAG -> {
                    depth++
                    when (parser.name) {
                        "display-name" -> {
                            language = parser.getAttributeValue(null, "lang")
                            displayName = parser.nextText()
                            depth--
                        }
                        "icon" -> {
                            iconUrl = parser.getAttributeValue(null, "src")
                        }
                    }
                }
                XmlPullParser.END_TAG -> depth--
            }
        }

        return EpgChannelEntity(
            xmltvId = xmltvId,
            displayName = displayName ?: xmltvId,
            iconUrl = iconUrl,
            language = language,
        )
    }

    private fun parseProgramme(parser: XmlPullParser): EpgProgramEntity? {
        val channelId = parser.getAttributeValue(null, "channel") ?: return null
        val startStr = parser.getAttributeValue(null, "start") ?: return null
        val stopStr = parser.getAttributeValue(null, "stop") ?: return null

        val startTime = parseXmltvDate(startStr) ?: return null
        val endTime = parseXmltvDate(stopStr) ?: return null

        var title: String? = null
        var description: String? = null
        var category: String? = null
        var iconUrl: String? = null

        var depth = 1
        while (depth > 0) {
            when (parser.next()) {
                XmlPullParser.START_TAG -> {
                    depth++
                    when (parser.name) {
                        "title" -> { title = parser.nextText(); depth-- }
                        "desc" -> { description = parser.nextText(); depth-- }
                        "category" -> { category = parser.nextText(); depth-- }
                        "icon" -> { iconUrl = parser.getAttributeValue(null, "src") }
                    }
                }
                XmlPullParser.END_TAG -> depth--
            }
        }

        return EpgProgramEntity(
            channelId = channelId,
            title = title ?: return null,
            description = description,
            startTime = startTime,
            endTime = endTime,
            category = category,
            iconUrl = iconUrl,
        )
    }

    private fun parseXmltvDate(dateStr: String): Long? {
        return try {
            dateFormat.parse(dateStr)?.time
        } catch (_: Exception) {
            null
        }
    }
}
