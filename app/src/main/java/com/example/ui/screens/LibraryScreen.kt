package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.CustomListEntity
import com.example.data.local.LibraryEntryEntity
import com.example.data.local.WatchedEpisodeEntity
import com.example.data.model.AnimeItem
import com.example.ui.components.AnimeArtworkImage
import com.example.ui.theme.CoralWarning
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.GlassCardHighlight
import com.example.ui.theme.GlassCardSurface
import com.example.ui.theme.LuminousPurple
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.SubtleBorderGlow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LibraryScreen(
    watchlist: List<LibraryEntryEntity>,
    continueWatching: List<LibraryEntryEntity>,
    favorites: List<LibraryEntryEntity>,
    allLibraryEntries: List<LibraryEntryEntity>,
    watchedEpisodes: List<WatchedEpisodeEntity>,
    customLists: List<CustomListEntity>,
    catalog: List<AnimeItem>,
    onAnimeClick: (Int) -> Unit,
    onPlayClick: (Int, Int) -> Unit,
    onToggleWatchlist: (AnimeItem) -> Unit,
    onToggleFavorite: (AnimeItem) -> Unit,
    onAssignCustomList: (AnimeItem, String?) -> Unit,
    onCreateCustomList: (String, String) -> Unit,
    onDeleteCustomList: (String) -> Unit,
    onRemoveEntry: (Int) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showCreateListDialog by remember { mutableStateOf(false) }
    val tabs = listOf("Watchlist", "Continue Watching", "Favorites", "History", "Custom Lists")

    val currentEntries = when (selectedTab) {
        0 -> watchlist
        1 -> continueWatching
        2 -> favorites
        3 -> allLibraryEntries.filter { it.lastWatchedEpisode > 0 }
        else -> allLibraryEntries.filter { !it.customListName.isNullOrBlank() }
    }

    if (showCreateListDialog) {
        CreateCustomListDialog(
            onDismiss = { showCreateListDialog = false },
            onConfirm = { name, desc ->
                onCreateCustomList(name, desc)
                showCreateListDialog = false
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("library_screen_list"),
        contentPadding = PaddingValues(bottom = 110.dp)
    ) {
        // Title Header matching Screen 7
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Library",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White
                )

                Surface(
                    color = GlassCardSurface,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .border(1.dp, SubtleBorderGlow, RoundedCornerShape(50))
                        .clip(RoundedCornerShape(50))
                        .clickable { showCreateListDialog = true }
                        .testTag("create_custom_list_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New List",
                            tint = SoftLavender,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "New List",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Segmented Pill Tabs matching Screen 7 ("Watchlist", "Continue Watching", "Favorites", "History", "Custom Lists")
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tabs.size) { idx ->
                    val isSelected = selectedTab == idx
                    Surface(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (isSelected) {
                                    Brush.horizontalGradient(listOf(LuminousPurple, ElectricViolet))
                                } else {
                                    Brush.horizontalGradient(listOf(GlassCardSurface, GlassCardSurface))
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) SoftLavender else SubtleBorderGlow,
                                shape = RoundedCornerShape(50)
                            )
                            .clickable { selectedTab = idx }
                            .testTag("library_tab_${tabs[idx].lowercase().replace(" ", "_")}")
                    ) {
                        Text(
                            text = tabs[idx],
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Custom Lists overview when on Custom Lists tab
        if (selectedTab == 4 && customLists.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    customLists.forEach { customList ->
                        val count = allLibraryEntries.count { it.customListName == customList.name }
                        Surface(
                            color = GlassCardSurface,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, SubtleBorderGlow, RoundedCornerShape(16.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = customList.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${customList.description.ifBlank { "Custom collection" }} • $count titles",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                                IconButton(onClick = { onDeleteCustomList(customList.name) }) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete ${customList.name}",
                                        tint = CoralWarning
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Clear History action on History tab
        if (selectedTab == 3 && (currentEntries.isNotEmpty() || watchedEpisodes.isNotEmpty())) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${watchedEpisodes.size} episodes tracked locally",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "Clear History",
                        style = MaterialTheme.typography.labelMedium,
                        color = CoralWarning,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable(onClick = onClearHistory)
                            .padding(4.dp)
                    )
                }
            }
        }

        if (currentEntries.isEmpty()) {
            item {
                Surface(
                    color = GlassCardSurface,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .border(1.dp, SubtleBorderGlow, RoundedCornerShape(20.dp))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(28.dp)
                    ) {
                        Text(
                            text = "Your ${tabs[selectedTab]} is empty",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Explore Home or Discover to bookmark anime, save favorites, or start watching.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(currentEntries, key = { it.animeId }) { entry ->
                val fullAnime = catalog.find { it.id == entry.animeId }
                LibraryEntryRow(
                    entry = entry,
                    customLists = customLists,
                    showProgress = selectedTab == 1 || selectedTab == 3,
                    onClick = { onAnimeClick(entry.animeId) },
                    onPlayClick = { onPlayClick(entry.animeId, entry.lastWatchedEpisode.coerceAtLeast(1)) },
                    onToggleWatchlist = { fullAnime?.let(onToggleWatchlist) },
                    onToggleFavorite = { fullAnime?.let(onToggleFavorite) },
                    onAssignCustomList = { listName ->
                        if (fullAnime != null) onAssignCustomList(fullAnime, listName)
                    },
                    onRemove = { onRemoveEntry(entry.animeId) }
                )
            }
        }
    }
}

@Composable
private fun LibraryEntryRow(
    entry: LibraryEntryEntity,
    customLists: List<CustomListEntity>,
    showProgress: Boolean,
    onClick: () -> Unit,
    onPlayClick: () -> Unit,
    onToggleWatchlist: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAssignCustomList: (String?) -> Unit,
    onRemove: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("library_item_${entry.animeId}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimeArtworkImage(
            imageUrl = entry.posterUrl,
            contentDescription = entry.title,
            accentSeed = entry.animeId,
            modifier = Modifier
                .size(width = 68.dp, height = 68.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, SubtleBorderGlow, RoundedCornerShape(14.dp))
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = if (entry.lastWatchedEpisode > 0) {
                    "Ep ${entry.lastWatchedEpisode} / ${entry.episodesCount} eps"
                } else {
                    "${entry.episodesCount} eps"
                },
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            if (!entry.customListName.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "List: ${entry.customListName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftLavender
                )
            }

            if (showProgress && entry.lastWatchedEpisode > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                val progress = (entry.lastPlaybackPositionMs.toFloat() / entry.lastDurationMs.coerceAtLeast(1L).toFloat())
                    .coerceIn(0.1f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    color = ElectricViolet,
                    trackColor = GlassCardHighlight,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }

        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "Options for ${entry.title}",
                    tint = TextSecondary
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                modifier = Modifier.background(GlassCardSurface)
            ) {
                DropdownMenuItem(
                    text = { Text("Resume / Watch", color = Color.White) },
                    onClick = {
                        menuExpanded = false
                        onPlayClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text(if (entry.inWatchlist) "Remove from Watchlist" else "Add to Watchlist", color = Color.White) },
                    onClick = {
                        menuExpanded = false
                        onToggleWatchlist()
                    }
                )
                DropdownMenuItem(
                    text = { Text(if (entry.isFavorite) "Remove from Favorites" else "Add to Favorites", color = Color.White) },
                    onClick = {
                        menuExpanded = false
                        onToggleFavorite()
                    }
                )
                customLists.forEach { cl ->
                    DropdownMenuItem(
                        text = { Text("Save to '${cl.name}'", color = SoftLavender) },
                        onClick = {
                            menuExpanded = false
                            onAssignCustomList(cl.name)
                        }
                    )
                }
                DropdownMenuItem(
                    text = { Text("Delete from Library", color = CoralWarning) },
                    onClick = {
                        menuExpanded = false
                        onRemove()
                    }
                )
            }
        }
    }
}

@Composable
private fun CreateCustomListDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GlassCardSurface,
        title = {
            Text("Create Custom List", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("List Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name, description) },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
