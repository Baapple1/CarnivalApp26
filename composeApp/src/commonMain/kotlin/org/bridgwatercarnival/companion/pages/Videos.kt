package org.bridgwatercarnival.companion.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bridgwatercarnival.companion.components.VideoPlayerComponent
import org.bridgwatercarnival.companion.theme.bungeeFont
import org.bridgwatercarnival.companion.util.TranslationManager
import org.bridgwatercarnival.companion.util.VideoManager
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import bridgwatercarnival.composeapp.generated.resources.Res
import bridgwatercarnival.composeapp.generated.resources.shuffle

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Videos() {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val currentVideoId by VideoManager.currentVideoId.collectAsState()
    
    var showFullscreen by remember { mutableStateOf(false) }
    
    // Video information map
    val videoInfoMap = remember {
        mapOf(
            "EhpI5nVkWu0" to VideoInfo(
                title = "Bridgwater Carnival Highlights 2023",
                description = "Experience the vibrant atmosphere and spectacular floats of Bridgwater Carnival 2023."
            ),
            "JxSGL7wy3Gs" to VideoInfo(
                title = "Bridgwater Squibbing 2023",
                description = "Watch the traditional squibbing ceremony that illuminates the night sky during the carnival celebrations."
            ),
            "TO7jGuIctzk" to VideoInfo(
                title = "Carnival Procession 2023",
                description = "Enjoy the entire procession of the famous Bridgwater Carnival from start to finish."
            )
        )
    }
    
    // Get current video info
    val currentVideoInfo = videoInfoMap[currentVideoId] ?: VideoInfo(
        title = "Carnival Video",
        description = "Watch highlights from the Bridgwater Carnival."
    )
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Regular view
        if (!showFullscreen) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Page title
                Text(
                    text = "Carnival Videos 🎭",
                    style = MaterialTheme.typography.h4.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = bungeeFont,
                        color = ThemeColors.entertainmentColor
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Featured video player
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = if (MaterialTheme.colors.isLight) 
                        Color.White 
                    else 
                        Color(0xFF2D2D2D)
                ) {
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        VideoPlayerComponent(
                            modifier = Modifier.fillMaxWidth(),
                            showControls = true
                        )
                        
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = currentVideoInfo.title,
                                style = MaterialTheme.typography.h6.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            Text(
                                text = currentVideoInfo.description,
                                style = MaterialTheme.typography.body1
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = { 
                                        coroutineScope.launch {
                                            VideoManager.shuffleVideo()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = ThemeColors.entertainmentColor
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = "Shuffle Videos",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Shuffle",
                                        color = Color.White
                                    )
                                }
                                
                                OutlinedButton(
                                    onClick = { showFullscreen = true }
                                ) {
                                    Text("Fullscreen")
                                }
                            }
                        }
                    }
                }
                
                // Video list title
                Text(
                    text = "Available Videos",
                    style = MaterialTheme.typography.h6.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                // Video list
                videoInfoMap.forEach { (videoId, info) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                coroutineScope.launch {
                                    if (currentVideoId != videoId) {
                                        // Set this video as current
                                        VideoManager.shuffleVideo()
                                    }
                                }
                            },
                        elevation = 2.dp,
                        border = if (currentVideoId == videoId) 
                            BorderStroke(2.dp, ThemeColors.entertainmentColor)
                        else 
                            null
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
                                    text = info.title,
                                    style = MaterialTheme.typography.body1.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = info.description,
                                    style = MaterialTheme.typography.caption,
                                    maxLines = 2
                                )
                            }
                            
                            if (currentVideoId == videoId) {
                                Card(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(ThemeColors.entertainmentColor, RoundedCornerShape(4.dp))
                                ) {}
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        } else {
            // Fullscreen view
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                VideoPlayerComponent(
                    modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                    showControls = true
                )
                
                IconButton(
                    onClick = { showFullscreen = false },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                        .background(
                            color = ThemeColors.entertainmentColor.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Exit Fullscreen",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

// Helper data class for video information
data class VideoInfo(
    val title: String,
    val description: String
) 