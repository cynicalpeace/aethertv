package com.aethertv.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "filter_rules")
data class FilterRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,       // "name_include", "name_exclude", "category", "language", "country"
    val pattern: String,
    val isEnabled: Boolean = true,
)
