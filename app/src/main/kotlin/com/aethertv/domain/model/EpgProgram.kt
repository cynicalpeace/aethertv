package com.aethertv.domain.model

data class EpgProgram(
    val id: Long,
    val channelId: String,
    val title: String,
    val description: String?,
    val startTime: Long,
    val endTime: Long,
    val category: String?,
    val iconUrl: String?,
)
