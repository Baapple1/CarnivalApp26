package org.bridgwatercarnival.companion.pages

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.bridgwatercarnival.companion.theme.bungeeFont
import org.bridgwatercarnival.companion.util.TranslationManager
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Gallery2025(navController: NavHostController) {
    val scrollState = rememberScrollState()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                style = MaterialTheme.typography.h4,
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
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Animated carnival elements
            CarnivalAnimation()

            Spacer(modifier = Modifier.height(32.dp))

            // Coming Soon text with glowing effect
            val glowIntensity by rememberInfiniteTransition().animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Text(
                text = TranslationManager.translate("coming_2025"),
                style = MaterialTheme.typography.h5,
                fontFamily = bungeeFont,
                color = MaterialTheme.colors.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .shadow(
                        elevation = 8.dp * glowIntensity,
                        shape = RoundedCornerShape(16.dp)
                    )
            )

            Text(
                text = TranslationManager.translate("gallery_2025_description"),
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CarnivalAnimation() {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing)
        )
    )

    Box(
        modifier = Modifier
            .size(200.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Rotating carnival elements
        val elements = listOf("🎡", "🎪", "🎭", "✨", "🎉", "🎨", "🎠", "🌟")
        elements.forEachIndexed { index, emoji ->
            val elementRotation by infiniteTransition.animateFloat(
                initialValue = (360f / elements.size) * index,
                targetValue = (360f / elements.size) * index + 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(20000, easing = LinearEasing)
                )
            )
            
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1000,
                        delayMillis = (index * 200),
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                )
            )

            val radius = 80f
            val x = radius * cos(elementRotation * PI.toFloat() / 180f)
            val y = radius * sin(elementRotation * PI.toFloat() / 180f)

            Text(
                text = emoji,
                style = MaterialTheme.typography.h5,
                modifier = Modifier
                    .offset(x = x.dp, y = y.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        rotationZ = -elementRotation // Keep emojis upright
                    }
            )
        }

        // Center element with reverse rotation
        val centerScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Text(
            text = "2025",
            style = MaterialTheme.typography.h4.copy(
                fontFamily = bungeeFont
            ),
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .graphicsLayer {
                    scaleX = centerScale
                    scaleY = centerScale
                    rotationZ = -rotation
                }
        )
    }
} 