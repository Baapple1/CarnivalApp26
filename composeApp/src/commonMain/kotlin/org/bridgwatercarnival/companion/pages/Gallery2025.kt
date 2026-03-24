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
import org.bridgwatercarnival.companion.util.TranslationManager
import kotlin.random.Random

// Gallery2026.kt - Displays the 2026 carnival gallery with accessibility support
// This composable provides a scrollable gallery of carnival clubs and their entries
// with full screen reader and voice control support

@Composable
fun Gallery2025(navController: NavHostController) {
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
                    contentDescription =
                        "${TranslationManager.translate(title)}. ${TranslationManager.translate(text)} ${
                            TranslationManager.translate("double_tap_view_photo")
                        }"
                }
                .clickable(
                    onClickLabel = TranslationManager.translate("view_full_size_photo"),
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
                        contentDescription = TranslationManager.translate(title)
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
                    contentDescription =
                        TranslationManager.translate("fullscreen_photo_instructions")
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
                        contentDescription = TranslationManager.translate("close_fullscreen")
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
                        contentDescription =
                            TranslationManager.translate("gallery_2025_description")
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

                Text(
                    text = TranslationManager.translate("gallery_2025_title"),
                    style = MaterialTheme.typography.h5,
                    fontFamily = bungeeFont,
                    color = MaterialTheme.colors.primary,
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
                            contentDescription = TranslationManager.translate("gallery_2025_title")
                        }
                )

                // List of carnival clubs with their entries
                val contentList = listOf(
                    Triple(
                        TranslationManager.translate("gremlins_title"),
                        TranslationManager.translate("gremlins_description"),
                        Res.drawable.Gremlins25
                    ),
                    Triple(
                        TranslationManager.translate("ramblers_title"),
                        TranslationManager.translate("ramblers_description"),
                        Res.drawable.Ramblers25
                    ),
                    Triple(
                        TranslationManager.translate("renegades_title"),
                        TranslationManager.translate("renegades_description"),
                        Res.drawable.Renegades25
                    ),
                    Triple(
                        TranslationManager.translate("marketeers_title"),
                        TranslationManager.translate("marketeers_description"),
                        Res.drawable.Marketeers25
                    ),
                    Triple(
                        TranslationManager.translate("lime_kiln_title"),
                        TranslationManager.translate("lime_kiln_description"),
                        Res.drawable.LimeKiln25
                    ),
                    Triple(
                        TranslationManager.translate("vagabonds_title"),
                        TranslationManager.translate("vagabonds_description"),
                        Res.drawable.Vagabonds25
                    ),
                    Triple(
                        TranslationManager.translate("griffens_title"),
                        TranslationManager.translate("griffens_description"),
                        Res.drawable.Griffens25
                    ),
                    Triple(
                        TranslationManager.translate("crusaders_title"),
                        TranslationManager.translate("crusaders_description"),
                        Res.drawable.Crusaders25
                    ),
                    Triple(
                        TranslationManager.translate("new_market_title"),
                        TranslationManager.translate("new_market_description"),
                        Res.drawable.Newmarket25
                    ),
                    Triple(
                        TranslationManager.translate("marina_sydenham_title"),
                        TranslationManager.translate("marina_sydenham_description"),
                        Res.drawable.MarinaSyden25
                    ),

                    Triple(
                        TranslationManager.translate("wilfs_title"),
                        TranslationManager.translate("wilfs_description"),
                        Res.drawable.Wilfs25
                    ),

                    Triple(
                        TranslationManager.translate("wills_title"),
                        TranslationManager.translate("wills_description"),
                        Res.drawable.Wills25
                    )
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

