package org.bridgwatercarnival.companion.pages

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import bridgwatercarnival.composeapp.generated.resources.Griffens
import bridgwatercarnival.composeapp.generated.resources.Res
import org.bridgwatercarnival.companion.theme.bungeeFont
import org.bridgwatercarnival.companion.util.getUriHandler
import org.jetbrains.compose.resources.painterResource

@Composable
fun DaytimeEntertainment(navController: NavHostController) {
	val scrollState = rememberScrollState()
	val uriHandler = getUriHandler()

	Scaffold(
	) { paddingValues ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
				.padding(horizontal = 16.dp)
				.verticalScroll(scrollState),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				text = "Daytime Entertainment",
				style = MaterialTheme.typography.h4.copy(
					color = ThemeColors.entertainmentColor,
					fontFamily = bungeeFont,
					fontWeight = FontWeight.ExtraBold,
					fontSize = 32.sp,
					letterSpacing = 1.sp
				),
				textAlign = TextAlign.Center,
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp)
			)

			Divider(color = Color.Gray)

			Spacer(modifier = Modifier.height(16.dp))

			Box(
				modifier = Modifier.fillMaxWidth(),
				contentAlignment = Alignment.Center
			) {
				Image(
					painter = painterResource(Res.drawable.Griffens),
					contentDescription = "Griffens cc"
				)
			}
			Spacer(modifier = Modifier.height(16.dp))

			// Function to Display Event Schedules with improved styling
			@Composable
			fun EventSchedule(title: String, events: List<String>, backgroundColor: Color) {
				var expanded by remember { mutableStateOf(false) }
				val rotationState by animateFloatAsState(
					targetValue = if (expanded) 180f else 0f,
					animationSpec = tween(300)
				)
				
				val (emoji, titleColor) = when {
					title.contains("MAIN STAGE") -> "🎭" to Color(0xFF1976D2)      // Darker blue
					title.contains("CORNHILL") -> "🎪" to Color(0xFFD81B60)        // Darker pink
					title.contains("HIGH STREET") -> "🎵" to Color(0xFF388E3C)     // Darker green
					title.contains("ARTS CENTRE") -> "🎨" to Color(0xFF388E3C)     // Darker green
					title.contains("MARY") -> "⛪" to Color(0xFFF57C00)            // Darker orange
					else -> "🎡" to Color(0xFF7B1FA2)                             // Darker purple
				}
				
				Card(
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 8.dp)
						.clickable { expanded = !expanded }
						.animateContentSize(
							animationSpec = spring(
								dampingRatio = 0.8f,
								stiffness = 400f
							)
						),
					elevation = if (expanded) 8.dp else 4.dp,
					backgroundColor = backgroundColor,
					shape = RoundedCornerShape(24.dp)
				) {
					Column(
						modifier = Modifier.padding(16.dp)
					) {
						Row(
							modifier = Modifier.fillMaxWidth(),
							horizontalArrangement = Arrangement.SpaceBetween,
							verticalAlignment = Alignment.CenterVertically
						) {
							Row(
								verticalAlignment = Alignment.CenterVertically,
								modifier = Modifier.weight(1f)
							) {
								Surface(
									shape = RoundedCornerShape(12.dp),
									color = titleColor.copy(alpha = 0.15f),
									modifier = Modifier.padding(end = 12.dp)
								) {
									Text(
										text = emoji,
										fontSize = 36.sp,
										modifier = Modifier.padding(8.dp)
									)
								}
								
								Text(
									text = title.replace(Regex("^[🎭🎪🎵⛪🎡] "), ""),
									style = MaterialTheme.typography.h6.copy(
										fontWeight = FontWeight.ExtraBold,
										fontSize = 22.sp,
										letterSpacing = 0.5.sp
									),
									color = titleColor
								)
							}
							
							Icon(
								imageVector = Icons.Default.KeyboardArrowDown,
								contentDescription = if (expanded) "Collapse" else "Expand",
								modifier = Modifier
									.size(32.dp)
									.graphicsLayer { 
										rotationZ = rotationState 
									},
								tint = titleColor
							)
						}

						if (expanded) {
							Spacer(modifier = Modifier.height(16.dp))
							
							events.forEach { event ->
								val (time, description) = if (event.contains(" - ")) {
									event.split(" - ", limit = 2)
								} else {
									listOf("", event)
								}
								
								Row(
									modifier = Modifier
										.fillMaxWidth()
										.padding(vertical = 8.dp),
									verticalAlignment = Alignment.CenterVertically
								) {
									if (time.isNotEmpty()) {
										Surface(
											color = titleColor.copy(alpha = 0.2f),
											shape = RoundedCornerShape(12.dp),
											modifier = Modifier.padding(end = 12.dp)
										) {
											Text(
												text = time,
												style = MaterialTheme.typography.body2.copy(
													fontWeight = FontWeight.Bold,
													fontSize = 15.sp,
													letterSpacing = 0.5.sp
												),
												modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
												color = titleColor
											)
										}
									}
									
									Text(
										text = description,
										style = MaterialTheme.typography.body1.copy(
											fontSize = 17.sp,
											letterSpacing = 0.3.sp,
											fontWeight = FontWeight.SemiBold
										),
										color = Color.Black.copy(alpha = 0.85f)
									)
								}
							}
						} else {
							Row(
								modifier = Modifier
									.fillMaxWidth()
									.padding(top = 8.dp),
								horizontalArrangement = Arrangement.Center,
								verticalAlignment = Alignment.CenterVertically
							) {
								Text(
									text = "✨ Tap to see ${events.size} events ✨",
									style = MaterialTheme.typography.body1.copy(
										fontWeight = FontWeight.Bold,
										fontSize = 16.sp,
										letterSpacing = 0.5.sp
									),
									color = titleColor.copy(alpha = 0.9f)
								)
							}
						}
					}
				}
			}

			// Event schedules with pastel colors
			EventSchedule(
				"🎵 HIGH STREET PERFORMANCE AREA",
				listOf(
					"10:30 - Fox King Dance Academy",
					"11:00 - Bubbles LaFae",
					"11:40 - Bridgwater Rock Choir",
					"12:15 - Julia McDonald Dance Co",
					"12:40 - Farmyard Circus",
					"13:15 - Bridgwater Sea Cadets",
					"13:45 - The Lifesaver",
					"14:20 - Gugge 2000",
					"15:00 - Farmyard Circus",
					"15:35 - Bubbles LaFae",
					"16:10 - The Lifesaver",
					"16:50 - Gugge 2000"
				),
				Color(0xFFE8F5E9) // Light green pastel
			)

			EventSchedule(
				"⛪ ST MARY'S CHURCH",
				listOf(
					"11:15 - Somerset Songbirds",
					"12:00 - Wells City Band",
					"14:15 - Bridgwater Rock Choir"
				),
				Color(0xFFFFF3E0) // Light orange pastel
			)

			EventSchedule(
				"🎪 CORNHILL STAGE",
				listOf(
					"10:30 - Black Velvet Band",
					"11:15 - Westcan",
					"11:50 - Barnacle Buoys",
					"12:25 - Ashley Ross Quinn",
					"13:05 - Black Velvet Band",
					"13:55 - The Vixens",
					"14:30 - Talisha Sings",
					"15:20 - Two Tone",
					"16:10 - Shalana Serafina",
					"16:45 - Talisha Sings"
				),
				Color(0xFFF3E5F5) // Light purple pastel
			)

			EventSchedule(
				"🎭 ANGEL PLACE SHOPPING CENTRE",
				listOf(
					"10:30 - MADE Community Youth Theatre",
					"13:00 - Krazy Kev & Dinky",
					"13:40 - YMTC",
					"14:25 - Barnacle Buoys",
					"15:10 - Lucy Lost-It",
					"15:45 - Krazy Kev & Dinky",
					"16:20 - Simon Ellis",
				),
				Color(0xFFE3F2FD) // Light blue pastel
			)

			EventSchedule(
				"🎡 TOWN CENTRE",
				listOf(
					"Marvellous Magical Theatre",
					"Lucy Lost-It",
					"Bubbles Le Fae",
					"Children's Fairground Rides (from 17.40)",
					"Sounds of the Streets"
				),
				Color(0xFFFFEBEE) // Light red pastel
			)

			EventSchedule(
				"BRIDGWATER ARTS CENTRE",
				listOf(
					"Milly Riquelme's Cuban music\n" +
							"Dawn from HK\n" +
							"Bulgarian Folk Dance",
				),
				Color(0xFFE8F5E9) // Light green pastel
			)

			Spacer(modifier = Modifier.height(24.dp))

			// Website link with improved styling
			Card(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 8.dp),
				elevation = 2.dp,
				backgroundColor = Color(0xFFE1F5FE),
				shape = RoundedCornerShape(12.dp)
			) {
				Text(
					text = "Press to go to www.bridgwatercarnival.org.uk",
					style = MaterialTheme.typography.body1,
					color = Color(0xFF1976D2),
					textAlign = TextAlign.Center,
					modifier = Modifier
						.clickable { uriHandler.openUri("https://www.bridgwatercarnival.org.uk") }
						.padding(16.dp)
				)
			}

			Spacer(modifier = Modifier.height(80.dp))
		}
	}
}
