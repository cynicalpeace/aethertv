package com.aethertv.scraper

import com.aethertv.data.local.entity.CategoryRemapRuleEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryMapper @Inject constructor() {

    fun toFilterRules(entities: List<CategoryRemapRuleEntity>): List<CategoryRemapRule> {
        return entities
            .filter { it.isEnabled }
            .map { CategoryRemapRule(sourcePattern = it.sourcePattern, targetCategory = it.targetCategory) }
    }
}
