package com.wterroni.news.feature.stories.domain.model

data class Story(
    val id: Long,
    val author: String?,
    val title: String?,
    val score: Int?,
    val time: Long?,
    val commentCount: Int?,
    val url: String?
)
