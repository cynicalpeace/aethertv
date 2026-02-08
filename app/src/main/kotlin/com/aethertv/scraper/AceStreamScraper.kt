package com.aethertv.scraper

import com.aethertv.data.remote.AceStreamChannel
import com.aethertv.data.remote.AceStreamEngineClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AceStreamScraper @Inject constructor(
    private val engineClient: AceStreamEngineClient,
    private val channelFilter: ChannelFilter,
) {
    suspend fun scrape(config: FilterConfig): List<AceStreamChannel> {
        engineClient.waitForConnection()
        val raw = engineClient.searchAll()
        return raw.applyFilters(config)
    }
}
