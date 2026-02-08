package com.aethertv.epg

import com.aethertv.data.local.entity.EpgChannelEntity
import com.aethertv.data.remote.AceStreamChannel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpgMatcher @Inject constructor() {

    fun findBestMatch(
        aceChannel: AceStreamChannel,
        epgChannels: List<EpgChannelEntity>,
    ): EpgChannelEntity? {
        // Tier 1: Exact ID match (if channel has epg ID metadata)
        // Not currently available from engine search API, but future-proofed

        // Tier 2: Fuzzy name match
        val normalized = aceChannel.name.normalizeForMatching()
        return epgChannels
            .map { it to it.displayName.normalizeForMatching().tokenSimilarity(normalized) }
            .filter { it.second > 0.7 }
            .maxByOrNull { it.second }
            ?.first
    }

    fun findBestMatchByName(
        channelName: String,
        epgChannels: List<EpgChannelEntity>,
    ): EpgChannelEntity? {
        val normalized = channelName.normalizeForMatching()
        return epgChannels
            .map { it to it.displayName.normalizeForMatching().tokenSimilarity(normalized) }
            .filter { it.second > 0.7 }
            .maxByOrNull { it.second }
            ?.first
    }

    companion object {
        private val QUALITY_REGEX = Regex("\\b(hd|fhd|sd|uhd|4k|1080p|720p|480p)\\b")
        private val COUNTRY_REGEX = Regex("\\b(us|usa|uk|eu|int)\\b")
        private val NON_ALPHANUMERIC = Regex("[^a-z0-9\\s]")
        private val MULTI_SPACE = Regex("\\s+")

        fun String.normalizeForMatching(): String {
            return this
                .lowercase()
                .replace(QUALITY_REGEX, "")
                .replace(COUNTRY_REGEX, "")
                .replace(NON_ALPHANUMERIC, "")
                .trim()
                .replace(MULTI_SPACE, " ")
        }

        fun String.tokenSimilarity(other: String): Double {
            val tokens1 = this.split(" ").filter { it.isNotBlank() }.toSet()
            val tokens2 = other.split(" ").filter { it.isNotBlank() }.toSet()
            if (tokens1.isEmpty() || tokens2.isEmpty()) return 0.0
            val intersection = tokens1.intersect(tokens2).size
            return intersection.toDouble() / maxOf(tokens1.size, tokens2.size)
        }
    }
}
