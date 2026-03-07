package org.bridgwatercarnival.companion.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import bridgwatercarnival.composeapp.generated.resources.Ramblers
import bridgwatercarnival.composeapp.generated.resources.Res
import org.bridgwatercarnival.companion.theme.bungeeFont
import org.bridgwatercarnival.companion.util.TranslatedText
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import bridgwatercarnival.composeapp.generated.resources.facebook
import bridgwatercarnival.composeapp.generated.resources.instagram
import bridgwatercarnival.composeapp.generated.resources.twitter
import androidx.compose.ui.platform.LocalUriHandler
import bridgwatercarnival.composeapp.generated.resources.website

@Composable
fun FirstTimeVisitorGuide(navController: NavHostController) {
	val scrollState = rememberScrollState()
	var currentCard by remember { mutableStateOf(0) }

	Scaffold(
	) { paddingValues ->
		Column(
			modifier = Modifier
				.padding(paddingValues)
				.verticalScroll(scrollState)
		) {
			// Header section
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp)
			) {
				TranslatedText(
					key = "first_time_visitor_guide",
					style = MaterialTheme.typography.h4.copy(
						color = ThemeColors.essentialColor,
						fontFamily = bungeeFont
					),
					textAlign = TextAlign.Center,
					modifier = Modifier
						.fillMaxWidth()
						.padding(16.dp)
				)

				Divider(color = Color.Gray, thickness = 1.dp)

				Spacer(modifier = Modifier.height(16.dp))

				Image(
					painter = painterResource(Res.drawable.Ramblers),
					contentDescription = "Ramblers Carnival Club",
					modifier = Modifier.fillMaxWidth()
				)

				Spacer(modifier = Modifier.height(16.dp))

				TranslatedText(
					key = "welcome_carnival",
					style = MaterialTheme.typography.h6.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
					textAlign = TextAlign.Center,
					modifier = Modifier.fillMaxWidth()
				)

				Spacer(modifier = Modifier.height(8.dp))

				TranslatedText(
					key = "quick_guide_intro",
					style = MaterialTheme.typography.body1.copy(fontSize = 16.sp),
					textAlign = TextAlign.Center
				)

				Spacer(modifier = Modifier.height(16.dp))
			}

			// Cards section
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 16.dp)
			) {
				LazyRow(
					modifier = Modifier.fillMaxWidth(),
					contentPadding = PaddingValues(horizontal = 16.dp),
					horizontalArrangement = Arrangement.spacedBy(12.dp)
				) {
					items(7) { index ->
						Box(
							modifier = Modifier.fillMaxWidth()
								.onGloballyPositioned { coordinates ->
									if (coordinates.isVisible()) {
										currentCard = index
									}
								}
						) {
							when (index) {
								0 -> SwipeableInfoCard(
									titleKey = "event_overview_title",
									textKey = "event_overview_text",
									backgroundColor = Color(0xFFFFB74D),
									funFacts = listOf(
										"🎪 Spectacular night parade!",
										"✨ Over 100 illuminated carts",
										"🎵 Live music and entertainment"
									)
								)
								1 -> SwipeableInfoCard(
									titleKey = "tickets_seating_title",
									textKey = "tickets_seating_text",
									backgroundColor = Color(0xFF4FC3F7),
									funFacts = listOf(
										"🎟️ Book early for best seats",
										"👨‍👩‍👧‍👦 Family packages available",
										"🌟 VIP viewing areas"
									)
								)
								2 -> SwipeableInfoCard(
									titleKey = "arriving_parking_title",
									textKey = "arriving_parking_text",
									backgroundColor = Color(0xFF81C784),
									funFacts = listOf(
										"🅿️ Parking will be £5",
										"🚶‍♂️ Easy walking access",
										"🗺️ Clear signage throughout"
									)
								)
								3 -> SwipeableInfoCard(
									titleKey = "what_to_expect_title",
									textKey = "what_to_expect_text",
									backgroundColor = Color(0xFFFF8A65),
									funFacts = listOf(
										"🎭 Amazing performances",
										"🍽️ Food and drink stalls",
										"🎪 Family entertainment"
									)
								)
								4 -> SwipeableInfoCard(
									titleKey = "additional_tips_title",
									textKey = "additional_tips_text",
									backgroundColor = Color(0xFFBA68C8),
									funFacts = listOf(
										"📱 Download our app",
										"🧥 Bring warm clothes",
										"📸 Photo opportunities"
									)
								)
								5 -> SwipeableInfoCard(
									titleKey = "more_info_title",
									textKey = "more_info_text",
									backgroundColor = Color(0xFFFFD54F),
									funFacts = listOf(
										"ℹ️ Information points",
										"🚻 Facilities available",
										"♿ Accessibility options"
									)
								)
								6 -> SwipeableInfoCard(
									titleKey = "connect_with_us",
									textKey = "connect_with_us_text",
									backgroundColor = Color(0xFF9575CD),
									funFacts = listOf(
										"connect_with_us_website",
										"connect_with_us_facebook",
										"connect_with_us_instagram",
										"connect_with_us_twitter"
									)
								)
							}
						}
					}
				}
			}

			// Page indicator dots
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = 16.dp),
				horizontalArrangement = Arrangement.Center
			) {
				repeat(7) { index ->
					Box(
						modifier = Modifier
							.padding(horizontal = 4.dp)
							.size(8.dp)
							.background(
								color = MaterialTheme.colors.primary.copy(
									alpha = if (index == currentCard) 1f else 0.3f
								),
								shape = CircleShape
							)
					)
				}
			}

			Spacer(modifier = Modifier.height(80.dp))
		}
	}
}

