package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.local.LibraryEntryEntity
import com.example.data.model.AnimeItem
import com.example.ui.MainTab
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmeraldVerified
import com.example.ui.theme.GlassCardHighlight
import com.example.ui.theme.GlassCardSurface
import com.example.ui.theme.LuminousPurple
import com.example.ui.theme.OrchidPink
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.StarAmber
import com.example.ui.theme.SubtleBorderGlow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Tactile spring press-scale modifier that shrinks slightly on press and bounces back smoothly.
 */
fun Modifier.pressScaleClickable(
    pressedScale: Float = 0.95f,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "pressScale"
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = ripple(color = SoftLavender),
            onClick = onClick
        )
}

/**
 * Atmospheric ambient nebula canvas drawn behind the dark app background for luxury depth.
 */
@Composable
fun AmbientNebulaBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x287C3AED), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.12f),
                    radius = size.minDimension * 0.72f
                ),
                radius = size.minDimension * 0.72f,
                center = Offset(size.width * 0.85f, size.height * 0.12f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x1CEC4899), Color.Transparent),
                    center = Offset(size.width * 0.15f, size.height * 0.55f),
                    radius = size.minDimension * 0.65f
                ),
                radius = size.minDimension * 0.65f,
                center = Offset(size.width * 0.15f, size.height * 0.55f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x226366F1), Color.Transparent),
                    center = Offset(size.width * 0.75f, size.height * 0.88f),
                    radius = size.minDimension * 0.6f
                ),
                radius = size.minDimension * 0.6f,
                center = Offset(size.width * 0.75f, size.height * 0.88f)
            )
        }
        content()
    }
}

@Composable
fun AnimeArtworkImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    accentSeed: Int = 0,
    contentScale: ContentScale = ContentScale.Crop
) {
    val palette = listOf(
        listOf(Color(0xFF311B92), Color(0xFF6A1B9A), Color(0xFF0D0F22)),
        listOf(Color(0xFF1A237E), Color(0xFF4A148C), Color(0xFF0A0C1B)),
        listOf(Color(0xFF4A148C), Color(0xFF880E4F), Color(0xFF0F1026)),
        listOf(Color(0xFF004D40), Color(0xFF1A237E), Color(0xFF090B16))
    )
    val chosenGradient = palette[kotlin.math.abs(accentSeed) % palette.size]

    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier,
        loading = {
            ShimmerPlaceholderBox(
                modifier = Modifier.fillMaxSize(),
                gradientColors = chosenGradient
            )
        },
        error = {
            StylizedAnimeFallbackArt(
                title = contentDescription ?: "AnimeFlix",
                gradientColors = chosenGradient,
                modifier = Modifier.fillMaxSize()
            )
        }
    )
}

@Composable
fun ShimmerPlaceholderBox(
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(Color(0xFF1E223D), Color(0xFF2D325A), Color(0xFF1E223D))
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    val brush = Brush.linearGradient(
        colors = gradientColors,
        start = Offset(translateAnim - 300f, translateAnim - 300f),
        end = Offset(translateAnim, translateAnim)
    )
    Box(modifier = modifier.background(brush))
}

