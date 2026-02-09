package com.aethertv.scraper

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thread-safe date formatter for log entries.
 * Uses ThreadLocal to avoid synchronization issues with SimpleDateFormat.
 * Uses Locale.ROOT for consistent formatting across all locales.
 */
private object LogTimeFormatter {
    private val formatter = ThreadLocal.withInitial {
        SimpleDateFormat("HH:mm:ss", Locale.ROOT)
    }
    
    /**
     * Format timestamp to HH:mm:ss string.
     * Uses safe fallback if ThreadLocal returns null (H26 fix).
     */
    fun format(timestamp: Long): String {
        val fmt = formatter.get() ?: SimpleDateFormat("HH:mm:ss", Locale.ROOT)
        return fmt.format(Date(timestamp))
    }
}

/**
 * Represents a single log entry from the scraper.
 * formattedTime is pre-computed at creation to avoid repeated allocations.
 */
data class ScraperLogEntry(
    val timestamp: Long = System.currentTimeMillis(),
    val level: LogLevel = LogLevel.INFO,
    val message: String,
    val details: String? = null,
    // Pre-format time at creation - avoids creating SimpleDateFormat on every display
    val formattedTime: String = LogTimeFormatter.format(timestamp),
) {
    enum class LogLevel { DEBUG, INFO, WARN, ERROR, SUCCESS }
    
    val emoji: String
        get() = when (level) {
            LogLevel.DEBUG -> "🔍"
            LogLevel.INFO -> "ℹ️"
            LogLevel.WARN -> "⚠️"
            LogLevel.ERROR -> "❌"
            LogLevel.SUCCESS -> "✅"
        }
}

/**
 * Current state of the scraper operation
 */
data class ScraperProgress(
    val isRunning: Boolean = false,
    val phase: ScraperPhase = ScraperPhase.IDLE,
    val currentPage: Int = 0,
    val totalPages: Int? = null,
    val channelsFound: Int = 0,
    val channelsAfterFilter: Int = 0,
    val currentSource: String = "",
    val startTime: Long? = null,
    val logs: List<ScraperLogEntry> = emptyList(),
    val error: String? = null,
) {
    val elapsedSeconds: Long
        get() = startTime?.let { (System.currentTimeMillis() - it) / 1000 } ?: 0
    
    val progressPercent: Float
        get() = if (totalPages != null && totalPages > 0) {
            (currentPage.toFloat() / totalPages).coerceIn(0f, 1f)
        } else 0f
}

enum class ScraperPhase {
    IDLE,
    CONNECTING,
    SEARCHING,
    FILTERING,
    SAVING,
    COMPLETE,
    ERROR,
}

/**
 * Singleton that tracks scraper state and emits progress updates.
 * UI observes this to show real-time scraper progress.
 */
@Singleton
class ScraperState @Inject constructor() {
    private val _progress = MutableStateFlow(ScraperProgress())
    val progress: StateFlow<ScraperProgress> = _progress.asStateFlow()
    
    private val maxLogs = 100 // Keep last 100 log entries
    
    fun reset() {
        _progress.value = ScraperProgress()
    }
    
    fun start() {
        _progress.value = ScraperProgress(
            isRunning = true,
            phase = ScraperPhase.CONNECTING,
            startTime = System.currentTimeMillis(),
            logs = listOf(
                ScraperLogEntry(
                    level = ScraperLogEntry.LogLevel.INFO,
                    message = "Starting scraper...",
                )
            ),
        )
    }
    
    /**
     * Add a log entry thread-safely using atomic update.
     * Prevents lost log entries when multiple coroutines log concurrently.
     */
    fun log(level: ScraperLogEntry.LogLevel, message: String, details: String? = null) {
        val entry = ScraperLogEntry(
            level = level,
            message = message,
            details = details,
        )
        _progress.update { current ->
            current.copy(logs = (current.logs + entry).takeLast(maxLogs))
        }
    }
    
    fun debug(message: String, details: String? = null) = log(ScraperLogEntry.LogLevel.DEBUG, message, details)
    fun info(message: String, details: String? = null) = log(ScraperLogEntry.LogLevel.INFO, message, details)
    fun warn(message: String, details: String? = null) = log(ScraperLogEntry.LogLevel.WARN, message, details)
    fun error(message: String, details: String? = null) = log(ScraperLogEntry.LogLevel.ERROR, message, details)
    fun success(message: String, details: String? = null) = log(ScraperLogEntry.LogLevel.SUCCESS, message, details)
    
    fun updatePhase(phase: ScraperPhase) {
        _progress.update { it.copy(phase = phase) }
    }
    
    fun updateSource(source: String) {
        _progress.update { it.copy(currentSource = source) }
    }
    
    fun updateSearchProgress(page: Int, totalPages: Int?, channelsFound: Int) {
        _progress.update { current ->
            current.copy(
                phase = ScraperPhase.SEARCHING,
                currentPage = page,
                totalPages = totalPages,
                channelsFound = channelsFound,
            )
        }
    }
    
    fun updateFilterProgress(channelsAfterFilter: Int) {
        _progress.update { current ->
            current.copy(
                phase = ScraperPhase.FILTERING,
                channelsAfterFilter = channelsAfterFilter,
            )
        }
    }
    
    fun complete(totalChannels: Int) {
        _progress.update { current ->
            current.copy(
                isRunning = false,
                phase = ScraperPhase.COMPLETE,
                channelsAfterFilter = totalChannels,
            )
        }
        success("Scrape complete!", "Found $totalChannels channels")
    }
    
    fun fail(errorMessage: String) {
        _progress.update { current ->
            current.copy(
                isRunning = false,
                phase = ScraperPhase.ERROR,
                error = errorMessage,
            )
        }
        error("Scrape failed", errorMessage)
    }
}
