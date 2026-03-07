package org.bridgwatercarnival.companion.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bridgwatercarnival.composeapp.generated.resources.BackArrow
import bridgwatercarnival.composeapp.generated.resources.Res
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bridgwatercarnival.companion.model.CarnivalClub
import org.bridgwatercarnival.companion.util.TimeWindow
import org.bridgwatercarnival.companion.util.getDeviceId
import org.bridgwatercarnival.companion.viewmodel.ResultsViewModel
import org.bridgwatercarnival.companion.viewmodel.ResultsState
import org.jetbrains.compose.resources.painterResource

@Composable
fun LockedScreen(
    message: String,
    isDarkMode: Boolean,
    onClose: () -> Unit
) {
    val backgroundColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black
    val secondaryTextColor = if (isDarkMode) Color(0xFFB0B0B0) else Color(0xFF666666)
    
    var currentMessage by remember { mutableStateOf(message) }
    val scope = rememberCoroutineScope()
    
    // Update the countdown every second
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000) // Wait for 1 second
            if (currentMessage.contains("Time until")) {
                // Get fresh countdown
                val timeUntilVoting = TimeWindow.getTimeUntilVoting()
                val timeUntilResults = TimeWindow.getTimeUntilResults()
                
                // Update the message with the new countdown
                currentMessage = if (message.contains("voting")) {
                    "Voting opens at 10:00 PM on November 1st!\n\nTime until voting: $timeUntilVoting"
                } else {
                    "Results will be available after the carnival!\n\nTime until results: $timeUntilResults"
                }
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Back button
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Image(
                painter = painterResource(Res.drawable.BackArrow),
                contentDescription = "Back",
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(if (isDarkMode) Color.White else Color.Black)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = secondaryTextColor,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Split the message into title and countdown
            val messageParts = currentMessage.split("\n\n")
            if (messageParts.size >= 2) {
                Text(
                    text = messageParts[0],
                    color = textColor,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = messageParts[1],
                    color = textColor,
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            } else {
                Text(
                    text = currentMessage,
                    color = textColor,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
            
            if (currentMessage.contains("Time until")) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Note: You may need to exit and return to this page when voting becomes available.",
                    color = secondaryTextColor,
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }
    }
}

@Composable
fun ResultsScreen(
    viewModel: ResultsViewModel,
    onClose: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var showNotAvailableDialog by remember { mutableStateOf(false) }
    var showResultsEndedDialog by remember { mutableStateOf(false) }
    var selectedClub by remember { mutableStateOf<CarnivalClub?>(null) }
    val scope = rememberCoroutineScope()
    val deviceId = remember {
        val id = getDeviceId()
        println("Generated Device ID: $id")
        id
    }

    val resultsState by viewModel.resultsState.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }
    var areResultsOpen by remember { mutableStateOf(false) }
    var hasResultsEnded by remember { mutableStateOf(false) }
    var timeUntilResults by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            areResultsOpen = TimeWindow.areResultsOpen()
            timeUntilResults = TimeWindow.getTimeUntilResults()
            hasResultsEnded = TimeWindow.hasResultsEnded()
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (hasResultsEnded) {
        LockedScreen(
            message = "Results are no longer available for this year's carnival. Check back next year!",
            isDarkMode = isSystemInDarkTheme(),
            onClose = onClose
        )
        return
    }

    if (!areResultsOpen) {
        LockedScreen(
            message = "Results will be available after the carnival!\n\nTime until results: $timeUntilResults",
            isDarkMode = isSystemInDarkTheme(),
            onClose = onClose
        )
        return
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Rest of the existing ResultsScreen code...
    }
} 