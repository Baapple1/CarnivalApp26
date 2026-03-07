package org.bridgwatercarnival.companion.pages

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import org.bridgwatercarnival.companion.database.FirestoreService
import org.bridgwatercarnival.companion.service.CarnivalClubService
import org.bridgwatercarnival.companion.theme.bungeeFont
import org.bridgwatercarnival.companion.util.TimeWindow
import org.bridgwatercarnival.companion.util.TranslationManager
import kotlin.math.*
import kotlin.random.Random

data class ClubResult(
    val position: Int,
    val clubName: String,
    val votes: Int
)

@Composable
fun LoadingAnimation() {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        )
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        // Rotating dots
        for (i in 0..7) {
            val dotRotation = rotation + (i * 45f)
            val delay = i * 100
            val dotScale by animateFloatAsState(
                targetValue = scale,
                animationSpec = infiniteRepeatable(
                    animation = tween(500, delayMillis = delay),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .offset(
                        x = (cos(dotRotation * PI.toFloat() / 180f) * 40).dp,
                        y = (sin(dotRotation * PI.toFloat() / 180f) * 40).dp
                    )
                    .scale(dotScale)
                    .background(
                        color = MaterialTheme.colors.primary,
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }

        Text(
            text = "🎭",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.scale(scale)
        )
    }
}

@Composable
fun Confetti() {
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(10000) // 10 seconds
        isVisible = false
    }

    if (!isVisible) return

    val particles = remember { //confetti animation
        List(100) {
            ConfettiParticle(
                color = listOf(
                    Color(0xFFFF1744), // Red
                    Color(0xFF2196F3), // Blue
                    Color(0xFFFFEB3B), // Yellow
                    Color(0xFF4CAF50), // Green
                    Color(0xFFFF9800), // Orange
                    Color(0xFF9C27B0)  // Purple
                ).random(),
                initialX = Random.nextFloat() * 1000,
                initialY = -Random.nextFloat() * 500
            )
        }
    }

    particles.forEach { particle ->
        val infiniteTransition = rememberInfiniteTransition()
        val xOffset by infiniteTransition.animateFloat(
            initialValue = particle.initialX,
            targetValue = particle.initialX + Random.nextFloat() * 300 - 150,
            animationSpec = infiniteRepeatable(
                animation = tween(2000 + Random.nextInt(1000)),
                repeatMode = RepeatMode.Reverse
            )
        )
        val yOffset by infiniteTransition.animateFloat(
            initialValue = particle.initialY,
            targetValue = 1200f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000 + Random.nextInt(2000)),
                repeatMode = RepeatMode.Restart
            )
        )
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000 + Random.nextInt(1000))
            )
        )
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse
            )
        )

        Canvas(
            modifier = Modifier
                .size(12.dp)
                .offset(x = xOffset.dp, y = yOffset.dp)
                .scale(scale)
                .rotate(rotation)
        ) {
            when (Random.nextInt(3)) {
                0 -> drawCircle(particle.color)
                1 -> drawRect(particle.color)
                2 -> {
                    val path = Path().apply {
                        moveTo(size.width / 2f, 0f)
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                        close()
                    }
                    drawPath(path, particle.color)
                }
            }
        }
    }
}

private data class ConfettiParticle(
    val color: Color,
    val initialX: Float,
    val initialY: Float
)

@Composable
fun CountdownTimer(onComplete: () -> Unit) {
    var countdown by remember { mutableStateOf(3) }

    // Static vibrant color
    val countdownColor = Color.Magenta

    // Scale animation
    val scale by animateFloatAsState(
        targetValue = if (countdown == 0) 0f else 1f,
        animationSpec = tween(
            durationMillis = 500,
            easing = EaseInCubic
        )
    )

    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(500)
            countdown--
        }
        onComplete()
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (countdown > 0) {
            Text(
                text = countdown.toString(),
                style = MaterialTheme.typography.h1.copy(
                    fontFamily = bungeeFont,
                    fontWeight = FontWeight.ExtraBold,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.25f),
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                ),
                color = countdownColor,
                modifier = Modifier.scale(scale)
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.scale(scale)
            ) {
                listOf("🎉", "🎭", "🎪").forEach { emoji ->
                    Text(
                        text = emoji,
                        style = MaterialTheme.typography.h2
                    )
                }
            }
        }
    }
}

