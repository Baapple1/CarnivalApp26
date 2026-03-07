package org.bridgwatercarnival.companion.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import bridgwatercarnival.composeapp.generated.resources.Res
import bridgwatercarnival.composeapp.generated.resources.ucs2023
import org.bridgwatercarnival.companion.CONTACT_EMAIL
import org.bridgwatercarnival.companion.VERSION_NUMBER
import org.bridgwatercarnival.companion.theme.bungeeFont
import org.bridgwatercarnival.companion.util.TranslationManager
import org.bridgwatercarnival.companion.util.getUriHandler
import org.bridgwatercarnival.companion.util.getCurrentPlatformType
import org.bridgwatercarnival.companion.util.PlatformType
import org.jetbrains.compose.resources.painterResource


@Composable
fun AppInfo(navController: NavHostController) {
	val uriHandler = getUriHandler()
	
	Scaffold(
	) { paddingValues ->
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(paddingValues) //  padding here
				.padding(horizontal = 15.dp)
				.padding(top = 20.dp),

			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				text = TranslationManager.translate("app_info_title"),
				style = MaterialTheme.typography.h4.copy(
					color = ThemeColors.additionalColor,
					fontFamily = bungeeFont
				),
				textAlign = TextAlign.Center,
				modifier = Modifier.padding(bottom = 16.dp)
			)

			Divider(color = Color.Gray)

			Text(
				TranslationManager.translate("app_info_version").replace("{version}",
					when (getCurrentPlatformType()) {
						PlatformType.IOS -> "Ios Version 2"
						PlatformType.ANDROID -> "Android Version 1"
						else -> VERSION_NUMBER
					}
				),
				color = MaterialTheme.colors.onBackground,
				modifier = Modifier.padding(vertical = 20.dp)
			)

			Text(
				TranslationManager.translate("app_info_made_at"),
				color = MaterialTheme.colors.onBackground
			)

			Image(
				painter = painterResource(Res.drawable.ucs2023),
				contentDescription = "University Centre Somerset",
				modifier = Modifier.size(150.dp)
			)

			Text(
				text = TranslationManager.translate("app_info_feedback").replace("{email}", "enquiries@bridgwatercarnival.org.uk"),
				color = MaterialTheme.colors.onBackground,
				textAlign = TextAlign.Center,
				modifier = Modifier
					.fillMaxWidth()
					.clickable { 
						uriHandler.openUri("mailto:enquiries@bridgwatercarnival.org.uk")
					}
					.padding(vertical = 10.dp)
			)

			// Privacy Policy Link
			Text(
				text = TranslationManager.translate("social_media_privacy_policy"),
				modifier = Modifier
					.fillMaxWidth()
					.clickable {
						uriHandler.openUri("https://sites.google.com/view/bgfcprivatepolicy/home")
					}
					.padding(vertical = 8.dp),
				textAlign = TextAlign.Center,
				color = MaterialTheme.colors.primary
			)
		}
	}
}
