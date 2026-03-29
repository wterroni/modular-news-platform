package com.wterroni.news.feature.stories.domain.usecase

import com.wterroni.news.feature.stories.domain.repository.StoriesRepository
import android.util.Log

class LoadMoreStoriesUseCase(
    private val repository: StoriesRepository
) {
    suspend operator fun invoke(offset: Int, limit: Int) {
        Log.d("NewsApp", "Chamando repository.loadMoreStories(offset=$offset, limit=$limit)")
        repository.loadMoreStories(offset, limit)
        Log.d("NewsApp", "repository.loadMoreStories concluído")
    }
}
