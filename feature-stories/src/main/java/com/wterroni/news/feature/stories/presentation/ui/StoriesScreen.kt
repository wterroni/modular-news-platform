package com.wterroni.news.feature.stories.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.wterroni.news.feature.stories.presentation.viewmodel.StoriesViewModel
import com.wterroni.news.feature.stories.domain.model.Story

@Composable
fun StoriesScreen() {
    val viewModel: StoriesViewModel = koinViewModel()
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
                items(uiState.stories) { story ->
                    val isFavoriteState = viewModel.isFavorite(story.id).collectAsState(initial = false)
                    StoryItem(
                        story = story,
                        onToggleFavorite = { viewModel.toggleFavorite(story) },
                        isFavorite = isFavoriteState
                    )
                }
            }
        }
    }
}

@Composable
private fun StoryItem(
    story: Story,
    onToggleFavorite: () -> Unit,
    isFavorite: androidx.compose.runtime.State<Boolean>
) {
    val isFav by isFavorite
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = story.title ?: "No title",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "By: ${story.author ?: "Unknown"}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Score: ${story.score ?: 0}",
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        IconButton(
            onClick = onToggleFavorite
        ) {
            Icon(
                imageVector = if (isFav) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = if (isFav) "Remove from favorites" else "Add to favorites",
                tint = if (isFav) androidx.compose.ui.graphics.Color.Yellow else androidx.compose.ui.graphics.Color.Gray
            )
        }
    }
}
