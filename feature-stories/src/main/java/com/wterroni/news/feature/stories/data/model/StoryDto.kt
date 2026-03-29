package com.wterroni.news.feature.stories.data.model

import kotlinx.serialization.Serializable

@Serializable
data class StoryDto(
    val id: Long,
    val by: String? = null,
    val title: String? = null,
    val score: Int? = null,
    val time: Long? = null,
    val descendants: Int? = null,
    val url: String? = null
)
