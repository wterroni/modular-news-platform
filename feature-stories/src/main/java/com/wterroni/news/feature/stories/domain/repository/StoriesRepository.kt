package com.wterroni.news.feature.stories.domain.repository

import com.wterroni.news.feature.stories.domain.model.Story

interface StoriesRepository {
    suspend fun getTopStories(limit: Int, offset: Int): List<Story>
}
