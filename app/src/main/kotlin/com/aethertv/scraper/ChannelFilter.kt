package com.aethertv.scraper

import android.util.Log
import com.aethertv.data.remote.AceStreamChannel
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ChannelFilter"

data class FilterConfig(
    val categoryRemapRules: List<CategoryRemapRule> = emptyList(),
    val nameToCategoryRules: List<NameToCategoryRule> = emptyList(),
    val statusFilter: Set<Int> = emptySet(),
    val availabilityThreshold: Float = 0f,
    val categoryWhitelist: Set<String> = emptySet(),
    val strict: Boolean = false,
    val languageWhitelist: Set<String> = emptySet(),
    val countryWhitelist: Set<String> = emptySet(),
    val nameIncludePatterns: List<String> = emptyList(),
    val nameExcludePatterns: List<String> = emptyList(),
) {
    // Pre-compiled regex patterns for performance
    // Compiled once when config is created, reused for all channels
    // Invalid patterns are logged and skipped instead of crashing (H21 fix)
    val compiledIncludePatterns: List<Regex> by lazy {
        nameIncludePatterns.mapNotNull { pattern ->
            try {
                Regex(pattern, RegexOption.IGNORE_CASE)
            } catch (e: Exception) {
                Log.e(TAG, "Invalid include pattern '$pattern': ${e.message}")
                null
            }
        }
    }
    
    val compiledExcludePatterns: List<Regex> by lazy {
        nameExcludePatterns.mapNotNull { pattern ->
            try {
                Regex(pattern, RegexOption.IGNORE_CASE)
            } catch (e: Exception) {
                Log.e(TAG, "Invalid exclude pattern '$pattern': ${e.message}")
                null
            }
        }
    }
    
    val compiledCategoryRemapPatterns: List<Pair<Regex, String>> by lazy {
        categoryRemapRules.mapNotNull { rule ->
            try {
                Regex(rule.sourcePattern, RegexOption.IGNORE_CASE) to rule.targetCategory
            } catch (e: Exception) {
                Log.e(TAG, "Invalid category remap pattern '${rule.sourcePattern}': ${e.message}")
                null
            }
        }
    }
    
    val compiledNameToCategoryPatterns: List<Pair<Regex, String>> by lazy {
        nameToCategoryRules.mapNotNull { rule ->
            try {
                Regex(rule.namePattern, RegexOption.IGNORE_CASE) to rule.category
            } catch (e: Exception) {
                Log.e(TAG, "Invalid name-to-category pattern '${rule.namePattern}': ${e.message}")
                null
            }
        }
    }
    
    // Pre-computed lowercase sets using Locale.ROOT for consistent matching (H32 fix)
    // This prevents issues in Turkish locale where "I".lowercase() = "ı"
    val lowercaseCategoryWhitelist: Set<String> by lazy {
        categoryWhitelist.map { it.lowercase(Locale.ROOT) }.toSet()
    }
    
    val lowercaseLanguageWhitelist: Set<String> by lazy {
        languageWhitelist.map { it.lowercase(Locale.ROOT) }.toSet()
    }
    
    val lowercaseCountryWhitelist: Set<String> by lazy {
        countryWhitelist.map { it.lowercase(Locale.ROOT) }.toSet()
    }
}

data class CategoryRemapRule(
    val sourcePattern: String,
    val targetCategory: String,
)

data class NameToCategoryRule(
    val namePattern: String,
    val category: String,
)

@Singleton
class ChannelFilter @Inject constructor()

/**
 * Apply all filters to a list of channels.
 * Uses pre-compiled regex patterns from FilterConfig for performance.
 */
fun List<AceStreamChannel>.applyFilters(config: FilterConfig): List<AceStreamChannel> {
    return this
        .map { it.remapCategories(config) }
        .map { it.assignCategoriesByName(config) }
        .filter { config.statusFilter.isEmpty() || it.status in config.statusFilter }
        .filter { it.availability >= config.availabilityThreshold }
        .filter { it.matchesCategories(config) }
        .filter { it.matchesLanguages(config) }
        .filter { it.matchesCountries(config) }
        .filter { it.matchesIncludePatterns(config) }
        .filterNot { it.matchesExcludePatterns(config) }
}

/**
 * Remap categories using pre-compiled patterns from config.
 */
fun AceStreamChannel.remapCategories(config: FilterConfig): AceStreamChannel {
    if (config.compiledCategoryRemapPatterns.isEmpty()) return this
    val remapped = categories.map { cat ->
        config.compiledCategoryRemapPatterns.firstOrNull { (regex, _) -> regex.matches(cat) }
            ?.second ?: cat
    }
    return copy(categories = remapped)
}

/**
 * Assign category based on name using pre-compiled patterns from config.
 */
fun AceStreamChannel.assignCategoriesByName(config: FilterConfig): AceStreamChannel {
    if (categories.isNotEmpty()) return this
    val matched = config.compiledNameToCategoryPatterns.firstOrNull { (regex, _) -> 
        regex.containsMatchIn(name) 
    }
    return if (matched != null) copy(categories = listOf(matched.second)) else this
}

/**
 * Check if channel matches category whitelist using pre-computed lowercase set.
 * Uses Locale.ROOT for consistent matching across all locales (H32 fix).
 */
fun AceStreamChannel.matchesCategories(config: FilterConfig): Boolean {
    if (config.lowercaseCategoryWhitelist.isEmpty()) return true
    return if (config.strict) {
        categories.all { it.lowercase(Locale.ROOT) in config.lowercaseCategoryWhitelist }
    } else {
        categories.any { it.lowercase(Locale.ROOT) in config.lowercaseCategoryWhitelist }
    }
}

/**
 * Check if channel matches language whitelist using pre-computed lowercase set.
 * Uses Locale.ROOT for consistent matching across all locales (H32 fix).
 */
fun AceStreamChannel.matchesLanguages(config: FilterConfig): Boolean {
    if (config.lowercaseLanguageWhitelist.isEmpty()) return true
    return languages.any { it.lowercase(Locale.ROOT) in config.lowercaseLanguageWhitelist }
}

/**
 * Check if channel matches country whitelist using pre-computed lowercase set.
 * Uses Locale.ROOT for consistent matching across all locales (H32 fix).
 */
fun AceStreamChannel.matchesCountries(config: FilterConfig): Boolean {
    if (config.lowercaseCountryWhitelist.isEmpty()) return true
    return countries.any { it.lowercase(Locale.ROOT) in config.lowercaseCountryWhitelist }
}

/**
 * Check if channel name matches any include pattern using pre-compiled regex.
 */
fun AceStreamChannel.matchesIncludePatterns(config: FilterConfig): Boolean {
    if (config.compiledIncludePatterns.isEmpty()) return true // No include = include all
    return config.compiledIncludePatterns.any { it.containsMatchIn(name) }
}

/**
 * Check if channel name matches any exclude pattern using pre-compiled regex.
 */
fun AceStreamChannel.matchesExcludePatterns(config: FilterConfig): Boolean {
    if (config.compiledExcludePatterns.isEmpty()) return false // No exclude = exclude none
    return config.compiledExcludePatterns.any { it.containsMatchIn(name) }
}