@Composable
fun SwipeableInfoCard(
	titleKey: String,
	textKey: String,
	backgroundColor: Color,
	funFacts: List<String>
) {
	var expanded by remember { mutableStateOf(false) }
	val uriHandler = LocalUriHandler.current
	
	Card(
		modifier = Modifier
			.width(320.dp)
			.height(if (expanded) 480.dp else 200.dp)
			.padding(horizontal = 8.dp)
			.clickable { expanded = !expanded },
		shape = RoundedCornerShape(24.dp),
		backgroundColor = backgroundColor.copy(alpha = 0.9f),
		elevation = if (expanded) 12.dp else 6.dp
	) {
		Box(
			modifier = Modifier.fillMaxSize()
		) {
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(16.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				// Card Header
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.background(
							Color.White.copy(alpha = 0.3f),
							RoundedCornerShape(16.dp)
						)
						.padding(8.dp),
					contentAlignment = Alignment.Center
				) {
					Column(
						horizontalAlignment = Alignment.CenterHorizontally
					) {
						Text(
							text = when {
								titleKey.contains("event") -> "🎭"
								titleKey.contains("tickets") -> "🎫️"
								titleKey.contains("parking") -> "🅿️"
								titleKey.contains("expect") -> "❓"
								titleKey.contains("tips") -> "💡"
								titleKey.contains("connect_with_us") -> "📱"
								else -> "ℹ️"
							},
							fontSize = 40.sp,
							modifier = Modifier.padding(bottom = 4.dp)
						)
						
						TranslatedText(
							key = titleKey,
							style = MaterialTheme.typography.h6.copy(
								fontSize = 20.sp,
								fontFamily = bungeeFont,
								color = Color.Black.copy(alpha = 0.7f),
								textAlign = TextAlign.Center
							)
						)
					}
				}

				// Expanded Content
				AnimatedVisibility(
					visible = expanded,
					enter = fadeIn() + expandVertically(),
					exit = fadeOut() + shrinkVertically()
				) {
					Column(
						modifier = Modifier
							.padding(top = 16.dp)
							.verticalScroll(rememberScrollState()),
						horizontalAlignment = Alignment.CenterHorizontally,
						verticalArrangement = Arrangement.spacedBy(12.dp)
					) {
						// Main content bullets
						funFacts.forEach { fact ->
							Row(
								modifier = Modifier
									.fillMaxWidth()
									.background(
										Color.White.copy(alpha = 0.5f),
										RoundedCornerShape(12.dp)
									)
									.padding(12.dp)
									.clickable {
										when (fact) {
											"connect_with_us_website" -> uriHandler.openUri("https://www.bridgwatercarnival.org.uk/")
											"connect_with_us_facebook" -> uriHandler.openUri("https://www.facebook.com/BridgwaterGuyFawkesCarnival/?locale=en_GB")
											"connect_with_us_instagram" -> uriHandler.openUri("https://www.instagram.com/bridgwatercarnival?igsh=ODdhZWo2OTYwdG5y")
											"connect_with_us_twitter" -> uriHandler.openUri("https://twitter.com/bcarnival?lang=en")
										}
									},
								horizontalArrangement = Arrangement.Start,
								verticalAlignment = Alignment.CenterVertically
							) {
								if (fact.startsWith("connect_with_us_")) {
									// Social media icons for connect with us card
									val iconKey = when (fact) {
										"connect_with_us_facebook" -> "social_media_icon_facebook"
										"connect_with_us_instagram" -> "social_media_icon_instagram"
										"connect_with_us_twitter" -> "social_media_icon_twitter"
										"connect_with_us_website" -> "social_media_icon_website"
										else -> null
									}
									
									if (iconKey != null) {
										Image(
											painter = painterResource(
												when (iconKey) {
													"social_media_icon_facebook" -> Res.drawable.facebook
													"social_media_icon_instagram" -> Res.drawable.instagram
													"social_media_icon_twitter" -> Res.drawable.twitter
													"social_media_icon_website" -> Res.drawable.website
													else -> Res.drawable.facebook
												}
											),
											contentDescription = when (iconKey) {
												"social_media_icon_facebook" -> "Facebook"
												"social_media_icon_instagram" -> "Instagram"
												"social_media_icon_twitter" -> "Twitter"
												"social_media_icon_website" -> "Website"
												else -> "Social Media Icon"
											},
											modifier = Modifier.size(24.dp)
										)
										Spacer(modifier = Modifier.width(8.dp))
									}
								} else {
									Text(
										text = "✦",
										fontSize = 20.sp,
										color = MaterialTheme.colors.primary
									)
									Spacer(modifier = Modifier.width(8.dp))
								}
								TranslatedText(
									key = fact,
									style = MaterialTheme.typography.body1.copy(
										fontSize = 16.sp,
										color = Color.Black.copy(alpha = 0.8f)
									)
								)
							}
						}
					}
				}

				// Updated "Tap to explore" button
				if (!expanded) {
					Column(
						modifier = Modifier.fillMaxSize(),
						verticalArrangement = Arrangement.Bottom,
						horizontalAlignment = Alignment.CenterHorizontally
					) {
						Row(
							verticalAlignment = Alignment.CenterVertically,
							modifier = Modifier
								.scale(scaleAnimation())
								.background(
									Color.White.copy(alpha = 0.5f),
									RoundedCornerShape(20.dp)
								)
								.padding(horizontal = 16.dp, vertical = 8.dp)
						) {
							Text(
								"👆Tap to explore👆",
								style = MaterialTheme.typography.button.copy(
									color = Color.Black.copy(alpha = 0.6f),
									fontSize = 16.sp,
									fontWeight = FontWeight.Medium
								)
							)
						}
					}
				}
			}
		}
	}
}

@Composable
private fun scaleAnimation(): Float {
	val infiniteTransition = rememberInfiniteTransition()
	return infiniteTransition.animateFloat(
		initialValue = 1f,
		targetValue = 1.1f,
		animationSpec = infiniteRepeatable(
			animation = tween(1000),
			repeatMode = RepeatMode.Reverse
		)
	).value
}

data class QuadrupleData(
	val first: String,
	val second: String,
	val third: Color,
	val fourth: List<String>
)

fun LayoutCoordinates.isVisible(): Boolean {
	return size.width > 0 && boundsInWindow().left < size.width / 2
}