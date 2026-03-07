package org.bridgwatercarnival.companion.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import bridgwatercarnival.composeapp.generated.resources.Closure_Map
import bridgwatercarnival.composeapp.generated.resources.Res
import org.bridgwatercarnival.companion.theme.bungeeFont
import org.bridgwatercarnival.companion.util.TranslationManager
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.isSystemInDarkTheme

@Composable
fun RoadClosure(navController: NavHostController) {
	var expandedImage by remember { mutableStateOf<DrawableResource?>(null) }
	var zoomState by remember { mutableStateOf(1f) }
	var isSortedByTime by remember { mutableStateOf(true) }

	val roadClosureItems = listOf(
		RoadClosureItem("closure_point_1", "16:00", 1),
		RoadClosureItem("closure_point_2", "16:00", 2),
		RoadClosureItem("closure_point_3", "16:00", 3),
		RoadClosureItem("closure_point_4", "16:00", 4),
		RoadClosureItem("closure_point_5", "16:00", 5),
		RoadClosureItem("closure_point_6", "16:00", 6),
		RoadClosureItem("closure_point_7", "16:00", 7),
		RoadClosureItem("closure_point_8", "16:00", 8),
		RoadClosureItem("closure_point_9", "16:00", 9),
		RoadClosureItem("closure_point_10", "13:00", 10),
		RoadClosureItem("closure_point_11", "08:00", 11),
		RoadClosureItem("closure_point_12", "09:00", 12)
	)

	val sortedRoadClosureItems = if (isSortedByTime) {
		roadClosureItems.sortedBy { it.closureTime }
	} else {
		roadClosureItems.sortedBy { it.closurePointNumber }
	}

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
				contentDescription = TranslationManager.translate("expanded_closure_map"),
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
		Scaffold { paddingValues ->
			Column(
				modifier = Modifier
					.fillMaxSize()
					.verticalScroll(rememberScrollState())
					.padding(paddingValues)
					.padding(16.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					text = TranslationManager.translate("road_closure"),
					style = MaterialTheme.typography.h4,
					fontFamily = bungeeFont,
					color = Color(0xFF00BCD4),
					modifier = Modifier.padding(16.dp)
				)
				
				Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.Gray)
				Spacer(modifier = Modifier.height(12.dp))

				Image(
					painter = painterResource(Res.drawable.Closure_Map),
					contentDescription = TranslationManager.translate("road_closure_map"),
					modifier = Modifier
						.fillMaxWidth()
						.clickable { expandedImage = Res.drawable.Closure_Map }
				)

				Spacer(modifier = Modifier.height(16.dp))

				Button(
					onClick = { isSortedByTime = !isSortedByTime },
					modifier = Modifier.padding(vertical = 16.dp)
				) {
					Text(text = if (isSortedByTime) 
						TranslationManager.translate("sort_by_point") 
					else 
						TranslationManager.translate("sort_by_time")
					)
				}

				Card(
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 8.dp),
					backgroundColor = if (isSystemInDarkTheme()) Color(0xFF1E1E1E) else Color.White,
					elevation = 4.dp
				) {
					Column(
						modifier = Modifier.padding(16.dp)
					) {
						Text(
							text = TranslationManager.translate("road_closure_schedule"),
							style = MaterialTheme.typography.h6,
							fontWeight = FontWeight.Bold,
							color = if (isSystemInDarkTheme()) Color.White else Color.Black,
							modifier = Modifier.padding(bottom = 16.dp)
						)

						sortedRoadClosureItems.forEach { item ->
							Row(
								modifier = Modifier
									.fillMaxWidth()
									.padding(vertical = 4.dp)
							) {
								Text(
									text = item.closureTime,
									color = when (item.closureTime) {
										"08:00" -> if (isSystemInDarkTheme()) Color(0xFF81C784) else Color(0xFF1B5E20)
										"09:00" -> if (isSystemInDarkTheme()) Color(0xFF64B5F6) else Color(0xFF0D47A1)
										"13:00" -> if (isSystemInDarkTheme()) Color(0xFFAB47BC) else Color(0xFF4A148C)
										else -> if (isSystemInDarkTheme()) Color(0xFFE57373) else Color(0xFFC62828)
									},
									fontWeight = FontWeight.Bold,
									modifier = Modifier.width(50.dp),
									style = MaterialTheme.typography.body1
								)
								Spacer(modifier = Modifier.width(8.dp))
								Text(
									text = TranslationManager.translate(item.displayText),
									modifier = Modifier.weight(1f),
									color = if (isSystemInDarkTheme()) Color.White else Color.Black,
									style = MaterialTheme.typography.body1,
									fontWeight = FontWeight.Medium
								)
							}
							if (item != sortedRoadClosureItems.last()) {
								Divider(
									modifier = Modifier.padding(vertical = 4.dp),
									color = Color(0xFFE0E0E0)
								)
							}
						}
					}
				}

				Spacer(modifier = Modifier.height(80.dp))
			}
		}
	}
}

data class RoadClosureItem(
	val displayText: String,
	val closureTime: String,
	val closurePointNumber: Int
)
