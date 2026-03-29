package com.wterroni.news.feature.stories.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.wterroni.news.feature.stories.presentation.viewmodel.StoriesViewModel
import com.wterroni.news.feature.stories.domain.model.Story
import com.wterroni.news.feature.stories.presentation.utils.openCustomTab

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun StoriesScreen() {
    val viewModel: StoriesViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = { viewModel.loadStories() },
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            uiState.isLoading && uiState.stories.isEmpty() -> {
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
                            isFavorite = isFavoriteState,
                            onItemClick = { 
                                story.url?.let { url ->
                                    openCustomTab(context, url)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StoryItem(
    story: Story,
    onToggleFavorite: () -> Unit,
    isFavorite: androidx.compose.runtime.State<Boolean>,
    onItemClick: () -> Unit
) {
    val isFav by isFavorite
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onItemClick() },
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
