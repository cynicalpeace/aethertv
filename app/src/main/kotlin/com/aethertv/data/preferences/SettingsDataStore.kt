package com.aethertv.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    // Scraper settings
    val autoRefreshEnabled: Flow<Boolean> = dataStore.data.map { it[AUTO_REFRESH] ?: true }
    val refreshIntervalHours: Flow<Int> = dataStore.data.map { it[REFRESH_INTERVAL] ?: 12 }
    val availabilityThreshold: Flow<Float> = dataStore.data.map { it[AVAILABILITY_THRESHOLD] ?: 0.5f }

    // EPG settings
    val epgCountries: Flow<String> = dataStore.data.map { it[EPG_COUNTRIES] ?: "" }
    val epgRefreshHour: Flow<Int> = dataStore.data.map { it[EPG_REFRESH_HOUR] ?: 3 }

    // Verification settings
    val autoVerifyEnabled: Flow<Boolean> = dataStore.data.map { it[AUTO_VERIFY] ?: true }
    val verifyScope: Flow<String> = dataStore.data.map { it[VERIFY_SCOPE] ?: "favorites" }
    val verifyIntervalMinutes: Flow<Int> = dataStore.data.map { it[VERIFY_INTERVAL] ?: 30 }
    val verifyOnLaunch: Flow<Boolean> = dataStore.data.map { it[VERIFY_ON_LAUNCH] ?: true }
    val notifyStreamDown: Flow<Boolean> = dataStore.data.map { it[NOTIFY_STREAM_DOWN] ?: true }

    // Playback settings
    val preferredQuality: Flow<String> = dataStore.data.map { it[PREFERRED_QUALITY] ?: "auto" }

    // App state
    val lastScrapeTime: Flow<Long> = dataStore.data.map { it[LAST_SCRAPE_TIME] ?: 0L }
    val isFirstRun: Flow<Boolean> = dataStore.data.map { it[IS_FIRST_RUN] ?: true }

    suspend fun setAutoRefreshEnabled(enabled: Boolean) {
        dataStore.edit { it[AUTO_REFRESH] = enabled }
    }

    suspend fun setRefreshIntervalHours(hours: Int) {
        dataStore.edit { it[REFRESH_INTERVAL] = hours }
    }

    suspend fun setAvailabilityThreshold(threshold: Float) {
        dataStore.edit { it[AVAILABILITY_THRESHOLD] = threshold }
    }

    suspend fun setEpgCountries(countries: String) {
        dataStore.edit { it[EPG_COUNTRIES] = countries }
    }

    suspend fun setEpgRefreshHour(hour: Int) {
        dataStore.edit { it[EPG_REFRESH_HOUR] = hour }
    }

    suspend fun setAutoVerifyEnabled(enabled: Boolean) {
        dataStore.edit { it[AUTO_VERIFY] = enabled }
    }

    suspend fun setVerifyScope(scope: String) {
        dataStore.edit { it[VERIFY_SCOPE] = scope }
    }

    suspend fun setVerifyIntervalMinutes(minutes: Int) {
        dataStore.edit { it[VERIFY_INTERVAL] = minutes }
    }

    suspend fun setVerifyOnLaunch(enabled: Boolean) {
        dataStore.edit { it[VERIFY_ON_LAUNCH] = enabled }
    }

    suspend fun setNotifyStreamDown(enabled: Boolean) {
        dataStore.edit { it[NOTIFY_STREAM_DOWN] = enabled }
    }

    suspend fun setPreferredQuality(quality: String) {
        dataStore.edit { it[PREFERRED_QUALITY] = quality }
    }

    suspend fun setLastScrapeTime(time: Long) {
        dataStore.edit { it[LAST_SCRAPE_TIME] = time }
    }

    suspend fun setFirstRunComplete() {
        dataStore.edit { it[IS_FIRST_RUN] = false }
    }

    companion object {
        private val AUTO_REFRESH = booleanPreferencesKey("auto_refresh")
        private val REFRESH_INTERVAL = intPreferencesKey("refresh_interval_hours")
        private val AVAILABILITY_THRESHOLD = floatPreferencesKey("availability_threshold")
        private val EPG_COUNTRIES = stringPreferencesKey("epg_countries")
        private val EPG_REFRESH_HOUR = intPreferencesKey("epg_refresh_hour")
        private val AUTO_VERIFY = booleanPreferencesKey("auto_verify")
        private val VERIFY_SCOPE = stringPreferencesKey("verify_scope")
        private val VERIFY_INTERVAL = intPreferencesKey("verify_interval_minutes")
        private val VERIFY_ON_LAUNCH = booleanPreferencesKey("verify_on_launch")
        private val NOTIFY_STREAM_DOWN = booleanPreferencesKey("notify_stream_down")
        private val PREFERRED_QUALITY = stringPreferencesKey("preferred_quality")
        private val LAST_SCRAPE_TIME = longPreferencesKey("last_scrape_time")
        private val IS_FIRST_RUN = booleanPreferencesKey("is_first_run")
    }
}
