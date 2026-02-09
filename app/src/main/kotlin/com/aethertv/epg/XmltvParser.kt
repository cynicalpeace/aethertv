package com.aethertv.epg

import android.util.Log
import com.aethertv.data.local.entity.EpgChannelEntity
import com.aethertv.data.local.entity.EpgProgramEntity
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Parser for XMLTV format EPG data.
 * Handles malformed XML gracefully with proper depth tracking.
 */
@Singleton
class XmltvParser @Inject constructor() {
    
    companion object {
        private const val TAG = "XmltvParser"
        private const val MAX_DEPTH = 100 // Sanity check to prevent infinite loops
        private const val DATE_FORMAT_STANDARD = "yyyyMMddHHmmss Z"
        private const val DATE_FORMAT_ALT = "yyyyMMddHHmmssZ"
        private const val DATE_FORMAT_SIMPLE = "yyyyMMddHHmmss"
    }

    /**
     * Creates thread-safe date formatters.
     * SimpleDateFormat is NOT thread-safe, so we create new instances per use.
     */
    private fun createStandardFormat(): SimpleDateFormat {
        return SimpleDateFormat(DATE_FORMAT_STANDARD, Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }
    
    private fun createAltFormat(): SimpleDateFormat {
        return SimpleDateFormat(DATE_FORMAT_ALT, Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }
    
    private fun createSimpleFormat(): SimpleDateFormat {
        return SimpleDateFormat(DATE_FORMAT_SIMPLE, Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    fun parse(
        inputStream: InputStream,
        onChannel: (EpgChannelEntity) -> Unit,
        onProgram: (EpgProgramEntity) -> Unit,
    ) {
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = false
            val parser = factory.newPullParser()
            parser.setInput(inputStream, null)

            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    when (parser.name) {
                        "channel" -> {
                            try {
                                parseChannel(parser)?.let(onChannel)
                            } catch (e: Exception) {
                                Log.w(TAG, "Error parsing channel: ${e.message}")
                            }
                        }
                        "programme" -> {
                            try {
                                parseProgramme(parser)?.let(onProgram)
                            } catch (e: Exception) {
                                Log.w(TAG, "Error parsing programme: ${e.message}")
                            }
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: XmlPullParserException) {
            Log.e(TAG, "XML parsing error", e)
            throw e
        }
    }

    private fun parseChannel(parser: XmlPullParser): EpgChannelEntity? {
        val xmltvId = parser.getAttributeValue(null, "id") ?: return null
        var displayName: String? = null
        var iconUrl: String? = null
        var language: String? = null
        
        val startTagName = parser.name

        var depth = 1
        var iterations = 0
        while (depth > 0 && iterations < MAX_DEPTH * 10) {
            iterations++
            val event = parser.next()
            when (event) {
                XmlPullParser.START_TAG -> {
                    depth++
                    when (parser.name) {
                        "display-name" -> {
                            language = parser.getAttributeValue(null, "lang")
                            displayName = parser.nextText()
                            depth-- // nextText() consumes the end tag
                        }
                        "icon" -> {
                            iconUrl = parser.getAttributeValue(null, "src")
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    // Verify we're closing the right tag
                    if (depth == 1 && parser.name == startTagName) {
                        depth = 0 // We're done
                    } else {
                        depth--
                    }
                }
                XmlPullParser.END_DOCUMENT -> break
            }
        }
        
        if (iterations >= MAX_DEPTH * 10) {
            Log.w(TAG, "Max iterations reached parsing channel: $xmltvId")
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
        
        val startTagName = parser.name

        var depth = 1
        var iterations = 0
        while (depth > 0 && iterations < MAX_DEPTH * 10) {
            iterations++
            val event = parser.next()
            when (event) {
                XmlPullParser.START_TAG -> {
                    depth++
                    when (parser.name) {
                        "title" -> { 
                            title = parser.nextText()
                            depth-- // nextText() consumes end tag
                        }
                        "desc" -> { 
                            description = parser.nextText()
                            depth--
                        }
                        "category" -> { 
                            category = parser.nextText()
                            depth--
                        }
                        "icon" -> { 
                            iconUrl = parser.getAttributeValue(null, "src") 
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (depth == 1 && parser.name == startTagName) {
                        depth = 0
                    } else {
                        depth--
                    }
                }
                XmlPullParser.END_DOCUMENT -> break
            }
        }
        
        if (iterations >= MAX_DEPTH * 10) {
            Log.w(TAG, "Max iterations reached parsing programme for channel: $channelId")
        }

        // Title is required
        if (title.isNullOrBlank()) return null

        return EpgProgramEntity(
            channelId = channelId,
            title = title,
            description = description,
            startTime = startTime,
            endTime = endTime,
            category = category,
            iconUrl = iconUrl,
        )
    }

    private fun parseXmltvDate(dateStr: String): Long? {
        // Try with space first (standard format)
        // Note: We create new SimpleDateFormat instances each call for thread safety
        return try {
            createStandardFormat().parse(dateStr)?.time
        } catch (_: Exception) {
            // Try without space
            try {
                createAltFormat().parse(dateStr)?.time
            } catch (_: Exception) {
                // Try parsing just the date part if timezone parsing fails
                try {
                    val cleanDate = dateStr.take(14)
                    createSimpleFormat().parse(cleanDate)?.time
                } catch (_: Exception) {
                    Log.w(TAG, "Failed to parse date: $dateStr")
                    null
                }
            }
        }
    }
}
