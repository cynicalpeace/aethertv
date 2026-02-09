package com.aethertv.util

import android.content.Context
import android.os.Build
import android.util.Log
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Simple crash logger that saves uncaught exceptions to a file.
 * Can be exported from Settings > About > Export Crash Logs
 */
object CrashLogger {
    private const val TAG = "CrashLogger"
    private const val LOG_FILENAME = "crash_log.txt"
    private const val MAX_LOG_SIZE = 500_000 // 500KB max

    private var appContext: Context? = null
    private var defaultHandler: Thread.UncaughtExceptionHandler? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            logCrash(throwable)
            // Call the default handler to show crash dialog / kill app
            defaultHandler?.uncaughtException(thread, throwable)
        }

        Log.i(TAG, "Crash logger initialized")
    }

    /**
     * Log an exception (can be called manually for non-fatal errors)
     */
    fun logException(throwable: Throwable, tag: String = "Exception") {
        try {
            val context = appContext ?: return
            val logFile = File(context.cacheDir, LOG_FILENAME)

            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
            val stackTrace = getStackTraceString(throwable)

            val logEntry = buildString {
                appendLine("=".repeat(60))
                appendLine("[$tag] $timestamp")
                appendLine("Message: ${throwable.message}")
                appendLine("-".repeat(40))
                appendLine(stackTrace)
                appendLine()
            }

            // Append to log file, truncating if too large
            if (logFile.exists() && logFile.length() > MAX_LOG_SIZE) {
                // Keep last half of the log
                val content = logFile.readText()
                val keepFrom = content.length / 2
                logFile.writeText(content.substring(keepFrom))
            }

            logFile.appendText(logEntry)
            Log.d(TAG, "Logged exception to ${logFile.absolutePath}")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to log exception", e)
        }
    }

    /**
     * Log a crash (uncaught exception)
     */
    private fun logCrash(throwable: Throwable) {
        try {
            val context = appContext ?: return
            val logFile = File(context.cacheDir, LOG_FILENAME)

            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
            val stackTrace = getStackTraceString(throwable)

            val deviceInfo = buildString {
                appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
                appendLine("Android: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
                appendLine("App: ${getVersionInfo(context)}")
            }

            val logEntry = buildString {
                appendLine("=".repeat(60))
                appendLine("[CRASH] $timestamp")
                appendLine(deviceInfo)
                appendLine("Exception: ${throwable.javaClass.name}")
                appendLine("Message: ${throwable.message}")
                appendLine("-".repeat(40))
                appendLine(stackTrace)
                appendLine()
            }

            logFile.appendText(logEntry)
            Log.e(TAG, "Logged crash to ${logFile.absolutePath}")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to log crash", e)
        }
    }

    /**
     * Get the crash log contents
     */
    fun getLogContents(): String? {
        val context = appContext ?: return null
        val logFile = File(context.cacheDir, LOG_FILENAME)

        return if (logFile.exists()) {
            logFile.readText()
        } else {
            null
        }
    }

    /**
     * Get log file for sharing/exporting
     */
    fun getLogFile(): File? {
        val context = appContext ?: return null
        val logFile = File(context.cacheDir, LOG_FILENAME)
        return if (logFile.exists()) logFile else null
    }

    /**
     * Check if there are any crash logs
     */
    fun hasLogs(): Boolean {
        val context = appContext ?: return false
        val logFile = File(context.cacheDir, LOG_FILENAME)
        return logFile.exists() && logFile.length() > 0
    }

    /**
     * Clear the crash log
     */
    fun clearLogs() {
        val context = appContext ?: return
        val logFile = File(context.cacheDir, LOG_FILENAME)
        if (logFile.exists()) {
            logFile.delete()
            Log.i(TAG, "Crash logs cleared")
        }
    }

    /**
     * Get the log file size in KB
     */
    fun getLogSizeKb(): Long {
        val context = appContext ?: return 0
        val logFile = File(context.cacheDir, LOG_FILENAME)
        return if (logFile.exists()) logFile.length() / 1024 else 0
    }

    private fun getStackTraceString(throwable: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        throwable.printStackTrace(pw)
        return sw.toString()
    }

    private fun getVersionInfo(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName} (${packageInfo.longVersionCode})"
        } catch (e: Exception) {
            "unknown"
        }
    }
}
