package com.aethertv.scraper

import com.aethertv.data.remote.AceStreamChannel
import com.aethertv.data.remote.AceStreamEngineClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
class AceStreamScraper @Inject constructor(
    private val engineClient: AceStreamEngineClient,
    private val channelFilter: ChannelFilter,
    private val scraperState: ScraperState,
    private val httpClient: HttpClient,
) {
    companion object {
        private const val ENGINE_ADDRESS = "http://127.0.0.1:6878"
    }
    
    /**
     * Main scrape function with progress reporting
     */
    suspend fun scrape(config: FilterConfig): List<AceStreamChannel> {
        scraperState.start()
        
        return try {
            // Phase 1: Connect to engine
            scraperState.updatePhase(ScraperPhase.CONNECTING)
            scraperState.info("Connecting to AceStream engine...")
            scraperState.updateSource(ENGINE_ADDRESS)
            
            engineClient.waitForConnection(timeout = 30.seconds)
            scraperState.success("Connected to AceStream engine")
            
            // Phase 2: Search for channels with pagination
            scraperState.updatePhase(ScraperPhase.SEARCHING)
            scraperState.info("Searching for channels...")
            
            val allChannels = searchWithProgress()
            scraperState.success("Found ${allChannels.size} total channels from engine")
            
            // Phase 3: Apply filters
            scraperState.updatePhase(ScraperPhase.FILTERING)
            scraperState.info("Applying filters...")
            logFilterConfig(config)
            
            val filtered = allChannels.applyFilters(config)
            scraperState.updateFilterProgress(filtered.size)
            
            val removed = allChannels.size - filtered.size
            if (removed > 0) {
                scraperState.info("Filtered out $removed channels", 
                    "Kept ${filtered.size} of ${allChannels.size}")
            }
            
            // Phase 4: Complete
            scraperState.complete(filtered.size)
            
            filtered
        } catch (e: Exception) {
            scraperState.fail(e.message ?: "Unknown error")
            throw e
        }
    }
    
    /**
     * Search with page-by-page progress reporting
     */
    private suspend fun searchWithProgress(): List<AceStreamChannel> {
        val allItems = mutableListOf<AceStreamChannel>()
        var page = 0
        val pageSize = 200
        var totalEstimate: Int? = null
        
        while (true) {
            scraperState.debug("Fetching page ${page + 1}...")
            
            val result: SearchResponseWithTotal = httpClient.get("$ENGINE_ADDRESS/server/api") {
                parameter("method", "search")
                parameter("page_size", pageSize)
                parameter("page", page)
            }.body()
            
            val channels = result.result.results
            allItems.addAll(channels)
            
            // Estimate total pages from first response
            if (totalEstimate == null && result.result.total > 0) {
                totalEstimate = (result.result.total + pageSize - 1) / pageSize
            }
            
            scraperState.updateSearchProgress(
                page = page + 1,
                totalPages = totalEstimate,
                channelsFound = allItems.size,
            )
            
            scraperState.info("Page ${page + 1}: +${channels.size} channels", 
                "Total so far: ${allItems.size}")
            
            if (channels.size < pageSize) {
                scraperState.debug("Last page reached (${channels.size} < $pageSize)")
                break
            }
            page++
        }
        
        return allItems
    }
    
    private fun logFilterConfig(config: FilterConfig) {
        val rules = mutableListOf<String>()
        if (config.statusFilter.isNotEmpty()) {
            rules.add("Status: ${config.statusFilter.joinToString()}")
        }
        if (config.availabilityThreshold > 0) {
            rules.add("Min availability: ${(config.availabilityThreshold * 100).toInt()}%")
        }
        if (config.categoryWhitelist.isNotEmpty()) {
            rules.add("Categories: ${config.categoryWhitelist.joinToString()}")
        }
        if (config.languageWhitelist.isNotEmpty()) {
            rules.add("Languages: ${config.languageWhitelist.joinToString()}")
        }
        if (config.countryWhitelist.isNotEmpty()) {
            rules.add("Countries: ${config.countryWhitelist.joinToString()}")
        }
        if (config.nameIncludePatterns.isNotEmpty()) {
            rules.add("Include patterns: ${config.nameIncludePatterns.size}")
        }
        if (config.nameExcludePatterns.isNotEmpty()) {
            rules.add("Exclude patterns: ${config.nameExcludePatterns.size}")
        }
        
        if (rules.isNotEmpty()) {
            scraperState.debug("Active filters", rules.joinToString("\n"))
        } else {
            scraperState.debug("No filters active")
        }
    }
}

@Serializable
private data class SearchResponseWithTotal(
    val result: SearchResultWithTotal,
)

@Serializable
private data class SearchResultWithTotal(
    val results: List<AceStreamChannel>,
    val total: Int = 0,
)
