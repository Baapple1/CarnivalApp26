package org.bridgwatercarnival.companion.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun DeviceIdConsentDialog(
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDecline,
        title = { Text("Device ID Collection") },
        text = {
            Text(
                text = "To ensure fair voting and prevent multiple votes, we need to collect your device ID. " +
                      "This ID is unique to your device and will only be used to track your votes. " +
                      "You can still use the app without sharing your device ID, but voting and planning features will be disabled.",
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        },
        confirmButton = {
            TextButton(onClick = onAccept) {
                Text("Allow Collection")
            }
        },
        dismissButton = {
            TextButton(onClick = onDecline) {
                Text("Decline")
            }
        }
    )
} 