package com.aethertv.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.aethertv.data.local.FilterRuleDao
import com.aethertv.data.repository.ChannelRepository
import com.aethertv.scraper.AceStreamScraper
import com.aethertv.scraper.CategoryRemapRule
import com.aethertv.scraper.FilterConfig
import com.aethertv.scraper.NameToCategoryRule
import com.aethertv.scraper.ScraperProgress
import com.aethertv.scraper.ScraperState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScraperMonitorViewModel @Inject constructor(
    private val scraperState: ScraperState,
    private val scraper: AceStreamScraper,
    private val channelRepository: ChannelRepository,
    private val filterRuleDao: FilterRuleDao,
    private val exoPlayer: ExoPlayer,
) : ViewModel() {

    val progress: StateFlow<ScraperProgress> = scraperState.progress
    
    // Warning message when playback is active
    private val _warningMessage = MutableStateFlow<String?>(null)
    val warningMessage: StateFlow<String?> = _warningMessage.asStateFlow()
    
    fun startScrape() {
        if (progress.value.isRunning) return
        
        // Check if playback is active - warn user
        if (exoPlayer.isPlaying || exoPlayer.playbackState == ExoPlayer.STATE_BUFFERING) {
            _warningMessage.value = "Playback will stop when scraping starts"
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
        }
        
        viewModelScope.launch {
            try {
                // Record timestamp before scraping to clean up stale channels (H30 fix)
                val scrapeStartTime = System.currentTimeMillis()
                
                // Load filter rules from database and build FilterConfig
                val rules = filterRuleDao.getEnabled()
                val config = buildFilterConfig(rules)
                
                val channels = scraper.scrape(config)
                
                // Save to database
                scraperState.info("Saving ${channels.size} channels to database...")
                channelRepository.insertFromScraper(channels, scrapeStartTime)
                scraperState.success("Saved ${channels.size} channels")
                
                // Clean up channels from before this scrape that weren't updated (H30 fix)
                // This removes channels that no longer exist in the engine
                channelRepository.deleteStale(scrapeStartTime)
                scraperState.info("Cleaned up stale channel entries")
                
            } catch (e: Exception) {
                // Error already logged by scraper
            }
        }
    }
    
    /**
     * Build FilterConfig from database filter rules
     */
    private fun buildFilterConfig(rules: List<com.aethertv.data.local.entity.FilterRuleEntity>): FilterConfig {
        val nameExclude = mutableListOf<String>()
        val nameInclude = mutableListOf<String>()
        val categories = mutableSetOf<String>()
        val languages = mutableSetOf<String>()
        val countries = mutableSetOf<String>()
        
        for (rule in rules) {
            when (rule.type) {
                "name_exclude" -> nameExclude.add(rule.pattern)
                "name_include" -> nameInclude.add(rule.pattern)
                "category" -> categories.addAll(rule.pattern.split("|").map { it.trim() })
                "language" -> languages.addAll(rule.pattern.split("|").map { it.trim() })
                "country" -> countries.addAll(rule.pattern.split("|").map { it.trim() })
            }
        }
        
        return FilterConfig(
            availabilityThreshold = 0.3f,
            nameExcludePatterns = nameExclude,
            nameIncludePatterns = nameInclude,
            categoryWhitelist = categories,
            languageWhitelist = languages,
            countryWhitelist = countries,
        )
    }
    
    fun clearLogs() {
        scraperState.reset()
    }
    
    fun dismissWarning() {
        _warningMessage.value = null
    }
}
