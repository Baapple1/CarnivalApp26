package org.bridgwatercarnival.companion.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import bridgwatercarnival.composeapp.generated.resources.Res
import bridgwatercarnival.composeapp.generated.resources.Squibbing
import org.bridgwatercarnival.companion.theme.bungeeFont
import org.bridgwatercarnival.companion.util.TranslationManager
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import org.bridgwatercarnival.companion.components.VideoPlayerComponent
import androidx.compose.foundation.layout.Box

@Composable
fun Squibbing(navController: NavHostController) {
	Scaffold(
	) { paddingValues ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState()) // Enable scrolling
				.padding(paddingValues)
				.padding(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				text = TranslationManager.translate("squibbing_title"),
				style = MaterialTheme.typography.h4.copy(
					color = ThemeColors.essentialColor,
					fontFamily = bungeeFont
				),
				textAlign = TextAlign.Center,
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp)
			)

			Divider(color = Color.Gray)

			Spacer(modifier = Modifier.height(16.dp))

			Box(
				modifier = Modifier
					.fillMaxWidth()
					.border(
						width = 1.dp,
						color = ThemeColors.essentialColor.copy(alpha = 0.5f),
						shape = RoundedCornerShape(8.dp)
					)
					.clip(RoundedCornerShape(8.dp))
			) {
				VideoPlayerComponent(
					modifier = Modifier.fillMaxWidth(),
					autoAdvance = false,
					showControls = false,
					initialVideoId = "GAM3C5YiFLA" // ID from the YouTube link https://www.youtube.com/watch?v=GAM3C5YiFLA
				)
			}

			Spacer(modifier = Modifier.height(8.dp))

			Text(TranslationManager.translate("squibbing_location"))

			Spacer(modifier = Modifier.height(24.dp))

			Text(TranslationManager.translate("squibbing_children_warning"))

			Spacer(modifier = Modifier.height(16.dp))

			Text(TranslationManager.translate("squibbing_safety_warning"))

			Text(
				text = TranslationManager.translate("what_is_squibbing"),
				style = MaterialTheme.typography.h5,
				fontWeight = FontWeight.Bold,
				color = MaterialTheme.colors.primary,
				modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
			)

			Text(
				text = TranslationManager.translate("squibbing_description")
			)
		}
	}
}
