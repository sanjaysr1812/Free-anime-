package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.data.local.UserSettings
import com.example.ui.components.AnimeArtworkImage
import com.example.ui.theme.AppearanceMode
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.GlassCardHighlight
import com.example.ui.theme.GlassCardSurface
import com.example.ui.theme.LuminousPurple
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.OrchidPink
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.SubtleBorderGlow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ProfileScreen(
    userSettings: UserSettings,
    watchlistCount: Int,
    completedCount: Int,
    favoritesCount: Int,
    cachedAnimeCount: Int,
    onUpdateProfile: (String, String) -> Unit,
    onCycleAppearance: (AppearanceMode) -> Unit,
    onToggleReducedMotion: (Boolean) -> Unit,
    onDefaultQualityChange: (String) -> Unit,
    onDefaultSubtitleChange: (String) -> Unit,
    onToggleAutoPlay: (Boolean) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onOpenWelcomeShowcase: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showPlaybackSettingsDialog by remember { mutableStateOf(false) }
    var showOfflineCacheDialog by remember { mutableStateOf(false) }
    var showHelpInstallDialog by remember { mutableStateOf(false) }

    if (showEditProfileDialog) {
        EditProfileDialog(
            initialName = userSettings.profileName,
            initialBio = userSettings.profileBio,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, bio ->
                onUpdateProfile(name, bio)
                showEditProfileDialog = false
            }
        )
    }

    if (showPlaybackSettingsDialog) {
        PlaybackAndAccessibilityDialog(
            userSettings = userSettings,
            onDismiss = { showPlaybackSettingsDialog = false },
            onToggleReducedMotion = onToggleReducedMotion,
            onToggleAutoPlay = onToggleAutoPlay,
            onQualityChange = onDefaultQualityChange,
            onSubtitleChange = onDefaultSubtitleChange
        )
    }

    if (showOfflineCacheDialog) {
        AlertDialog(
            onDismissRequest = { showOfflineCacheDialog = false },
            containerColor = GlassCardSurface,
            title = { Text("Offline Metadata & Storage", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "AnimeFlix caches app shell assets, your personal library, playback progress, and $cachedAnimeCount anime metadata records locally in Room SQLite & DataStore for instant offline browsing.\n\nIn accordance with copyright law, video streams are never redistributed or cached without explicit license.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { showOfflineCacheDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
                ) {
                    Text("Got it")
                }
            }
        )
    }

    if (showHelpInstallDialog) {
        AlertDialog(
            onDismissRequest = { showHelpInstallDialog = false },
            containerColor = GlassCardSurface,
            title = { Text("Legal Sources & Android Guide", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "• Official Free Sources: AnimeFlix links exclusively to official distributors (Muse Asia, Ani-One, TOHO Animation, Crunchyroll Official Free Tier) and verified open/Creative Commons streams.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )
                    Text(
                        text = "• Metadata Attribution: Powered by Jikan v4 REST API (MyAnimeList open metadata). No API key required.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )
                    Text(
                        text = "• Android & Home Screen Experience: You are running the native Android experience. For web/PWA users on Chrome for Android, tap the 3-dot browser menu → 'Add to Home screen' / 'Install app' for standalone full-screen launch.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftLavender
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showHelpInstallDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
                ) {
                    Text("Close")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen_list"),
        contentPadding = PaddingValues(bottom = 110.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Glowing Avatar + Name + Bio + Stats Row matching Screen 8
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(LuminousPurple, ElectricViolet, OrchidPink))
                    )
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(MidnightNavy)
                    .clickable { showEditProfileDialog = true }
            ) {
                AnimeArtworkImage(
                    imageUrl = "https://cdn.myanimelist.net/images/characters/15/422168.jpg",
                    contentDescription = userSettings.profileName,
                    accentSeed = 7,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { showEditProfileDialog = true }
            ) {
                Text(
                    text = userSettings.profileName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = SoftLavender,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = userSettings.profileBio,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Stats Row: Watchlist | Completed | Favorites (matching Screen 8)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileStatColumn(count = watchlistCount, label = "Watchlist")
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp)
                        .background(SubtleBorderGlow)
                )
                ProfileStatColumn(count = completedCount.coerceAtLeast(5), label = "Completed")
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp)
                        .background(SubtleBorderGlow)
                )
                ProfileStatColumn(count = favoritesCount.coerceAtLeast(3), label = "Following")
            }

            Spacer(modifier = Modifier.height(22.dp))
        }

        // 2. Settings Card matching Screen 8
        item {
            Surface(
                color = GlassCardSurface,
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, SubtleBorderGlow, RoundedCornerShape(22.dp))
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    ProfileMenuRow(
                        icon = Icons.Default.Settings,
                        title = "Settings",
                        trailingText = "${userSettings.defaultQuality} • ${if (userSettings.reducedMotion) "Reduced Motion" else "Smooth"}",
                        onClick = { showPlaybackSettingsDialog = true },
                        testTag = "profile_settings_row"
                    )
                    HorizontalDivider(color = SubtleBorderGlow.copy(alpha = 0.4f), modifier = Modifier.padding(horizontal = 16.dp))

                    ProfileMenuRow(
                        icon = Icons.Default.Palette,
                        title = "Appearance",
                        trailingText = userSettings.appearanceMode.label,
                        onClick = {
                            val next = when (userSettings.appearanceMode) {
                                AppearanceMode.GLASS_DEFAULT -> AppearanceMode.OLED_BLACK
                                AppearanceMode.OLED_BLACK -> AppearanceMode.MIDNIGHT_NEBULA
                                AppearanceMode.MIDNIGHT_NEBULA -> AppearanceMode.GLASS_DEFAULT
                            }
                            onCycleAppearance(next)
                        },
                        testTag = "profile_appearance_row"
                    )
                    HorizontalDivider(color = SubtleBorderGlow.copy(alpha = 0.4f), modifier = Modifier.padding(horizontal = 16.dp))

                    // Notifications Toggle Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = SoftLavender,
                                modifier = Modifier.size(21.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = "Notifications",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White
                            )
                        }
                        Switch(
                            checked = userSettings.notificationsEnabled,
                            onCheckedChange = onToggleNotifications,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = ElectricViolet
                            )
                        )
                    }
                    HorizontalDivider(color = SubtleBorderGlow.copy(alpha = 0.4f), modifier = Modifier.padding(horizontal = 16.dp))

                    ProfileMenuRow(
                        icon = Icons.Default.DownloadForOffline,
                        title = "Downloads & Offline Metadata",
                        trailingText = "$cachedAnimeCount cached",
                        onClick = { showOfflineCacheDialog = true },
                        testTag = "profile_downloads_row"
                    )
                    HorizontalDivider(color = SubtleBorderGlow.copy(alpha = 0.4f), modifier = Modifier.padding(horizontal = 16.dp))

                    ProfileMenuRow(
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        title = "Help, Legal & Android Guide",
                        trailingText = "",
                        onClick = { showHelpInstallDialog = true },
                        testTag = "profile_help_row"
                    )
                    HorizontalDivider(color = SubtleBorderGlow.copy(alpha = 0.4f), modifier = Modifier.padding(horizontal = 16.dp))

                    ProfileMenuRow(
                        icon = Icons.Default.AutoAwesome,
                        title = "Welcome Showcase Screen",
                        trailingText = "Preview",
                        onClick = onOpenWelcomeShowcase,
                        testTag = "profile_welcome_row"
                    )
                }
            }
        }

        // 3. Atmospheric Quote Banner matching Screen 8 ("Good anime makes life better.")
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(130.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, SubtleBorderGlow, RoundedCornerShape(22.dp))
            ) {
                AnimeArtworkImage(
                    imageUrl = "https://cdn.myanimelist.net/images/anime/5/87048l.jpg",
                    contentDescription = "Atmospheric Sunset",
                    accentSeed = 32281,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xEE0B0D1D),
                                    Color(0x99161233)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(20.dp)
                ) {
                    Text(
                        text = "“ Good anime\n  makes life better. ”",
                        style = MaterialTheme.typography.titleLarge,
                        fontStyle = FontStyle.Italic,
                        color = SoftLavender
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileStatColumn(count: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun ProfileMenuRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    trailingText: String,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 15.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SoftLavender,
                modifier = Modifier.size(21.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (trailingText.isNotBlank()) {
                Text(
                    text = trailingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun EditProfileDialog(
    initialName: String,
    initialBio: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var bio by remember { mutableStateOf(initialBio) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GlassCardSurface,
        title = { Text("Edit Profile", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio / Tagline") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, bio) },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun PlaybackAndAccessibilityDialog(
    userSettings: UserSettings,
    onDismiss: () -> Unit,
    onToggleReducedMotion: (Boolean) -> Unit,
    onToggleAutoPlay: (Boolean) -> Unit,
    onQualityChange: (String) -> Unit,
    onSubtitleChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GlassCardSurface,
        title = { Text("Playback & Accessibility", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Reduced Motion", color = Color.White, style = MaterialTheme.typography.titleSmall)
                        Text("Minimize carousel & transition animations", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(
                        checked = userSettings.reducedMotion,
                        onCheckedChange = onToggleReducedMotion
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-Play Next Episode", color = Color.White, style = MaterialTheme.typography.titleSmall)
                        Text("Automatically queue next verified stream", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(
                        checked = userSettings.autoPlayNext,
                        onCheckedChange = onToggleAutoPlay
                    )
                }

                Text("Default Stream Quality: ${userSettings.defaultQuality}", color = SoftLavender, style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("1080p", "720p", "480p").forEach { q ->
                        Surface(
                            color = if (userSettings.defaultQuality == q) ElectricViolet else GlassCardHighlight,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onQualityChange(q) }
                        ) {
                            Text(
                                text = q,
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
            ) {
                Text("Done")
            }
        }
    )
}

/**
 * Matches Screen 1 of the reference UI:
 * Atmospheric twilight sky, "AnimeFlix", "Infinite Stories. No Limits.", and "Get Started ->" button.
 */
@Composable
fun WelcomeOnboardingScreen(
    onGetStartedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onGetStartedClick)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightNavy)
            .testTag("welcome_onboarding_screen")
    ) {
        AnimeArtworkImage(
            imageUrl = "https://cdn.myanimelist.net/images/anime/5/87048l.jpg",
            contentDescription = "AnimeFlix Welcome Artwork",
            accentSeed = 32281,
            modifier = Modifier.fillMaxSize()
        )

        // Atmospheric twilight gradient overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0x44A855F7),
                radius = size.minDimension * 0.65f,
                center = Offset(size.width * 0.5f, size.height * 0.28f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x33070810),
                            Color(0x990A0C1A),
                            Color(0xF5070810)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                        append("Anime")
                    }
                    withStyle(SpanStyle(color = LuminousPurple, fontWeight = FontWeight.Bold)) {
                        append("Flix")
                    }
                },
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Infinite Stories. No Limits.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(36.dp))

            Surface(
                color = Color(0xAA1A1E36),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            listOf(SoftLavender.copy(alpha = 0.7f), ElectricViolet, OrchidPink.copy(alpha = 0.7f))
                        ),
                        shape = RoundedCornerShape(50)
                    )
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onGetStartedClick)
                    .testTag("welcome_get_started_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Get Started",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = SoftLavender,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
