package org.bridgwatercarnival.companion.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.NavHostController
import org.bridgwatercarnival.companion.theme.bungeeFont
import org.bridgwatercarnival.companion.util.TranslationManager
import org.bridgwatercarnival.companion.util.getUriHandler
import org.bridgwatercarnival.companion.components.SocialMediaIframe
import bridgwatercarnival.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

object NewsColors {
    val primary = Color(0xFFD32F2F)  // Red to match additional info section
    val facebook = Color(0xFF1877F2)
    val news = Color(0xFF00796B)     // Teal color for news tab

    @Composable
    fun background() = if (isSystemInDarkTheme()) Color.Black else Color.White

    @Composable
    fun cardBackground() = if (isSystemInDarkTheme()) Color(0xFF1E1E1E) else Color.White

    @Composable
    fun textPrimary() = if (isSystemInDarkTheme()) Color.White else Color.Black

    @Composable
    fun textSecondary() = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.7f)
}

@Composable
fun News(navController: NavHostController) {
    var selectedTab by remember { mutableStateOf(0) }
    var currentUrl by remember { mutableStateOf("") }
    var showNoteDialog by remember { mutableStateOf(true) }
    val uriHandler = getUriHandler()

    // Clearly define URLs
    val facebookUrl = "https://m.facebook.com/BridgwaterGuyFawkesCarnival"
    val carnivalNewsUrl = "https://www.bridgwatercarnival.org.uk/news/"

    // Setup Tab Data with explicitly defined types
    val tabs = listOf(
        Triple("Facebook", Res.drawable.facebook, facebookUrl),
        Triple("Carnival", Res.drawable.news, carnivalNewsUrl)
    )

    LaunchedEffect(selectedTab) {
        currentUrl = when (selectedTab) {
            0 -> facebookUrl
            1 -> carnivalNewsUrl
            else -> facebookUrl
        }
    }

    // Note Dialog
    if (showNoteDialog) {
        AlertDialog(
            onDismissRequest = { showNoteDialog = false },
            title = {
                Text(
                    text = "Note",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF795548)
                )
            },
            text = {
                Text(
                    text = TranslationManager.translate("news_note_open_link"),
                    style = MaterialTheme.typography.body1,
                    color = Color(0xFF795548)
                )
            },
            confirmButton = {
                TextButton(onClick = { showNoteDialog = false }) {
                    Text("Got it!")
                }
            },
            backgroundColor = Color(0xFFFFF9C4)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NewsColors.background())
    ) {
        // Title
        Text(
            text = TranslationManager.translate("news_title"),
            style = MaterialTheme.typography.h4,
            fontFamily = bungeeFont,
            color = NewsColors.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            backgroundColor = NewsColors.primary,
            contentColor = Color.White
        ) {
            tabs.forEachIndexed { index, (platform, icon, _) ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Image(
                                painter = painterResource(icon),
                                contentDescription = platform,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = platform,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )
            }
        }

        // Content - Full screen
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (currentUrl.isNotEmpty()) {
                if (selectedTab == 0) { // Facebook
                    // For Facebook, show a card that opens in browser
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(16.dp),
                        backgroundColor = NewsColors.cardBackground(),
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.facebook),
                                contentDescription = "Facebook",
                                modifier = Modifier.size(100.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Connect with Bridgwater Carnival",
                                style = MaterialTheme.typography.h5,
                                color = NewsColors.textPrimary(),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Follow our Facebook page for the latest updates, behind-the-scenes content, and community discussions.",
                                style = MaterialTheme.typography.body1,
                                color = NewsColors.textSecondary(),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { uriHandler.openUri(facebookUrl) },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = NewsColors.facebook
                                ),
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(56.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = painterResource(Res.drawable.facebook),
                                        contentDescription = "Facebook",
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Visit Our Facebook Page",
                                        color = Color.White,
                                        style = MaterialTheme.typography.button.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                } else { // News
                    // Force recomposition of iframe when tab changes by using key
                    key(selectedTab) {
                        // Use embedded iframe for the News website - Full screen
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            DisposableEffect(Unit) {
                                // JavaScript to run on component disposal
                                onDispose {
                                    // Clear any cached data when component is disposed
                                }
                            }
                            
                            SocialMediaIframe(
                                url = carnivalNewsUrl,
                                width = 800,
                                height = 1200,
                                modifier = Modifier.fillMaxSize(),
                                key = "$selectedTab-$currentUrl"
                            )
                        }
                    }
                }
            }
        }
    }
} 