package org.bridgwatercarnival.companion.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import bridgwatercarnival.composeapp.generated.resources.*
import org.bridgwatercarnival.companion.theme.bungeeFont
import org.bridgwatercarnival.companion.util.TranslationManager
import org.bridgwatercarnival.companion.util.getUriHandler
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun SocialMedia(navController: NavHostController) {
	val scrollState = rememberScrollState() // Create a scroll state for vertical scrolling
	val uriHandler = getUriHandler() // Get the UriHandler to handle opening URLs

	@Composable
	fun SocialMediaItem(
		titleKey: String,
		image: DrawableResource,
		contentDescription: String,
		url: String
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.clickable { uriHandler.openUri(url) } // Open URL using UriHandler
				.padding(vertical = 8.dp), // Padding for the row
			verticalAlignment = Alignment.CenterVertically // Center content vertically
		) {
			// Icon
			Image(
				painter = painterResource(image),
				contentDescription = contentDescription,
				modifier = Modifier
					.size(48.dp) // Adjust size as needed
					.padding(end = 16.dp), // Space between icon and text
			)

			// Social Media Name
			Text(TranslationManager.translate(titleKey))
		}
	}

	// Add FAB inside Scaffold
	Scaffold(
	) { paddingValues ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
				.padding(horizontal = 16.dp)
				.verticalScroll(scrollState),
			horizontalAlignment = Alignment.Start // Align content to start
		) {
			Text(
				text = TranslationManager.translate("social_media_title"),
				style = MaterialTheme.typography.h4.copy(
					color = ThemeColors.additionalColor,
					fontFamily = bungeeFont
				),
				textAlign = TextAlign.Center,
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp)
			)

			Divider(color = Color.Gray)

			Spacer(modifier = Modifier.height(16.dp)) // Space between divider and next section

			// Image
			Image(
				painter = painterResource(Res.drawable.Marketeers),
				contentDescription = "Marketeers"
			)

			Spacer(modifier = Modifier.height(16.dp)) // Space between divider and next section

			// Social Media Links
			SocialMediaItem(
				"social_media_instagram",
				Res.drawable.instagram,
				"Instagram",
				"https://www.instagram.com/bridgwatercarnival?igsh=ODdhZWo2OTYwdG5y"
			)

			SocialMediaItem(
				"social_media_twitter",
				Res.drawable.twitter,
				"X (Twitter)",
				"https://twitter.com/bcarnival?lang=en"
			)

			SocialMediaItem(
				"social_media_facebook",
				Res.drawable.facebook,
				"Facebook",
				"https://www.facebook.com/BridgwaterGuyFawkesCarnival/?locale=en_GB"
			)

			SocialMediaItem(
				"social_media_youtube",
				Res.drawable.youtube,
				"Youtube",
				"https://www.youtube.com/@BGFCarnival"
			)

			SocialMediaItem(
				"social_media_donate",
				Res.drawable.Donation,
				"Donation",
				"https://www.bridgwatercarnival.org.uk/product-category/donations/"
			)

			Divider(
				color = Color.Gray,
				modifier = Modifier.padding(vertical = 16.dp)
			)

		}
	}
}
