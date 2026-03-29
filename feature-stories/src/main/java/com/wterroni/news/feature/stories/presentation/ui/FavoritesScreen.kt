package com.wterroni.news.feature.stories.presentation.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.wterroni.news.feature.stories.presentation.viewmodel.FavoritesViewModel
import com.wterroni.news.feature.stories.domain.model.Story
import com.wterroni.news.core.common.utils.toRelativeTime
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onNavigateToStoryDetail: (String) -> Unit = {},
    viewModel: FavoritesViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Favorites",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.favorites.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.StarBorder,
                                contentDescription = "No favorites",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No favorites yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Star stories to save them here",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.favorites,
                            key = { it.id }
                        ) { story ->
                            val favoriteStates = viewModel.favoriteStates.collectAsState()
                            val isFavoriteFlow = viewModel.isFavorite(story.id)
                            val isFav by remember {
                                derivedStateOf {
                                    favoriteStates.value[story.id] ?: true
                                }
                            }
                            
                            LaunchedEffect(story.id) {
                                isFavoriteFlow.collect { favState ->
                                    val currentStates = favoriteStates.value.toMutableMap()
                                    currentStates[story.id] = favState
                                }
                            }
                            
                            FavoriteStoryItem(
                                story = story,
                                onToggleFavorite = { 
                                    viewModel.toggleFavorite(story) 
                                },
                                isFavorite = remember { derivedStateOf { favoriteStates.value[story.id] ?: true } },
                                onItemClick = { 
                                    story.url?.let { url ->
                                        onNavigateToStoryDetail(url)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteStoryItem(
    story: Story,
    onToggleFavorite: () -> Unit,
    isFavorite: androidx.compose.runtime.State<Boolean>,
    onItemClick: () -> Unit
) {
    val isFav = isFavorite.value
    
    val favoriteColor by animateColorAsState(
        targetValue = if (isFav) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 300),
        label = "favoriteColor"
    )
    
    val favoriteScale by animateFloatAsState(
        targetValue = if (isFav) 1.2f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "favoriteScale"
    )
    
    ElevatedCard(
        onClick = onItemClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "By: ${story.author ?: "Unknown"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Score: ${story.score ?: 0}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                story.time?.let { time ->
                    Text(
                        text = time.toRelativeTime(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                story.commentCount?.let { comments ->
                    if (comments > 0) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$comments comments",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            IconButton(
                onClick = onToggleFavorite
            ) {
                Icon(
                    imageVector = if (isFav) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (isFav) "Remove from favorites" else "Add to favorites",
                    tint = favoriteColor,
                    modifier = Modifier.scale(favoriteScale)
                )
            }
        }
    }
}
