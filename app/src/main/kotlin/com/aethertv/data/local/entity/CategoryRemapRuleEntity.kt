package com.aethertv.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_remap_rules")
data class CategoryRemapRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourcePattern: String,
    val targetCategory: String,
    val isEnabled: Boolean = true,
)
