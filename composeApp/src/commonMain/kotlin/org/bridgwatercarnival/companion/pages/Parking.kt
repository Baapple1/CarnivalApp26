package org.bridgwatercarnival.companion.pages

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.NavHostController
import bridgwatercarnival.composeapp.generated.resources.CarParkInfo2025
import bridgwatercarnival.composeapp.generated.resources.Res
import bridgwatercarnival.composeapp.generated.resources.car_park
import bridgwatercarnival.composeapp.generated.resources.info_32
import bridgwatercarnival.composeapp.generated.resources.roadmap
import org.bridgwatercarnival.companion.theme.bungeeFont
import org.bridgwatercarnival.companion.util.TranslationManager
import org.bridgwatercarnival.companion.util.getUriHandler
import org.bridgwatercarnival.companion.components.SocialMediaIframe
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.DrawableResource

data class CarParkInfo(
	val name: String,
	val street: String,
	val postcode: String,
	val openingTime: String
)

@Composable
fun Parking(navController: NavHostController) {
	val scrollState = rememberScrollState()
	var expandedImage by remember { mutableStateOf<DrawableResource?>(null) }
	var zoomState by remember { mutableStateOf(1f) }

	if (expandedImage != null) {
		var offsetX by remember { mutableStateOf(0f) }
		var offsetY by remember { mutableStateOf(0f) }

		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(Color.Black)
				.pointerInput(Unit) {
					detectTransformGestures { _, pan, zoom, _ ->
						zoomState = (zoomState * zoom).coerceIn(1f, 3f)
						if (zoomState > 1f) {
							offsetX += pan.x
							offsetY += pan.y
							val maxOffset = (zoomState - 1) * 500
							offsetX = offsetX.coerceIn(-maxOffset, maxOffset)
							offsetY = offsetY.coerceIn(-maxOffset, maxOffset)
						} else {
							offsetX = 0f
							offsetY = 0f
						}
					}
				},
			contentAlignment = Alignment.Center
		) {
			Image(
				painter = painterResource(expandedImage!!),
				contentDescription = TranslationManager.translate("expanded_parking_map"),
				modifier = Modifier
					.fillMaxSize()
					.graphicsLayer(
						scaleX = zoomState,
						scaleY = zoomState,
						translationX = offsetX,
						translationY = offsetY
					),
				contentScale = ContentScale.Fit
			)

			IconButton(
				onClick = {
					expandedImage = null
					zoomState = 1f
					offsetX = 0f
					offsetY = 0f
				},
				modifier = Modifier
					.align(Alignment.TopEnd)
					.padding(16.dp)
			) {
				Icon(
					imageVector = Icons.Default.Close,
					contentDescription = TranslationManager.translate("close"),
					tint = Color.White
				)
			}
		}
	} else {
		Scaffold(
		) { paddingValues ->
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(paddingValues)
					.padding(horizontal = 16.dp)
					.verticalScroll(scrollState),
				horizontalAlignment = Alignment.Start
			) {
				Text(
					text = TranslationManager.translate("parking_information"),
					fontFamily = bungeeFont,
					style = MaterialTheme.typography.h4.copy(
						fontWeight = FontWeight.Bold,
						color = Color(0xFF00BCD4)
					),
					textAlign = TextAlign.Center,
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 24.dp)
				)
				Divider(color = Color.Gray)
				Spacer(modifier = Modifier.height(14.dp))

				Card(
					modifier = Modifier
						.fillMaxWidth()
						.padding(bottom = 16.dp),
					elevation = 4.dp,
					shape = RoundedCornerShape(12.dp)
				) {
					Image(
						painter = painterResource(Res.drawable.CarParkInfo2025),
						contentDescription = "Parking Map",
						modifier = Modifier
							.fillMaxWidth()
							.clickable { expandedImage = Res.drawable.CarParkInfo2025 },
						contentScale = ContentScale.FillWidth
					)
				}

				// Car Parks Section
				@Composable
				fun ParkingLocation(
					title: String,
					details: List<String>,
					backgroundColor: Color,
					iconRes: DrawableResource
				) {
					Card(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 8.dp),
						elevation = 4.dp,
						backgroundColor = backgroundColor,
						shape = RoundedCornerShape(16.dp)
					) {
						Column(
							modifier = Modifier.padding(16.dp)
						) {
							Row(
								verticalAlignment = Alignment.CenterVertically,
								modifier = Modifier.padding(bottom = 12.dp)
							) {
								Image(
									painter = painterResource(iconRes),
									contentDescription = null,
									modifier = Modifier.size(24.dp)
								)
								Spacer(modifier = Modifier.width(12.dp))
								Text(
									text = title,
									style = MaterialTheme.typography.h6.copy(
										fontWeight = FontWeight.Bold
									),
									color = Color.Black
								)
							}

							details.forEach { detail ->
								Row(
									modifier = Modifier
										.fillMaxWidth()
										.padding(vertical = 4.dp)
								) {
									Text(
										text = "•",
										color = Color.Black,
										modifier = Modifier.padding(end = 8.dp)
									)
									Text(
										text = detail,
										color = Color.Black,
										style = MaterialTheme.typography.body1
									)
								}
							}
						}
					}
				}

				// Collapsible Section Component
				@Composable
				fun CollapsibleSection(
					title: String,
					icon: String,
					backgroundColor: Color,
					content: @Composable () -> Unit
				) {
					var expanded by remember { mutableStateOf(false) }
					val rotationState by animateFloatAsState(
						targetValue = if (expanded) 180f else 0f,
						animationSpec = tween(300)
					)
					
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
						shape = RoundedCornerShape(16.dp)
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
									Text(
										text = icon,
										fontSize = 24.sp,
										modifier = Modifier.padding(end = 12.dp)
									)
									Text(
										text = title,
										style = MaterialTheme.typography.h6.copy(
											fontWeight = FontWeight.Bold
										),
										color = Color.Black
									)
								}

								Icon(
									imageVector = Icons.Default.KeyboardArrowDown,
									contentDescription = if (expanded) "Collapse" else "Expand",
									modifier = Modifier
										.size(24.dp)
										.graphicsLayer {
											rotationZ = rotationState
										}
								)
							}

							if (expanded) {
								Spacer(modifier = Modifier.height(16.dp))
								content()
							} else {
								Row(
									modifier = Modifier
										.fillMaxWidth()
										.padding(top = 8.dp),
									horizontalArrangement = Arrangement.Center,
									verticalAlignment = Alignment.CenterVertically
								) {
									Text(
										text = "✨ Tap to see details ✨",
										style = MaterialTheme.typography.body2.copy(
											fontWeight = FontWeight.Bold
										),
										color = Color.Gray
									)
								}
							}
						}
					}
				}

				// P+R Park and Ride
				CollapsibleSection(
					title = "P+R Park and Ride",
					icon = "🚌",
					backgroundColor = Color(0xFFFFEBEE)
				) {
					Text(
						text = "Operational: 2pm - 11.30pm (remove by midnight)",
						style = MaterialTheme.typography.body2,
						color = Color.Black,
						modifier = Modifier.padding(bottom = 12.dp)
					)
					
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceEvenly
					) {
						Card(
							backgroundColor = Color(0xFFE57373),
							shape = RoundedCornerShape(8.dp)
						) {
							Text(
								text = "£17 per car",
								style = MaterialTheme.typography.body2.copy(
									fontWeight = FontWeight.Bold
								),
								color = Color.White,
								modifier = Modifier.padding(8.dp)
							)
						}
						Card(
							backgroundColor = Color(0xFFE57373),
							shape = RoundedCornerShape(8.dp)
						) {
							Text(
								text = "Mini-Buses £30",
								style = MaterialTheme.typography.body2.copy(
									fontWeight = FontWeight.Bold
								),
								color = Color.White,
								modifier = Modifier.padding(8.dp)
							)
						}
					}
					
					Spacer(modifier = Modifier.height(12.dp))
					
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceEvenly
					) {
						Column(
							modifier = Modifier.weight(1f),
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							Text(
								text = "M5 J23",
								style = MaterialTheme.typography.subtitle2.copy(
									fontWeight = FontWeight.Bold
								),
								color = Color(0xFFD81B60)
							)
							Text(
								text = "HPC Park & Ride",
								style = MaterialTheme.typography.caption,
								color = Color.Black,
								textAlign = TextAlign.Center
							)
							Text(
								text = "Bridgwater Operatic",
								style = MaterialTheme.typography.caption,
								color = Color.Gray,
								fontStyle = FontStyle.Italic
							)
						}
						Column(
							modifier = Modifier.weight(1f),
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							Text(
								text = "M5 J24",
								style = MaterialTheme.typography.subtitle2.copy(
									fontWeight = FontWeight.Bold
								),
								color = Color(0xFFD81B60)
							)
							Text(
								text = "Livestock Market",
								style = MaterialTheme.typography.caption,
								color = Color.Black,
								textAlign = TextAlign.Center
							)
							Text(
								text = "Cancer Research UK",
								style = MaterialTheme.typography.caption,
								color = Color.Gray,
								fontStyle = FontStyle.Italic
							)
						}
					}
				}

				// Official Car Parks
				CollapsibleSection(
					title = "Official Car Parks",
					icon = "️ 🚘",
					backgroundColor = Color(0xFFE3F2FD)
				) {
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(bottom = 16.dp),
						horizontalArrangement = Arrangement.SpaceEvenly
					) {
						Card(
							backgroundColor = Color(0xFF1976D2),
							shape = RoundedCornerShape(8.dp)
						) {
							Text(
								text = "Cars £10",
								style = MaterialTheme.typography.body2.copy(
									fontWeight = FontWeight.Bold
								),
								color = Color.White,
								modifier = Modifier.padding(8.dp)
							)
						}
						Card(
							backgroundColor = Color(0xFF1976D2),
							shape = RoundedCornerShape(8.dp)
						) {
							Text(
								text = "Minibuses £20",
								style = MaterialTheme.typography.body2.copy(
									fontWeight = FontWeight.Bold
								),
								color = Color.White,
								modifier = Modifier.padding(8.dp)
							)
						}
					}
					
					Text(
						text = "Many blue badge spaces available",
						style = MaterialTheme.typography.body2,
						color = Color.Black,
						textAlign = TextAlign.Center,
						modifier = Modifier
							.fillMaxWidth()
							.padding(bottom = 12.dp)
					)
					
					// Car Parks Grid
					LazyVerticalGrid(
						columns = GridCells.Fixed(2),
						horizontalArrangement = Arrangement.spacedBy(8.dp),
						verticalArrangement = Arrangement.spacedBy(8.dp),
						modifier = Modifier.height(280.dp)
					) {
						val carParks = listOf(
							CarParkInfo("ASDA - SNAP", "East Quay", "TA6 5AZ", "2:00pm"),
							CarParkInfo("BRIDGWATER HOSPITAL - Royal British Legion", "Bower Lane", "TA6 4GU", "2:00pm"),
							CarParkInfo("BLAKE - Bridgwater Sea Cadets", "Northgate", "TA6 3EU", "10:00am"),
							CarParkInfo("WICKES - Weston Operatic", "Wylds Road", "TA6 4DH", "2:00pm"),
							CarParkInfo("MORRISONS - Bridgwater Rotary Club", "Broadway", "TA6 3LN", "2:00pm"),
							CarParkInfo("B&M - Bridgwater Rotary Club", "Broadway", "TA6 3LN", "2:00pm"),
							CarParkInfo("WEST QUAY - Bridgwater Sea Cadets", "Northgate", "TA6 3EU", "10:00am"),
							CarParkInfo("POLDEN BOWER SCHOOL - Royal British Legion", "Bower Lane", "TA6 4GU", "3:30pm"),
							CarParkInfo("ST. MATTHEWS FIELD - Westfield Church", "West Street", "TA6 7HD", "10:30am"),
							CarParkInfo("UCS COLLEGE - Wilstock Hub", "Bath Road", "TA6 4PZ", "10:30am")
						)
						
						items(carParks.size) { index ->
							Card(
								backgroundColor = when (index % 5) {
									0 -> Color(0xFFE3F2FD) // Light blue
									1 -> Color(0xFFF3E5F5) // Light purple
									2 -> Color(0xFFE8F5E9) // Light green
									3 -> Color(0xFFFFF3E0) // Light orange
									else -> Color(0xFFFFEBEE) // Light red
								},
								shape = RoundedCornerShape(8.dp)
							) {
								Column(
									modifier = Modifier.padding(8.dp),
									horizontalAlignment = Alignment.CenterHorizontally
								) {
									Text(
										text = "${('A' + index).takeIf { it <= 'K' } ?: 'K'}",
										style = MaterialTheme.typography.caption.copy(
											fontWeight = FontWeight.Bold
										),
										color = Color(0xFF1976D2)
									)
									Text(
										text = carParks[index].name,
										style = MaterialTheme.typography.caption,
										color = Color.Black,
										textAlign = TextAlign.Center
									)
									Text(
										text = carParks[index].street,
										style = MaterialTheme.typography.caption.copy(
											fontSize = 10.sp
										),
										color = Color.Gray,
										textAlign = TextAlign.Center
									)
									Text(
										text = carParks[index].postcode,
										style = MaterialTheme.typography.caption.copy(
											fontWeight = FontWeight.Bold,
											fontSize = 10.sp
										),
										color = Color(0xFF1976D2),
										textAlign = TextAlign.Center
									)
									Text(
										text = "Opens: ${carParks[index].openingTime}",
										style = MaterialTheme.typography.caption.copy(
											fontWeight = FontWeight.Bold,
											fontSize = 9.sp
										),
										color = Color(0xFFD81B60),
										textAlign = TextAlign.Center
									)
								}
							}
						}
					}
				}

				// Opening Times
				CollapsibleSection(
					title = "Opening Times",
					icon = "⏰",
					backgroundColor = Color(0xFFF3E5F5)
				) {
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceEvenly
					) {
						Column(
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							Text(
								text = "10:00am",
								style = MaterialTheme.typography.subtitle2.copy(
									fontWeight = FontWeight.Bold
								),
								color = Color(0xFFD81B60)
							)
							Text(
								text = "(C) & (G)",
								style = MaterialTheme.typography.caption,
								color = Color.Black
							)
						}
						Column(
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							Text(
								text = "10:30am",
								style = MaterialTheme.typography.subtitle2.copy(
									fontWeight = FontWeight.Bold
								),
								color = Color(0xFFD81B60)
							)
							Text(
								text = "(J) & (K)",
								style = MaterialTheme.typography.caption,
								color = Color.Black
							)
						}
						Column(
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							Text(
								text = "2:00pm",
								style = MaterialTheme.typography.subtitle2.copy(
									fontWeight = FontWeight.Bold
								),
								color = Color(0xFFD81B60)
							)
							Text(
								text = "Others",
								style = MaterialTheme.typography.caption,
								color = Color.Black
							)
						}
					}
					
					Spacer(modifier = Modifier.height(12.dp))
					
					Text(
						text = "All close at midnight",
						style = MaterialTheme.typography.body2.copy(
							fontWeight = FontWeight.Bold
						),
						color = Color(0xFFD81B60),
						textAlign = TextAlign.Center,
						modifier = Modifier.fillMaxWidth()
					)
				}

				// Important Notice
				Card(
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 16.dp),
					elevation = 2.dp,
					backgroundColor = Color(0xFFFFEBEE), // Light red pastel
					shape = RoundedCornerShape(12.dp)
				) {
					Row(
						modifier = Modifier.padding(16.dp),
						verticalAlignment = Alignment.CenterVertically
					) {
						Image(
							painter = painterResource(Res.drawable.info_32),
							contentDescription = "Important Notice",
							modifier = Modifier.size(24.dp),
							colorFilter = ColorFilter.tint(Color.Black)
						)
						Spacer(modifier = Modifier.width(12.dp))
						Text(
							text = "Please arrive early as parking fills up quickly. Follow stewards' instructions for smooth parking operations.",
							style = MaterialTheme.typography.body1,
							color = Color.Black
						)
					}
				}

				Spacer(modifier = Modifier.height(80.dp))
			}
		}
	}
}