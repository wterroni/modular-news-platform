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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.wterroni.news.feature.stories.presentation.viewmodel.StoriesViewModel
import com.wterroni.news.feature.stories.domain.model.Story
import com.wterroni.news.core.common.utils.toRelativeTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoriesScreen(
    onNavigateToStoryDetail: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val viewModel: StoriesViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsState().value
    var showLogoutDialog by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { firstVisibleIndex ->
                val totalItems = listState.layoutInfo.totalItemsCount
                val lastVisibleItem = firstVisibleIndex + listState.layoutInfo.visibleItemsInfo.size
                
                val shouldLoadMore = lastVisibleItem >= totalItems - 5
                
                if (shouldLoadMore) {
                    viewModel.loadMore()
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Top Stories",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showLogoutDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout"
                        )
                    }
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
            PullToRefreshBox(
                isRefreshing = uiState.isLoading,
                onRefresh = { viewModel.loadStories() },
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    uiState.isLoading && uiState.stories.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    uiState.error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Error loading stories",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = uiState.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    uiState.stories.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No stories available",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = listState,
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = uiState.stories,
                                key = { it.id }
                            ) { story ->
                                val favoriteStates = viewModel.favoriteStates.collectAsState()
                                val isFavoriteFlow = viewModel.isFavorite(story.id)
                                val isFav by remember {
                                    derivedStateOf {
                                        favoriteStates.value[story.id] ?: false
                                    }
                                }
                                
                                LaunchedEffect(story.id) {
                                    isFavoriteFlow.collect { favState ->
                                        val currentStates = favoriteStates.value.toMutableMap()
                                        currentStates[story.id] = favState
                                    }
                                }
                                
                                StoryItem(
                                    story = story,
                                    onToggleFavorite = { 
                                        viewModel.toggleFavorite(story) 
                                    },
                                    isFavorite = remember { derivedStateOf { favoriteStates.value[story.id] ?: false } },
                                    onItemClick = { 
                                        story.url?.let { url ->
                                            onNavigateToStoryDetail(url)
                                        }
                                    }
                                )
                            }
                            
                            if (uiState.isLoadingMore) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout")
                }
            },
            text = {
                Text("Do you really want to logout?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.logout()
                        onLogout()
                        showLogoutDialog = false
                    }
                ) {
                    Text(
                        "Logout",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
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

