package org.bridgwatercarnival.companion.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bridgwatercarnival.companion.pages.ThemeColors
import org.bridgwatercarnival.companion.util.VideoManager

@Composable
expect fun PlatformVideoPlayer(
    videoId: String,
    modifier: Modifier = Modifier,
    onLoadingStateChanged: (Boolean) -> Unit = {}
)

@Composable
fun VideoPlayerComponent(
    modifier: Modifier = Modifier,
    autoAdvance: Boolean = true,
    shuffleIntervalSeconds: Int = 120, // 2 minutes
    showControls: Boolean = true,
    initialVideoId: String? = null
) {
    val coroutineScope = rememberCoroutineScope()
    var currentVideoId by remember { mutableStateOf(initialVideoId ?: VideoManager.currentVideoId.value) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Use the specified video ID if provided, otherwise collect from flow
    val videoIdFromFlow by VideoManager.currentVideoId.collectAsState()
    
    // Update currentVideoId when videoIdFromFlow changes, but only if initialVideoId is null
    LaunchedEffect(videoIdFromFlow) {
        if (initialVideoId == null) {
            currentVideoId = videoIdFromFlow
        }
    }
    
    // Auto advance effect
    LaunchedEffect(autoAdvance) {
        if (autoAdvance && initialVideoId == null) {
            while (true) {
                delay(shuffleIntervalSeconds * 1000L)
                VideoManager.nextVideo()
            }
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, ThemeColors.entertainmentColor, RoundedCornerShape(16.dp))
    ) {
        // Video player
        PlatformVideoPlayer(
            videoId = currentVideoId,
            modifier = Modifier.fillMaxWidth().aspectRatio(16f/9f),
            onLoadingStateChanged = { isLoading = it }
        )
        
        // Loading indicator
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f/9f)
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = ThemeColors.entertainmentColor
                )
            }
        }
        
        // Controls - only show if initialVideoId is null and showControls is true
        if (showControls && initialVideoId == null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = { 
                        coroutineScope.launch {
                            VideoManager.nextVideo()
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = ThemeColors.entertainmentColor.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Next Video",
                        tint = Color.White
                    )
                }
            }
        }
    }
} 