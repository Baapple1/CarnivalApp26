package org.bridgwatercarnival.companion.pages

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import bridgwatercarnival.composeapp.generated.resources.*
import org.bridgwatercarnival.companion.theme.bungeeFont
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import androidx.compose.animation.core.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import org.bridgwatercarnival.companion.util.TranslatedText
import kotlin.random.Random

// Gallery2023.kt - Displays the 2023 carnival gallery with accessibility support
// This composable provides a scrollable gallery of carnival clubs and their entries
// with full screen reader and voice control support

@Composable
fun Gallery2023(navController: NavHostController) {
    val scrollState = rememberScrollState()
    val imageSize = 350.dp
    // State for handling expanded image view with accessibility
    var expandedImage by remember { mutableStateOf<DrawableResource?>(null) }
    var zoomState by remember { mutableStateOf(1f) }

    // ContentBox: A reusable component that displays a carnival club's information
    // with proper semantic structure for screen readers
    @Composable
    fun ContentBox(title: String, text: String, image: DrawableResource) {
        // Animation states for visual feedback
        var isHovered by remember { mutableStateOf(false) }
        val scale by animateFloatAsState(
            targetValue = if (isHovered) 1.05f else 1f,
            animationSpec = tween(200)
        )
        val elevation by animateFloatAsState(
            targetValue = if (isHovered) 12f else 4f,
            animationSpec = tween(200)
        )
        // Continuous rotation animation for hover effect
        val rotation by rememberInfiniteTransition().animateFloat(
            initialValue = -1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        // Main card container with accessibility description
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp)
                .shadow(elevation.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .graphicsLayer {
                    this.scaleX = scale
                    this.scaleY = scale
                    if (isHovered) {
                        this.rotationZ = rotation
                    }
                }
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            when (event.type) {
                                PointerEventType.Enter -> isHovered = true
                                PointerEventType.Exit -> isHovered = false
                            }
                        }
                    }
                }
                .semantics {
                    contentDescription = "$title. $text Double tap to view full size photo."
                }
                .clickable(
                    onClickLabel = "View $title full size photo",
                    onClick = { expandedImage = image }
                ),
            backgroundColor = getRandomPastelColor()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TranslatedText(
                    key = title,
                    style = MaterialTheme.typography.h6.copy(
                        fontFamily = bungeeFont,
                        color = Color.Black
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.semantics { 
                        heading()
                        contentDescription = title
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                TranslatedText(
                    key = text,
                    style = MaterialTheme.typography.body1.copy(
                        color = Color.Black
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(image),
                    contentDescription = null, // Remove separate image description as it's part of the card
                    modifier = Modifier
                        .width(imageSize)
                        .clip(RoundedCornerShape(12.dp))
                        .shadow(8.dp)
                        .graphicsLayer {
                            if (isHovered) {
                                this.rotationZ = rotation * 0.5f
                            }
                        }
                )
            }
        }
    }

    // Expanded image view with accessibility instructions
    if (expandedImage != null) {
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        // Full screen overlay with gesture support
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .semantics(mergeDescendants = true) {
                    contentDescription = "Full screen photo view. You can zoom in by pinching with two fingers, and move around by dragging with three fingers. There's a close button at the top right - double tap it to go back to the gallery."
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        zoomState = (zoomState * zoom).coerceIn(0.5f, 3f)
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(expandedImage!!),
                contentDescription = null, // Remove duplicate description
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = zoomState,
                        scaleY = zoomState,
                        translationX = offsetX,
                        translationY = offsetY
                    ),
                contentScale = ContentScale.Fit
            )

            // Close button with accessibility label
            IconButton(
                onClick = {
                    expandedImage = null
                    zoomState = 1f
                    offsetX = 0f
                    offsetY = 0f
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .semantics { 
                        contentDescription = "Close full screen view"
                    },
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    } else {
        // Main gallery view with semantic structure
        Scaffold(
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState)
                    .semantics {
                        contentDescription = "2023 Carnival Gallery. Showing photos from all carnival clubs. Swipe up or down to browse."
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated title with accessibility support
                val titleScale by rememberInfiniteTransition().animateFloat(
                    initialValue = 1f,
                    targetValue = 1.05f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                TranslatedText(
                    key = "gallery_2023_title",
                    style = MaterialTheme.typography.h5.copy(
                        fontFamily = bungeeFont,
                        color = MaterialTheme.colors.primary
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .graphicsLayer {
                            scaleX = titleScale
                            scaleY = titleScale
                        }
                        .semantics { 
                            heading()
                            contentDescription = "2023 Carnival Gallery"
                        }
                )

                // List of carnival clubs with their entries
                val contentList = listOf(
                    Triple("ramblers_2023_title", "ramblers_2023_description", Res.drawable.Ramblers),
                    Triple("gremlins_2023_title", "gremlins_2023_description", Res.drawable.Gremlins),
                    Triple("marketeers_2023_title", "marketeers_2023_description", Res.drawable.Marketeers),
                    Triple("british_flag_2023_title", "british_flag_2023_description", Res.drawable.BritishFlag),
                    Triple("cavaliers_2023_title", "cavaliers_2023_description", Res.drawable.Cavaliers),
                    Triple("centurions_2023_title", "centurions_2023_description", Res.drawable.Centurion),
                    Triple("crusaders_2023_title", "crusaders_2023_description", Res.drawable.Crusaders),
                    Triple("griffens_2023_title", "griffens_2023_description", Res.drawable.Griffens),
                    Triple("lime_kiln_2023_title", "lime_kiln_2023_description", Res.drawable.LimeKiln),
                    Triple("newmarket_2023_title", "newmarket_2023_description", Res.drawable.Newmarket),
                    Triple("marina_sydenham_2023_title", "marina_sydenham_2023_description", Res.drawable.MarinaSydenham),
                    Triple("pentathlon_2023_title", "pentathlon_2023_description", Res.drawable.Pentathlon),
                    Triple("renegades_2023_title", "renegades_2023_description", Res.drawable.Renegades),
                    Triple("vagabonds_2023_title", "vagabonds_2023_description", Res.drawable.Vagabonds),
                    Triple("guy_fawkes_2023_title", "guy_fawkes_2023_description", Res.drawable.GuyFawkesCart)
                )
                // Create accessible cards for each carnival club
                contentList.forEach { (title, text, image) ->
                    ContentBox(title, text, image)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// Helper function to generate pastel background colors
private fun getRandomPastelColor(): Color {
    val pastelColors = listOf(
        Color(0xFFFFF9C4), // Pastel Yellow
        Color(0xFFE1F5FE), // Pastel Blue
        Color(0xFFF8BBD0), // Pastel Pink
        Color(0xFFDCEDC8), // Pastel Green
        Color(0xFFFFE0B2), // Pastel Orange
        Color(0xFFE1BEE7)  // Pastel Purple
    )
    return pastelColors[Random.nextInt(pastelColors.size)]
}