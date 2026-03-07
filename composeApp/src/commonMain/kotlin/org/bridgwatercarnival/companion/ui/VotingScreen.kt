package org.bridgwatercarnival.companion.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bridgwatercarnival.companion.model.CarnivalClub
import org.bridgwatercarnival.companion.pages.ThemeColors
import org.bridgwatercarnival.companion.service.CarnivalClubService
import org.bridgwatercarnival.companion.theme.bungeeFont
import org.bridgwatercarnival.companion.util.TimeWindow
import org.bridgwatercarnival.companion.util.TranslationManager
import org.bridgwatercarnival.companion.util.getDeviceId
import org.bridgwatercarnival.companion.viewmodel.VotingViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VotingScreen(
    viewModel: VotingViewModel,
    onClose: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var showAlreadyVotedDialog by remember { mutableStateOf(false) }
    var showNotAvailableDialog by remember { mutableStateOf(false) }
    var showVotingEndedDialog by remember { mutableStateOf(false) }
    var selectedClub by remember { mutableStateOf<CarnivalClub?>(null) }
    val scope = rememberCoroutineScope()
    val deviceId = remember {
        val id = getDeviceId()
        println("Generated Device ID: $id")
        id
    }

    val votingState by viewModel.votingState.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }
    var isVotingOpen by remember { mutableStateOf(false) }
    var hasVotingEnded by remember { mutableStateOf(false) }
    var timeUntilVoting by remember { mutableStateOf("") }

    // Initial check
    LaunchedEffect(Unit) {
        isVotingOpen = TimeWindow.isVotingOpen()
        timeUntilVoting = TimeWindow.getTimeUntilVoting()
        hasVotingEnded = TimeWindow.hasVotingEnded()
    }

    // Update countdown every second
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000) // Wait for 1 second
            if (!isVotingOpen && !hasVotingEnded) {
                timeUntilVoting = TimeWindow.getTimeUntilVoting()
                isVotingOpen = TimeWindow.isVotingOpen()
                hasVotingEnded = TimeWindow.hasVotingEnded()
            }
        }
    }

    LaunchedEffect(votingState) {
        when (votingState) {
            is VotingViewModel.VotingState.Success -> {
                showSuccessDialog = true
            }
            is VotingViewModel.VotingState.AlreadyVoted -> {
                showAlreadyVotedDialog = true
            }
            else -> {}
        }
    }

    // Animation states
    val buttonScale by animateFloatAsState(
        targetValue = if (selectedClub != null) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    if (hasVotingEnded) {
        LockedScreen(
            message = "Voting has ended for this year's carnival. Check back next year!",
            isDarkMode = isSystemInDarkTheme(),
            onClose = onClose
        )
        return
    }

    if (!isVotingOpen) {
        LockedScreen(
            message = "Voting opens at 10:00 PM on November 1st!\n\nTime until voting: $timeUntilVoting",
            isDarkMode = isSystemInDarkTheme(),
            onClose = onClose
        )
        return
    }

    if (votingState == VotingViewModel.VotingState.Loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                CircularProgressIndicator(
                    color = ThemeColors.votingColor,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    TranslationManager.translate("voting_recording"),
                    style = MaterialTheme.typography.h6.copy(
                        color = ThemeColors.votingColor
                    )
                )
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .background(if (MaterialTheme.colors.isLight) Color(0xFFF8F9FA) else MaterialTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.15f))

        // TEST BUTTON - Comment out this section to disable bulk voting test
//        Button(
//            onClick = {
//                scope.launch {
//                    viewModel.simulateBulkVoting(
//                        numVotes = 2000,
//                        clubs = CarnivalClubService.clubs,
//                        progressCallback = { current, total ->
//                            println("Progress: $current/$total votes submitted")
//                        }
//                    )
//                }
//            },
//            colors = ButtonDefaults.buttonColors(
//                backgroundColor = Color.Red
//            ),
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            Text(
//                "Test: Submit 2000 Votes",
//                color = Color.White,
//                style = MaterialTheme.typography.button.copy(
//                    fontWeight = FontWeight.Bold
//                )
//            )
//        }


        // Animated title
        val titleScale by rememberInfiniteTransition().animateFloat(
            initialValue = 1f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Text(
            text = TranslationManager.translate("voting_favorite"),
            style = MaterialTheme.typography.h4.copy(
                color = ThemeColors.votingColor,
                fontFamily = bungeeFont,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = titleScale
                    scaleY = titleScale
                }
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            backgroundColor = if (MaterialTheme.colors.isLight)
                Color(0xFFFFF3E0)
            else
                Color(0xFF2C2C2C),
            shape = RoundedCornerShape(16.dp),
            elevation = 2.dp,
            border = BorderStroke(
                1.dp,
                if (MaterialTheme.colors.isLight)
                    Color(0xFFFFB74D)
                else
                    Color(0xFF2C2C2C)
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = TranslationManager.translate("voting_be_part"),
                    style = MaterialTheme.typography.subtitle1.copy(
                        color = if (MaterialTheme.colors.isLight)
                            Color(0xFFE65100)
                        else
                            Color(0xFFFFB74D),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = TranslationManager.translate("voting_helps_tradition"),
                    style = MaterialTheme.typography.body2.copy(
                        color = if (MaterialTheme.colors.isLight)
                            Color(0xFF424242)
                        else
                            Color(0xFFE0E0E0),
                        textAlign = TextAlign.Center
                    )
                )
            }
        }

        var showConfirmDialog by remember { mutableStateOf(false) }
        var selectedClub by remember { mutableStateOf<CarnivalClub?>(null) }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(CarnivalClubService.clubs) { club ->
                var cardScale by remember { mutableStateOf(1f) }
                val animatedScale by animateFloatAsState(
                    targetValue = cardScale,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .graphicsLayer {
                            scaleX = animatedScale
                            scaleY = animatedScale
                        }
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    when (event.type) {
                                        PointerEventType.Enter -> cardScale = 1.05f
                                        PointerEventType.Exit -> cardScale = 1f
                                    }
                                }
                            }
                        }
                        .clickable {
                            selectedClub = club
                            showConfirmDialog = true
                        },
                    backgroundColor = if (MaterialTheme.colors.isLight)
                        Color.White
                    else
                        ThemeColors.votingColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp),
                    elevation = if (MaterialTheme.colors.isLight) 2.dp else 4.dp,
                    border = BorderStroke(
                        if (MaterialTheme.colors.isLight) 1.dp else 2.dp,
                        if (MaterialTheme.colors.isLight)
                            ThemeColors.votingColor.copy(alpha = 0.3f)
                        else
                            ThemeColors.votingColor.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = club.name,
                                style = MaterialTheme.typography.h6.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (MaterialTheme.colors.isLight)
                                        Color(0xFF2C3E50)
                                    else
                                        MaterialTheme.colors.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = TranslationManager.translate("voting_tap_to_cast"),
                                style = MaterialTheme.typography.caption.copy(
                                    color = ThemeColors.votingColor,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = if (MaterialTheme.colors.isLight)
                                        FontWeight.Medium
                                    else
                                        FontWeight.Normal
                                )
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = TranslationManager.translate("voting_vote_for").replace("{name}", club.name),
                            tint = ThemeColors.votingColor,
                            modifier = Modifier
                                .size(32.dp)
                                .padding(4.dp)
                        )
                    }
                }
            }
        }

        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            TranslationManager.translate("voting_confirm_title"),
                            style = MaterialTheme.typography.h6.copy(
                                color = ThemeColors.votingColor,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            TranslationManager.translate("voting_confirm_text"),
                            style = MaterialTheme.typography.body1.copy(
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            selectedClub?.name ?: "",
                            style = MaterialTheme.typography.h6.copy(
                                color = ThemeColors.votingColor,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            TranslationManager.translate("voting_remember_once"),
                            style = MaterialTheme.typography.caption.copy(
                                textAlign = TextAlign.Center,
                                fontStyle = FontStyle.Italic
                            )
                        )
                    }
                },
                buttons = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                selectedClub?.let { club ->
                                    scope.launch {
                                        viewModel.submitVote(deviceId, club)
                                    }
                                }
                                showConfirmDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = ThemeColors.votingColor
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                TranslationManager.translate("voting_yes_cast"),
                                color = Color.White,
                                style = MaterialTheme.typography.button.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        TextButton(
                            onClick = { showConfirmDialog = false },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = ThemeColors.votingColor
                            ),
                        ) {
                            Text(
                                TranslationManager.translate("voting_let_think"),
                                style = MaterialTheme.typography.button.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            )
        }

        Spacer(modifier = Modifier.weight(0.15f)) // Add bottom spacing

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = {
                    showSuccessDialog = false
                    onClose()
                },
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            TranslationManager.translate("voting_thank_you"),
                            style = MaterialTheme.typography.h5.copy(
                                color = ThemeColors.votingColor,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            TranslationManager.translate("voting_success_recorded"),
                            style = MaterialTheme.typography.body1.copy(
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            selectedClub?.name ?: "",
                            style = MaterialTheme.typography.h6.copy(
                                color = ThemeColors.votingColor,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            TranslationManager.translate("voting_check_results"),
                            style = MaterialTheme.typography.body1.copy(
                                textAlign = TextAlign.Center,
                                fontStyle = FontStyle.Italic
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            onClose()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = ThemeColors.votingColor
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            TranslationManager.translate("voting_cant_wait"),
                            color = Color.White,
                            style = MaterialTheme.typography.button.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            )
        }

        if (showAlreadyVotedDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAlreadyVotedDialog = false
                    onClose()
                },
                title = {
                    Text(
                        TranslationManager.translate("voting_already_title"),
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            TranslationManager.translate("voting_already_message"),
                            style = MaterialTheme.typography.body1.copy(
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            TranslationManager.translate("voting_check_results"),
                            style = MaterialTheme.typography.body1.copy(
                                textAlign = TextAlign.Center,
                                fontStyle = FontStyle.Italic
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showAlreadyVotedDialog = false
                            onClose()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = ThemeColors.votingColor
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            TranslationManager.translate("voting_ok"),
                            color = Color.White,
                            style = MaterialTheme.typography.button.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            )
        }
    }
}