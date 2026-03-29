package com.wterroni.news.feature.stories.data.repository

import com.wterroni.news.core.data.datastore.AuthDataStore
import com.wterroni.news.feature.stories.data.api.HackerNewsApi
import com.wterroni.news.feature.stories.data.local.FavoriteStoriesLocalDataSource
import com.wterroni.news.feature.stories.data.local.StoryLocalDataSource
import com.wterroni.news.feature.stories.data.model.StoryDto
import com.wterroni.news.feature.stories.domain.model.Story
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class StoriesRepositoryImplTest {

    private val api: HackerNewsApi = mockk()
    private val storyLocalDataSource: StoryLocalDataSource = mockk()
    private val favoriteStoriesLocalDataSource: FavoriteStoriesLocalDataSource = mockk()
    private val authDataStore: AuthDataStore = mockk()
    
    private val repository = StoriesRepositoryImpl(api, storyLocalDataSource, favoriteStoriesLocalDataSource, authDataStore)

    private val testStoryDto = StoryDto(
        id = 1L,
        by = "Test Author",
        title = "Test Story",
        score = 100,
        time = System.currentTimeMillis() / 1000,
        descendants = 10,
        url = "https://example.com"
    )

    private val testStory = Story(
        id = 1L,
        author = "Test Author",
        title = "Test Story",
        score = 100,
        time = System.currentTimeMillis() / 1000,
        commentCount = 10,
        url = "https://example.com"
    )

    @Test
    fun `loadMoreStories calls API correctly and saves to database`() = runTest {
        // Given
        val offset = 20
        val limit = 10
        val storyIds = (1L..50L).toList()
        val expectedIds = storyIds.drop(offset).take(limit)
        
        coEvery { api.getTopStories() } returns storyIds
        coEvery { api.getStory(match { it > 0 }) } returns testStoryDto
        coEvery { storyLocalDataSource.insertStories(match { it.isNotEmpty() }) } returns Unit
        
        // When
        repository.loadMoreStories(offset, limit)
        
        // Then
        coVerify { api.getTopStories() }
        expectedIds.forEach { id ->
            coVerify { api.getStory(id) }
        }
        coVerify { storyLocalDataSource.insertStories(match { it.isNotEmpty() }) }
    }

    @Test
    fun `loadMoreStories with empty list does nothing`() = runTest {
        // Given
        val offset = 100
        val limit = 10
        val storyIds = (1L..10L).toList()
        
        coEvery { api.getTopStories() } returns storyIds
        
        // When
        repository.loadMoreStories(offset, limit)
        
        // Then
        coVerify { api.getTopStories() }
        coVerify(exactly = 0) { api.getStory(match { it > 0 }) }
        coVerify(exactly = 0) { storyLocalDataSource.insertStories(match { it.isNotEmpty() }) }
    }

    @Test
    fun `refreshStories calls API and clears existing data`() = runTest {
        // Given
        val storyIds = (1L..30L).toList()
        
        coEvery { api.getTopStories() } returns storyIds
        coEvery { api.getStory(match { it > 0 }) } returns testStoryDto
        coEvery { storyLocalDataSource.clearAll() } returns Unit
        coEvery { storyLocalDataSource.saveStories(match { it.isNotEmpty() }) } returns Unit
        
        // When
        repository.refreshStories()
        
        // Then
        coVerify { api.getTopStories() }
        coVerify { storyLocalDataSource.clearAll() }
        coVerify { storyLocalDataSource.saveStories(match { it.isNotEmpty() }) }
    }

    @Test
    fun `refreshStories handles API errors gracefully`() = runTest {
        // Given
        coEvery { api.getTopStories() } throws Exception("Network error")
        
        // When
        repository.refreshStories()
        
        // Then
        coVerify { api.getTopStories() }
        coVerify(exactly = 0) { storyLocalDataSource.clearAll() }
        coVerify(exactly = 0) { storyLocalDataSource.saveStories(match { it.isNotEmpty() }) }
    }

    @Test
    fun `toggleFavorite when not favorited adds to favorites`() = runTest {
        // Given
        val userEmail = "test@example.com"
        coEvery { authDataStore.getUserEmail() } returns flowOf(userEmail)
        coEvery { favoriteStoriesLocalDataSource.isFavorite(testStory.id, userEmail) } returns flowOf(false)
        coEvery { favoriteStoriesLocalDataSource.addFavorite(testStory, userEmail) } returns Unit
        
        // When
        repository.toggleFavorite(testStory)
        
        // Then
        coVerify { authDataStore.getUserEmail() }
        coVerify { favoriteStoriesLocalDataSource.isFavorite(testStory.id, userEmail) }
        coVerify { favoriteStoriesLocalDataSource.addFavorite(testStory, userEmail) }
        coVerify(exactly = 0) { favoriteStoriesLocalDataSource.removeFavorite(any(), any()) }
    }

    @Test
    fun `toggleFavorite when already favorited removes from favorites`() = runTest {
        // Given
        val userEmail = "test@example.com"
        coEvery { authDataStore.getUserEmail() } returns flowOf(userEmail)
        coEvery { favoriteStoriesLocalDataSource.isFavorite(testStory.id, userEmail) } returns flowOf(true)
        coEvery { favoriteStoriesLocalDataSource.removeFavorite(testStory, userEmail) } returns Unit
        
        // When
        repository.toggleFavorite(testStory)
        
        // Then
        coVerify { authDataStore.getUserEmail() }
        coVerify { favoriteStoriesLocalDataSource.isFavorite(testStory.id, userEmail) }
        coVerify { favoriteStoriesLocalDataSource.removeFavorite(testStory, userEmail) }
        coVerify(exactly = 0) { favoriteStoriesLocalDataSource.addFavorite(any(), any()) }
    }

    @Test
    fun `isFavorite returns true when story is favorited`() = runTest {
        // Given
        val storyId = 1L
        val userEmail = "test@example.com"
        coEvery { authDataStore.getUserEmail() } returns flowOf(userEmail)
        coEvery { favoriteStoriesLocalDataSource.isFavorite(storyId, userEmail) } returns flowOf(true)
        
        // When
        val result = repository.isFavorite(storyId)
        
        // Then
        assert(result == true)
        coVerify { authDataStore.getUserEmail() }
        coVerify { favoriteStoriesLocalDataSource.isFavorite(storyId, userEmail) }
    }

    @Test
    fun `isFavorite returns false when user is not logged in`() = runTest {
        // Given
        val storyId = 1L
        coEvery { authDataStore.getUserEmail() } returns flowOf(null)
        
        // When
        val result = repository.isFavorite(storyId)
        
        // Then
        assert(result == false)
        coVerify { authDataStore.getUserEmail() }
        coVerify(exactly = 0) { favoriteStoriesLocalDataSource.isFavorite(any(), any()) }
    }

    @Test
    fun `getFavorites returns user favorites`() = runTest {
        // Given
        val userEmail = "test@example.com"
        val favorites = listOf(testStory)
        coEvery { authDataStore.getUserEmail() } returns flowOf(userEmail)
        coEvery { favoriteStoriesLocalDataSource.getFavorites(userEmail) } returns flowOf(favorites)
        
        // When
        val result = repository.getFavorites()
        
        // Then
        assert(result == favorites)
        coVerify { authDataStore.getUserEmail() }
        coVerify { favoriteStoriesLocalDataSource.getFavorites(userEmail) }
    }

    @Test
    fun `getFavorites returns empty list when user is not logged in`() = runTest {
        // Given
        coEvery { authDataStore.getUserEmail() } returns flowOf(null)
        
        // When
        val result = repository.getFavorites()
        
        // Then
        assert(result.isEmpty())
        coVerify { authDataStore.getUserEmail() }
        coVerify(exactly = 0) { favoriteStoriesLocalDataSource.getFavorites(any()) }
    }
}
