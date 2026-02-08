package com.aethertv.search

import android.app.SearchManager
import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.provider.BaseColumns
import com.aethertv.data.local.AetherTvDatabase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Content provider for Android TV global search integration.
 * Provides channel suggestions when user searches via voice or keyboard.
 */
class ChannelSearchProvider : ContentProvider() {

    companion object {
        private const val AUTHORITY = "com.aethertv.app.search"
        private const val SEARCH_SUGGEST = 1
        private const val SEARCH_CHANNEL = 2
        
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
    
    private fun getSuggestions(query: String): Cursor {
        val cursor = MatrixCursor(arrayOf(
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1,        // Channel name
            SearchManager.SUGGEST_COLUMN_TEXT_2,        // Category
            SearchManager.SUGGEST_COLUMN_ICON_1,        // Icon
            SearchManager.SUGGEST_COLUMN_INTENT_DATA,   // URI for intent
            SearchManager.SUGGEST_COLUMN_INTENT_ACTION  // Action
        ))
        
        runBlocking {
            try {
                val database = getDatabase()
                val channels = database.channelDao().search("%$query%").first()
                
                channels.take(10).forEachIndexed { index, channel ->
                    cursor.addRow(arrayOf(
                        index.toLong(),
                        channel.name,
                        channel.categories.takeIf { it.isNotBlank() } ?: "TV",
                        channel.iconUrl ?: "",
                        "aethertv://channel/${channel.infohash}",
                        "android.intent.action.VIEW"
                    ))
                }
            } catch (e: Exception) {
                // Return empty cursor on error
            }
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