@Composable
fun WinnerCongratulations(winner: String) {
    var visible by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition()

    // Gentle scale animation for celebratory effect
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically() + scaleIn()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Trophy emoji
            Text(
                text = "🏆🥇",
                style = MaterialTheme.typography.h3,
                modifier = Modifier
                    .scale(scale)
                    .padding(bottom = 8.dp)
            )

            // Congratulations text
            Text(
                text = "Congratulations to",
                style = MaterialTheme.typography.h5.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = bungeeFont,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.25f),
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .scale(scale)
                    .padding(bottom = 4.dp),
                color = Color.Magenta
            )

            // Winner name
            Text(
                text = winner,
                style = MaterialTheme.typography.h5.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = bungeeFont,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.15f),
                        offset = Offset(1f, 1f),
                        blurRadius = 2f
                    )
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp),
                color = MaterialTheme.colors.onBackground
            )
        }
    }
}

@Composable
fun Results(onNavigateBack: () -> Unit) {
    var results by remember { mutableStateOf<List<ClubResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isResultsVisible by remember { mutableStateOf(false) }
    var timeUntilResults by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var revealedPositions by remember { mutableStateOf(0) }
    var showWinnerCongrats by remember { mutableStateOf(false) }
    var showingInitialCountdown by remember { mutableStateOf(true) }
    val firestoreService = remember { FirestoreService() }

    // Initial check
    LaunchedEffect(Unit) {
        try {
            isResultsVisible = TimeWindow.areResultsOpen()
            timeUntilResults = TimeWindow.getTimeUntilResults()

            if (isResultsVisible) {
                // Fetch vote counts for all clubs
                val clubVotes = CarnivalClubService.clubs.map { club ->
                    val voteCount = firestoreService.getVoteCount(club.id)
                    ClubResult(0, club.name, voteCount)
                }

                results = clubVotes
                    .sortedByDescending { it.votes }
                    .mapIndexed { index, result ->
                        result.copy(position = index + 1)
                    }
                    .take(5)
            }
        } catch (e: Exception) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    // Update countdown every second
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000) // Wait for 1 second
            if (!isResultsVisible) {
                timeUntilResults = TimeWindow.getTimeUntilResults()
                isResultsVisible = TimeWindow.areResultsOpen()
            }
        }
    }

    LaunchedEffect(results.isNotEmpty()) {
        if (results.isNotEmpty()) {
            // Initial countdown
            delay(2000) // Give time for countdown to complete
            showingInitialCountdown = false

            // Reveal results one by one
            repeat(5) { index ->
                delay(1000) // Delay between each reveal
                revealedPositions++
                if (revealedPositions == 5) {
                    delay(250)
                    showWinnerCongrats = true
                }
            }
        }
    }

    if (!isResultsVisible) {
        AlertDialog(
            onDismissRequest = onNavigateBack,
            title = {
                Text(
                    text = TranslationManager.translate("results_suspense_title"),
                    style = MaterialTheme.typography.h5.copy(
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        fontFamily = bungeeFont
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    // Star icon at the top
                    Text(
                        text = "⭐",
                        fontSize = 32.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Votes being counted message
                    Text(
                        text = TranslationManager.translate("results_counting_votes"),
                        style = MaterialTheme.typography.body1.copy(
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colors.onSurface
                    )

                    // Time until reveal
                    Text(
                        text = TranslationManager.translate("results_time_until").replace("{time}", timeUntilResults),
                        style = MaterialTheme.typography.body1.copy(
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colors.primary
                    )

                    // Join us message
                    Text(
                        text = TranslationManager.translate("results_join_after"),
                        style = MaterialTheme.typography.body1.copy(
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
                    )

                    // Star icon at the bottom
                    Text(
                        text = "✨",
                        fontSize = 32.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = TranslationManager.translate("results_ill_check_later"),
                        color = Color.White,
                        style = MaterialTheme.typography.button.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            },
            backgroundColor = MaterialTheme.colors.surface,
            shape = RoundedCornerShape(16.dp),
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(MaterialTheme.colors.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = TranslationManager.translate("results_title"),
                style = MaterialTheme.typography.h4.copy(
                    color = ThemeColors.votingColor,
                    fontFamily = bungeeFont,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
            )

            when {
                isLoading -> {
                    LoadingAnimation()
                }
                error != null -> {
                    Text(
                        text = "Error: $error",
                        color = MaterialTheme.colors.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                !isResultsVisible -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .shadow(8.dp, RoundedCornerShape(8.dp)),
                        elevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = TranslationManager.translate("results_not_available"),
                                style = MaterialTheme.typography.h6,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = TranslationManager.translate("results_check_back"),
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                    }
                }
                else -> {
                    if (showingInitialCountdown) {
                        CountdownTimer {
                            showingInitialCountdown = false
                        }
                    } else {
                        results.filter { it.position > 5 - revealedPositions }
                            .sortedBy { it.position }
                            .forEach { result ->
                                if (result.position == 1) {
                                    AnimatedVisibility(
                                        visible = true,
                                        enter = slideInHorizontally(
                                            initialOffsetX = { fullWidth -> fullWidth },
                                            animationSpec = tween(
                                                durationMillis = 500,
                                                easing = FastOutSlowInEasing
                                            )
                                        ) + fadeIn(
                                            animationSpec = tween(500)
                                        )
                                    ) {
                                        ResultCard(result = result, position = result.position)
                                    }
                                } else {
                                    ResultCard(result = result, position = result.position)
                                }
                            }

                        if (showWinnerCongrats && results.isNotEmpty()) {
                            WinnerCongratulations(results.first().clubName)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        if (showWinnerCongrats) {
            Confetti()
        }
    }
}

@Composable
private fun ResultCard(result: ClubResult, position: Int) {
    var shouldPop by remember { mutableStateOf(true) }
    val popScale by animateFloatAsState(
        targetValue = if (shouldPop) 1.2f else 1f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        )
    )

    LaunchedEffect(Unit) {
        delay(100)
        shouldPop = false
    }

    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (position == 1) 1.05f else 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .scale(popScale * pulseScale),
        elevation = if (position == 1) 12.dp else 8.dp,
        backgroundColor = getPastelPositionColor(position),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = getPositionText(position),
                    style = MaterialTheme.typography.h6.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontFamily = bungeeFont
                    ),
                    color = Color.Black,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = result.clubName,
                    style = MaterialTheme.typography.body1.copy(
                        fontSize = 12.sp,
                        fontFamily = bungeeFont
                    ),
                    color = Color.Black,
                    maxLines = 1
                )
            }
            Text(
                text = TranslationManager.translate("results_votes").replace("{count}", result.votes.toString()),
                style = MaterialTheme.typography.body2.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = bungeeFont
                ),
                color = Color.Black,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun getPositionText(position: Int): String {
    return when (position) {
        1 -> TranslationManager.translate("results_first_place")
        2 -> TranslationManager.translate("results_second_place")
        3 -> TranslationManager.translate("results_third_place")
        4 -> TranslationManager.translate("results_fourth_place")
        else -> TranslationManager.translate("results_fifth_place")
    }
}

@Composable
private fun getPastelPositionColor(position: Int): Color {
    return when (position) {
        1 -> Color(0xFFFFD700) // Bright gold
        2 -> Color(0xFF00E676) // Vibrant green
        3 -> Color(0xFF40C4FF) // Bright blue
        4 -> Color(0xFFFF9100) // Bright orange
        else -> Color(0xFFFF4081) // Bright pink
    }
}

@Composable
fun TrophyRoom(
    results: List<ClubResult>,
    onViewMore: () -> Unit
) {
    var selectedTrophy by remember { mutableStateOf<ClubResult?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = TranslationManager.translate("results_title"),
            style = MaterialTheme.typography.h4.copy(
                fontFamily = bungeeFont,
                textAlign = TextAlign.Center
            ),
            color = MaterialTheme.colors.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )


    }
} 