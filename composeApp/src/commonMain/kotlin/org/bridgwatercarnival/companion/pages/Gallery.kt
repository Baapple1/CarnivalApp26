package org.bridgwatercarnival.companion.pages

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.bridgwatercarnival.companion.theme.bungeeFont
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import bridgwatercarnival.composeapp.generated.resources.Ramblers
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import bridgwatercarnival.composeapp.generated.resources.Res
import bridgwatercarnival.composeapp.generated.resources.TwentyFourGremlins
import androidx.compose.foundation.border
import org.bridgwatercarnival.companion.util.TranslationManager
import org.bridgwatercarnival.companion.components.VideoPlayerComponent
import androidx.compose.ui.text.font.FontWeight
import bridgwatercarnival.composeapp.generated.resources.AllOut

@Composable
fun Gallery(navController: NavHostController) {
	val scrollState = rememberScrollState()

	// Define carnival-themed neon colors
	val neonPink = Color(0xFFFF1493)     // Deep pink
	val neonBlue = Color(0xFF00BFFF)     // Deep sky blue
	val neonGreen = Color(0xFF39FF14)    // Electric green
	val neonYellow = Color(0xFFFFBE0B)   // Amber Gold

	Scaffold { paddingValues ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
				.padding(horizontal = 16.dp)
				.verticalScroll(scrollState),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			val titleScale by rememberInfiniteTransition().animateFloat(
				initialValue = 1f,
				targetValue = 1.05f,
				animationSpec = infiniteRepeatable(
					animation = tween(1000, easing = FastOutSlowInEasing),
					repeatMode = RepeatMode.Reverse
				)
			)

			Text(
				text = TranslationManager.translate("gallery_title"),
				style = MaterialTheme.typography.h5,
				fontFamily = bungeeFont,
				color = MaterialTheme.colors.primary,
				textAlign = TextAlign.Center,
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp)
					.graphicsLayer {
						scaleX = titleScale
						scaleY = titleScale
					}
			)

			// Carnival Videos Section
			Card(
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 24.dp)
					.border(
						width = 2.dp,
						color = neonBlue,
						shape = RoundedCornerShape(16.dp)
					)
					.clip(RoundedCornerShape(16.dp)),
				backgroundColor = MaterialTheme.colors.surface,
				elevation = 4.dp
			) {
				Column(
					modifier = Modifier.padding(16.dp),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(
						text = "Carnival Videos 📹",
						style = MaterialTheme.typography.h6,
						fontFamily = bungeeFont,
						color = neonBlue,
						textAlign = TextAlign.Center,
						modifier = Modifier.padding(bottom = 8.dp)
					)
					
					// Video player component
					Box(
						modifier = Modifier
							.fillMaxWidth()
							.border(
								width = 1.dp,
								color = neonBlue.copy(alpha = 0.5f),
								shape = RoundedCornerShape(8.dp)
							)
							.clip(RoundedCornerShape(8.dp))
					) {
						VideoPlayerComponent(
							modifier = Modifier.fillMaxWidth(),
							autoAdvance = true,
							shuffleIntervalSeconds = 180 // Change videos every 3 minutes
						)
					}
					
					Text(
						text = "Videos play in sequence every 3 minutes. Tap refresh button for next video.",
						style = MaterialTheme.typography.caption,
						color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
						textAlign = TextAlign.Center,
						modifier = Modifier.padding(top = 8.dp)
					)
				}
			}

			Spacer(modifier = Modifier.height(24.dp))

			// 2023 Gallery Card with pink neon
			GalleryCard(
				year = "2023",
				description = TranslationManager.translate("gallery_2023_preview_description"),
				emojis = listOf("🏆", "🎭", "✨"),
				onClick = { navController.navigate("gallery2023") },
				previewImage = Res.drawable.Ramblers,
				neonColor = neonPink
			)

			Spacer(modifier = Modifier.height(16.dp))

			// 2024 Gallery Card with blue neon
			GalleryCard(
				year = "2024",
				description = TranslationManager.translate("gallery_2024_preview_description"),
				emojis = listOf("🏆", "🎪", "✨"),
				onClick = { navController.navigate("gallery2024") },
				previewImage = Res.drawable.TwentyFourGremlins,
				neonColor = neonBlue
			)

			Spacer(modifier = Modifier.height(16.dp))

			// 2025 Gallery Card with yellow neon
			GalleryCard(
				year = "2025",
				description = TranslationManager.translate("gallery_2025_preview_description"),
				emojis = listOf("🏆", "🎪", "✨"),
				onClick = { navController.navigate("gallery2025") },
				previewImage = Res.drawable.AllOut,
				neonColor = neonYellow
			)

			Spacer(modifier = Modifier.height(24.dp))

			// 2026 Preview Card with neon green
			val previewScale by rememberInfiniteTransition().animateFloat(
				initialValue = 1f,
				targetValue = 1.05f,
				animationSpec = infiniteRepeatable(
					animation = tween(2000, easing = FastOutSlowInEasing),
					repeatMode = RepeatMode.Reverse
				)
			)

			val neonIntensity by rememberInfiniteTransition().animateFloat(
				initialValue = 0.5f,
				targetValue = 1f,
				animationSpec = infiniteRepeatable(
					animation = tween(1500, easing = LinearEasing)
				)
			)

			val glowingNeonColor = neonGreen.copy(alpha = 0.8f * neonIntensity)

			Card(
				modifier = Modifier
					.fillMaxWidth()
					.padding(8.dp)
					// Outer glow
					.shadow(
						elevation = 16.dp,
						shape = RoundedCornerShape(16.dp),
						spotColor = glowingNeonColor
					)
					// Middle glow
					.shadow(
						elevation = 12.dp,
						shape = RoundedCornerShape(16.dp),
						spotColor = glowingNeonColor
					)
					// Inner glow
					.shadow(
						elevation = 8.dp,
						shape = RoundedCornerShape(16.dp),
						spotColor = glowingNeonColor
					)
					.border(
						width = 4.dp,
						color = glowingNeonColor,
						shape = RoundedCornerShape(16.dp)
					)
					.clip(RoundedCornerShape(16.dp))
					.clickable { navController.navigate("gallery2026") }
					.graphicsLayer {
						scaleX = previewScale
						scaleY = previewScale
					},
				backgroundColor = MaterialTheme.colors.surface
			) {
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.padding(24.dp),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(
						text = TranslationManager.translate("gallery_2026_title"),
						style = MaterialTheme.typography.h6,
						fontFamily = bungeeFont,
						color = MaterialTheme.colors.primary,
						textAlign = TextAlign.Center
					)

					Spacer(modifier = Modifier.height(16.dp))

					// Animated emojis
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceEvenly
					) {
						listOf("🎊", "✨", "🏆","🎭").forEachIndexed { index, emoji ->
							val emojiScale by rememberInfiniteTransition().animateFloat(
								initialValue = 1f,
								targetValue = 1.2f,
								animationSpec = infiniteRepeatable(
									animation = tween(
										durationMillis = 1000,
										delayMillis = index * 300,
										easing = FastOutSlowInEasing
									),
									repeatMode = RepeatMode.Reverse
								)
							)
							Text(
								text = emoji,
								style = MaterialTheme.typography.h5,
								modifier = Modifier.graphicsLayer {
									scaleX = emojiScale
									scaleY = emojiScale
								}
							)
						}
					}

					Spacer(modifier = Modifier.height(16.dp))

					Text(
						text = TranslationManager.translate("coming_2026"),
						style = MaterialTheme.typography.h6,
						color = MaterialTheme.colors.primary,
						fontFamily = bungeeFont
					)

					Spacer(modifier = Modifier.height(8.dp))

					Text(
						text = TranslationManager.translate("gallery_2026_preview_description"),
						style = MaterialTheme.typography.body1,
						color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
						textAlign = TextAlign.Center
					)
				}
			}
		}
	}
}

