import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
import org.bridgwatercarnival.companion.util.TranslationManager

@Composable
fun TopClubsDialog(topClubs: List<Pair<String, Int>>, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface {
            Column {
                Text(TranslationManager.translate("top_clubs_title"), style = MaterialTheme.typography.h6)
                topClubs.forEachIndexed { index, (clubId, voteCount) ->
                    val medalIcon = when (index) {
                        0 -> "🥇" // Gold medal for 1st place
                        1 -> "🥈" // Silver medal for 2nd place
                        2 -> "🥉" // Bronze medal for 3rd place
                        else -> ""
                    }
                    Text(
                        TranslationManager.translate("top_clubs_votes")
                            .replace("{club}", "$medalIcon $clubId")
                            .replace("{count}", voteCount.toString())
                    )
                }
                Button(onClick = onDismiss) {
                    Text(TranslationManager.translate("top_clubs_close"))
                }
            }
        }
    }
}

