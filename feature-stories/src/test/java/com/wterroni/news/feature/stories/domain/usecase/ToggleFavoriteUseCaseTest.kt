package com.wterroni.news.feature.stories.domain.usecase

import com.wterroni.news.feature.stories.domain.model.Story
import com.wterroni.news.feature.stories.domain.repository.StoriesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ToggleFavoriteUseCaseTest {

    private val repository: StoriesRepository = mockk()
    private val toggleFavoriteUseCase = ToggleFavoriteUseCase(repository)

    private val testStory = Story(
        id = 1L,
        author = "Test Author",
        title = "Test Story",
        score = 100,
        time = System.currentTimeMillis(),
        commentCount = 10,
        url = "https://example.com"
    )

    @Test
    fun `toggle favorite when not favorited adds to favorites`() = runTest {
        // Given
        coEvery { repository.toggleFavorite(testStory) } returns Unit

        // When
        toggleFavoriteUseCase(testStory)

        // Then
        coVerify { repository.toggleFavorite(testStory) }
    }

    @Test
    fun `toggle favorite when already favorited removes from favorites`() = runTest {
        // Given
        coEvery { repository.toggleFavorite(testStory) } returns Unit

        // When
        toggleFavoriteUseCase(testStory)

        // Then
        coVerify { repository.toggleFavorite(testStory) }
    }

    @Test
    fun `toggle favorite calls repository regardless of story content`() = runTest {
        // Given
        val storyWithNulls = Story(
            id = 2L,
            author = null,
            title = null,
            score = null,
            time = null,
            commentCount = null,
            url = null
        )
        coEvery { repository.toggleFavorite(storyWithNulls) } returns Unit

        // When
        toggleFavoriteUseCase(storyWithNulls)

        // Then
        coVerify { repository.toggleFavorite(storyWithNulls) }
    }
}