@Composable
private fun GalleryCard(
	year: String,
	description: String,
	emojis: List<String>,
	onClick: () -> Unit,
	previewImage: DrawableResource? = null,
	neonColor: Color = MaterialTheme.colors.primary
) {
	var isHovered by remember { mutableStateOf(false) }
	val scale by animateFloatAsState(
		targetValue = if (isHovered) 1.05f else 1f,
		animationSpec = tween(200)
	)

	val neonIntensity by rememberInfiniteTransition().animateFloat(
		initialValue = 0.5f,
		targetValue = 1f,
		animationSpec = infiniteRepeatable(
			animation = tween(2000, easing = FastOutSlowInEasing),
			repeatMode = RepeatMode.Reverse
		)
	)

	val glowingNeonColor = neonColor.copy(alpha = 0.8f * neonIntensity)

	Box(
		modifier = Modifier
			.fillMaxWidth()
			.padding(8.dp)
			// Outer glow
			.shadow(
				elevation = 16.dp,
				shape = RoundedCornerShape(16.dp),
				spotColor = glowingNeonColor
			)
			// Middle glow
			.shadow(
				elevation = 12.dp,
				shape = RoundedCornerShape(16.dp),
				spotColor = glowingNeonColor
			)
			// Inner glow
			.shadow(
				elevation = 8.dp,
				shape = RoundedCornerShape(16.dp),
				spotColor = glowingNeonColor
			)
			.border(
				width = 4.dp,
				color = glowingNeonColor,
				shape = RoundedCornerShape(16.dp)
			)
			.clip(RoundedCornerShape(16.dp))
			.clickable(onClick = onClick)
			.graphicsLayer {
				scaleX = scale
				scaleY = scale
			}
	) {
		if (previewImage != null) {
			Image(
				painter = painterResource(previewImage),
				contentDescription = "Preview for $year carnival",
				modifier = Modifier.fillMaxWidth(),
				contentScale = ContentScale.FillWidth
			)
		}

		// Semi-transparent gradient overlay
		Box(
			modifier = Modifier
				.matchParentSize()
				.background(
					brush = Brush.verticalGradient(
						colors = listOf(
							Color.Transparent,
							Color.Black.copy(alpha = 0.4f)
						),
						startY = 0.7f * Float.POSITIVE_INFINITY,
						endY = Float.POSITIVE_INFINITY
					)
				)
		)

		// Text overlay at the bottom
		Column(
			modifier = Modifier
				.align(Alignment.BottomStart)
				.fillMaxWidth()
				.padding(12.dp),
			horizontalAlignment = Alignment.Start,
			verticalArrangement = Arrangement.spacedBy(4.dp)
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.Start,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = TranslationManager.translate("gallery_year_title").replace("{year}", year),
					style = MaterialTheme.typography.h6,
					fontFamily = bungeeFont,
					color = Color.White,
					textAlign = TextAlign.Start
				)
				Spacer(modifier = Modifier.width(8.dp))
				emojis.forEachIndexed { index, emoji ->
					val emojiScale by rememberInfiniteTransition().animateFloat(
						initialValue = 1f,
						targetValue = 1.2f,
						animationSpec = infiniteRepeatable(
							animation = tween(
								durationMillis = 1000,
								delayMillis = index * 300,
								easing = FastOutSlowInEasing
							),
							repeatMode = RepeatMode.Reverse
						)
					)
					Text(
						text = emoji,
						style = MaterialTheme.typography.subtitle1,
						modifier = Modifier
							.graphicsLayer {
								scaleX = emojiScale
								scaleY = emojiScale
							}
							.padding(end = 4.dp)
					)
				}
			}

			Text(
				text = description,
				style = MaterialTheme.typography.body2,
				color = Color.White.copy(alpha = 0.8f),
				textAlign = TextAlign.Start
			)
		}
	}
}

// Helper composable for video list items
@Composable
private fun CarnivalVideoListItem(title: String, description: String) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp)
	) {
		Text(
			text = "• $title",
			style = MaterialTheme.typography.body2.copy(
				fontWeight = FontWeight.Bold
			),
			color = MaterialTheme.colors.onBackground
		)
		Text(
			text = description,
			style = MaterialTheme.typography.caption,
			color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f),
			modifier = Modifier.padding(start = 12.dp)
		)
	}
}
