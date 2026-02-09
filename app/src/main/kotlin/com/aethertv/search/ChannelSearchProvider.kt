package com.aethertv.search

import android.app.SearchManager
import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.provider.BaseColumns
import android.util.Log
import com.aethertv.data.local.AetherTvDatabase
import com.aethertv.data.local.entity.ChannelEntity
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/**
 * Content provider for Android TV global search integration.
 * Provides channel suggestions when user searches via voice or keyboard.
 * 
 * Note: Uses a background thread with timeout instead of runBlocking
 * to avoid blocking the binder thread and causing ANRs.
 */
class ChannelSearchProvider : ContentProvider() {

    companion object {
        private const val TAG = "ChannelSearchProvider"
        private const val AUTHORITY = "com.aethertv.app.search"
        private const val SEARCH_SUGGEST = 1
        private const val SEARCH_CHANNEL = 2
        private const val QUERY_TIMEOUT_MS = 2000L
        
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST)
            addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST)
            addURI(AUTHORITY, "channel/*", SEARCH_CHANNEL)
        }
    }
    
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ChannelSearchProviderEntryPoint {
        fun database(): AetherTvDatabase
    }
    
    private fun getDatabase(): AetherTvDatabase {
        val appContext = context?.applicationContext ?: throw IllegalStateException("Context is null")
        val entryPoint = EntryPointAccessors.fromApplication(
            appContext,
            ChannelSearchProviderEntryPoint::class.java
        )
        return entryPoint.database()
    }

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return when (uriMatcher.match(uri)) {
            SEARCH_SUGGEST -> {
                val query = uri.lastPathSegment ?: return null
                if (query.length < 2) return null
                getSuggestions(query)
            }
            else -> null
        }
    }
    
    /**
     * Get search suggestions with timeout to avoid ANR.
     * Uses a background thread instead of runBlocking on the binder thread.
     */
    private fun getSuggestions(query: String): Cursor {
        val cursor = MatrixCursor(arrayOf(
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1,        // Channel name
            SearchManager.SUGGEST_COLUMN_TEXT_2,        // Category
            SearchManager.SUGGEST_COLUMN_ICON_1,        // Icon
            SearchManager.SUGGEST_COLUMN_INTENT_DATA,   // URI for intent
            SearchManager.SUGGEST_COLUMN_INTENT_ACTION  // Action
        ))
        
        val latch = CountDownLatch(1)
        val results = mutableListOf<ChannelEntity>()
        
        // Run query on background thread with timeout (M25 fix - named thread)
        thread(name = "channel-search-query") {
            try {
                val database = getDatabase()
                val dao = database.channelDao()
                
                // Use runBlocking only on the background thread, not the binder thread
                @Suppress("BlockingMethodInNonBlockingContext")
                val channels: List<ChannelEntity>? = runBlocking {
                    withTimeoutOrNull<List<ChannelEntity>?>(QUERY_TIMEOUT_MS) {
                        dao.search("%$query%").firstOrNull()
                    }
                }
                
                if (channels != null) {
                    results.addAll(channels.take(10))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Search query failed", e)
            } finally {
                latch.countDown()
            }
        }
        
        // Wait with timeout to avoid blocking the binder thread forever
        try {
            latch.await(QUERY_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            Log.w(TAG, "Search query interrupted")
        }
        
        // Build cursor from results
        results.forEachIndexed { index, channel ->
            cursor.addRow(arrayOf<Any>(
                index.toLong(),
                channel.name,
                channel.categories.takeIf { it.isNotBlank() } ?: "TV",
                channel.iconUrl ?: "",
                "aethertv://channel/${channel.infohash}",
                "android.intent.action.VIEW"
            ))
        }
        
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            SEARCH_SUGGEST -> SearchManager.SUGGEST_MIME_TYPE
            SEARCH_CHANNEL -> "vnd.android.cursor.item/channel"
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
}