@Composable
fun StylizedAnimeFallbackArt(
    title: String,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(
            Brush.verticalGradient(colors = gradientColors)
        ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0x33A855F7),
                radius = size.minDimension * 0.42f,
                center = Offset(size.width * 0.75f, size.height * 0.25f)
            )
            drawCircle(
                color = Color(0x22EC4899),
                radius = size.minDimension * 0.35f,
                center = Offset(size.width * 0.25f, size.height * 0.75f)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = SoftLavender.copy(alpha = 0.85f),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.9f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionLabel: String = "View all",
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
        if (onActionClick != null) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelLarge,
                color = SoftLavender,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .pressScaleClickable(pressedScale = 0.92f, onClick = onActionClick)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun AnimePosterCard(
    anime: AnimeItem,
    isBookmarked: Boolean,
    onClick: () -> Unit,
    onBookmarkToggle: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 134.dp,
    height: Dp = 188.dp
) {
    val bookmarkScale by animateFloatAsState(
        targetValue = if (isBookmarked) 1.15f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bookmarkScale"
    )

    Column(
        modifier = modifier
            .width(width)
            .clip(RoundedCornerShape(16.dp))
            .pressScaleClickable(pressedScale = 0.95f, onClick = onClick)
            .testTag("anime_card_${anime.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            if (isBookmarked) SoftLavender.copy(alpha = 0.85f) else SubtleBorderGlow.copy(alpha = 0.7f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            AnimeArtworkImage(
                imageUrl = anime.posterUrl,
                contentDescription = anime.title,
                accentSeed = anime.id,
                modifier = Modifier.fillMaxSize()
            )

            // Bottom subtle dark gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color(0xD8080913)
                            )
                        )
                    )
            )

            // Score badge top-left
            Surface(
                color = Color(0xCC0D0F20),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = StarAmber,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = String.format("%.1f", anime.score),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Watchlist quick icon top-right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(30.dp)
                    .scale(bookmarkScale)
                    .clip(CircleShape)
                    .background(if (isBookmarked) ElectricViolet.copy(alpha = 0.85f) else Color(0xAA0D0F20))
                    .clickable(onClick = onBookmarkToggle),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Toggle Watchlist",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Free stream badge if verified
            if (anime.hasVerifiedFreeStream) {
                Surface(
                    color = EmeraldVerified.copy(alpha = 0.92f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "FREE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = anime.title,
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = if (anime.format.equals("Movie", ignoreCase = true)) {
                "Movie • ${anime.duration}"
            } else {
                "S1 • ${anime.episodesCount} eps"
            },
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            maxLines = 1
        )
    }
}

@Composable
fun ContinueWatchingCard(
    entry: LibraryEntryEntity,
    onResumeClick: () -> Unit,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rawProgress = (entry.lastPlaybackPositionMs.toFloat() / entry.lastDurationMs.coerceAtLeast(1L).toFloat())
        .coerceIn(0.08f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "continueProgress"
    )

    Surface(
        color = GlassCardSurface.copy(alpha = 0.92f),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(ElectricViolet.copy(alpha = 0.5f), SubtleBorderGlow.copy(alpha = 0.6f))
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .clip(RoundedCornerShape(18.dp))
            .pressScaleClickable(pressedScale = 0.97f, onClick = onResumeClick)
            .testTag("continue_watching_${entry.animeId}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimeArtworkImage(
                imageUrl = entry.posterUrl,
                contentDescription = entry.title,
                accentSeed = entry.animeId,
                modifier = Modifier
                    .size(width = 64.dp, height = 64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onDetailsClick)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "S1 • Ep ${entry.lastWatchedEpisode.coerceAtLeast(1)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    color = ElectricViolet,
                    trackColor = GlassCardHighlight,
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onResumeClick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(ElectricViolet.copy(alpha = 0.45f), GlassCardHighlight)
                        )
                    )
                    .border(1.dp, SoftLavender.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Resume ${entry.title}",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun FloatingGlassBottomBar(
    selectedTab: MainTab,
    onSelectTab: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color(0xF0121528),
            shape = RoundedCornerShape(30.dp),
            shadowElevation = 20.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            SubtleBorderGlow.copy(alpha = 0.85f),
                            ElectricViolet.copy(alpha = 0.55f),
                            OrchidPink.copy(alpha = 0.4f),
                            SubtleBorderGlow.copy(alpha = 0.85f)
                        )
                    ),
                    shape = RoundedCornerShape(30.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MainTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    val tint by animateColorAsState(
                        targetValue = if (isSelected) SoftLavender else TextMuted,
                        animationSpec = tween(220),
                        label = "navTint"
                    )
                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.14f else 1.0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "navIconScale"
                    )
                    val indicatorWidth by animateDpAsState(
                        targetValue = if (isSelected) 18.dp else 0.dp,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "navIndicatorWidth"
                    )

                    val icon = when (tab) {
                        MainTab.HOME -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
                        MainTab.DISCOVER -> if (isSelected) Icons.Filled.Explore else Icons.Outlined.Explore
                        MainTab.SEARCH -> if (isSelected) Icons.Filled.Search else Icons.Outlined.Search
                        MainTab.LIBRARY -> if (isSelected) Icons.Filled.VideoLibrary else Icons.Outlined.VideoLibrary
                        MainTab.PROFILE -> if (isSelected) Icons.Filled.Person else Icons.Outlined.Person
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                if (isSelected) {
                                    Brush.verticalGradient(
                                        listOf(ElectricViolet.copy(alpha = 0.25f), LuminousPurple.copy(alpha = 0.1f))
                                    )
                                } else {
                                    Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                                }
                            )
                            .pressScaleClickable(pressedScale = 0.90f) { onSelectTab(tab) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag(tab.testTag)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.label,
                            tint = tint,
                            modifier = Modifier
                                .size(22.dp)
                                .scale(iconScale)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Color.White else TextMuted,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        if (indicatorWidth > 0.dp) {
                            Box(
                                modifier = Modifier
                                    .width(indicatorWidth)
                                    .height(2.5.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.horizontalGradient(listOf(LuminousPurple, OrchidPink))
                                    )
                            )
                        } else {
                            Spacer(modifier = Modifier.height(2.5.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdaptiveSideNavigationRail(
    selectedTab: MainTab,
    onSelectTab: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        containerColor = Color(0xFF0E1020),
        contentColor = TextPrimary,
        modifier = modifier.fillMaxHeight()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        MainTab.entries.forEach { tab ->
            val isSelected = selectedTab == tab
            val icon = when (tab) {
                MainTab.HOME -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
                MainTab.DISCOVER -> if (isSelected) Icons.Filled.Explore else Icons.Outlined.Explore
                MainTab.SEARCH -> if (isSelected) Icons.Filled.Search else Icons.Outlined.Search
                MainTab.LIBRARY -> if (isSelected) Icons.Filled.VideoLibrary else Icons.Outlined.VideoLibrary
                MainTab.PROFILE -> if (isSelected) Icons.Filled.Person else Icons.Outlined.Person
            }
            NavigationRailItem(
                selected = isSelected,
                onClick = { onSelectTab(tab) },
                icon = { Icon(icon, contentDescription = tab.label) },
                label = { Text(tab.label) },
                modifier = Modifier.testTag("${tab.testTag}_rail")
            )
        }
    }
}

@Composable
fun GradientPlayButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
) {
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(50))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        LuminousPurple,
                        ElectricViolet,
                        Color(0xFF6366F1)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.32f),
                shape = RoundedCornerShape(50)
            )
            .pressScaleClickable(pressedScale = 0.94f, onClick = onClick)
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
