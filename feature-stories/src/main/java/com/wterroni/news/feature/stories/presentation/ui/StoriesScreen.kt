package com.wterroni.news.feature.stories.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wterroni.news.feature.stories.domain.model.Story
import com.wterroni.news.feature.stories.presentation.viewmodel.StoriesViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun StoriesScreen(
    viewModel: StoriesViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.loadStories()
    }

    when {
        uiState.isLoading -> {
            Text(
                text = "Loading...",
                modifier = Modifier.fillMaxSize(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        uiState.error != null -> {
            Text(
                text = "Error: ${uiState.error}",
                modifier = Modifier.fillMaxSize(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = uiState.stories,
                    key = { story: Story -> story.id }
                ) { story ->
                    StoryItem(
                        title = story.title ?: "No title",
                        author = story.author ?: "Unknown",
                        score = story.score ?: 0
                    )
                }
            }
        }
    }
}

@Composable
private fun StoryItem(
    title: String,
    author: String,
    score: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "By: $author",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "Score: $score",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
