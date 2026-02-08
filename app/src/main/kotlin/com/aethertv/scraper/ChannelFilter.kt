package com.aethertv.scraper

import com.aethertv.data.remote.AceStreamChannel
import javax.inject.Inject
import javax.inject.Singleton

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
)

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

fun List<AceStreamChannel>.applyFilters(config: FilterConfig): List<AceStreamChannel> {
    return this
        .map { it.remapCategories(config.categoryRemapRules) }
        .map { it.assignCategoriesByName(config.nameToCategoryRules) }
        .filter { config.statusFilter.isEmpty() || it.status in config.statusFilter }
        .filter { it.availability >= config.availabilityThreshold }
        .filter { it.matchesCategories(config.categoryWhitelist, config.strict) }
        .filter { it.matchesLanguages(config.languageWhitelist) }
        .filter { it.matchesCountries(config.countryWhitelist) }
        .filter { it.matchesNameRegex(config.nameIncludePatterns) }
        .filterNot { it.matchesNameRegex(config.nameExcludePatterns) }
}

fun AceStreamChannel.remapCategories(rules: List<CategoryRemapRule>): AceStreamChannel {
    if (rules.isEmpty()) return this
    val remapped = categories.map { cat ->
        rules.firstOrNull { Regex(it.sourcePattern, RegexOption.IGNORE_CASE).matches(cat) }
            ?.targetCategory ?: cat
    }
    return copy(categories = remapped)
}

fun AceStreamChannel.assignCategoriesByName(rules: List<NameToCategoryRule>): AceStreamChannel {
    if (categories.isNotEmpty()) return this
    val matched = rules.firstOrNull { Regex(it.namePattern, RegexOption.IGNORE_CASE).containsMatchIn(name) }
    return if (matched != null) copy(categories = listOf(matched.category)) else this
}

fun AceStreamChannel.matchesCategories(whitelist: Set<String>, strict: Boolean): Boolean {
    if (whitelist.isEmpty()) return true
    return if (strict) {
        categories.all { it.lowercase() in whitelist.map { w -> w.lowercase() } }
    } else {
        categories.any { it.lowercase() in whitelist.map { w -> w.lowercase() } }
    }
}

fun AceStreamChannel.matchesLanguages(whitelist: Set<String>): Boolean {
    if (whitelist.isEmpty()) return true
    return languages.any { it.lowercase() in whitelist.map { w -> w.lowercase() } }
}

fun AceStreamChannel.matchesCountries(whitelist: Set<String>): Boolean {
    if (whitelist.isEmpty()) return true
    return countries.any { it.lowercase() in whitelist.map { w -> w.lowercase() } }
}

fun AceStreamChannel.matchesNameRegex(patterns: List<String>): Boolean {
    if (patterns.isEmpty()) return false
    return patterns.any { Regex(it, RegexOption.IGNORE_CASE).containsMatchIn(name) }
}
