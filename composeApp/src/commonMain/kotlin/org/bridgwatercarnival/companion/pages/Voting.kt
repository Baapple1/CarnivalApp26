package org.bridgwatercarnival.companion.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.bridgwatercarnival.companion.components.DeviceDataConsentDialog
import org.bridgwatercarnival.companion.ui.VotingScreen
import org.bridgwatercarnival.companion.util.ConsentManager
import org.bridgwatercarnival.companion.util.TimeWindow
import org.bridgwatercarnival.companion.viewmodel.VotingViewModel

@Composable
fun Voting(viewModel: VotingViewModel, navController: NavHostController) {
    var showConsentDialog by remember { mutableStateOf(true) }
    var isVotingOpen by remember { mutableStateOf(false) }
    var hasVotingEnded by remember { mutableStateOf(false) }
    var timeUntilVoting by remember { mutableStateOf("") }

    // Check if voting is open or has ended
    LaunchedEffect(Unit) {
        isVotingOpen = TimeWindow.isVotingOpen()
        timeUntilVoting = TimeWindow.getTimeUntilVoting() 
        hasVotingEnded = TimeWindow.hasVotingEnded()
    }

    if (showConsentDialog) {
        DeviceDataConsentDialog(
            message = "The voting system uses device data to ensure one vote per device. This helps maintain fair voting for all carnival clubs.",
            onAccept = {
                showConsentDialog = false
            },
            onDecline = {
                showConsentDialog = false
                navController.navigateUp()
            }
        )
            }

        // Show VotingScreen, which will handle whether voting is open or not
        VotingScreen(viewModel = viewModel, onClose = {})
} 