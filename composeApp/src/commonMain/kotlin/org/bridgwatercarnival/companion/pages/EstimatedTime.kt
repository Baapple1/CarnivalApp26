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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import bridgwatercarnival.composeapp.generated.resources.Res
import bridgwatercarnival.composeapp.generated.resources.Route_map
import org.bridgwatercarnival.companion.theme.bungeeFont
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.bridgwatercarnival.companion.util.TranslationManager

@Composable
fun EstimatedTime(navController: NavHostController) {
	var showImageDialog by remember { mutableStateOf(false) }

	Scaffold(
	) { paddingValues ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
				.padding(horizontal = 16.dp)
				.background(MaterialTheme.colors.background)
				.verticalScroll(rememberScrollState()),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			// Title
			Text(
				text = TranslationManager.translate("estimated_arrival_time"),
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
			Spacer(modifier = Modifier.height(14.dp))

			// Route map image
			Image(
				painter = painterResource(Res.drawable.Route_map),
				contentDescription = TranslationManager.translate("route_map"),
				modifier = Modifier
					.fillMaxWidth()
					.clickable { showImageDialog = true }
			)

			// Image dialog for zooming
			if (showImageDialog) {
				FullScreenImageDialog(
					imageRes = Res.drawable.Route_map,
					onDismiss = { showImageDialog = false }
				)
			}

			// Warning note about delays
			Text(
				text = TranslationManager.translate("delay_warning"),
				style = MaterialTheme.typography.caption,
				textAlign = TextAlign.Center,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
			)

			// Schedule items
			Column(modifier = Modifier.padding(8.dp)) {
				listOf(
					Pair("18:00 hrs - " + TranslationManager.translate("start"), "start"),
					Pair("18:15 hrs - " + TranslationManager.translate("bath_road_bridge"), "normal"),
					Pair("18:25 hrs - " + TranslationManager.translate("cross_rifles"), "normal"),
					Pair("18:40 hrs - " + TranslationManager.translate("eastover"), "normal"),
					Pair("18:50 hrs - " + TranslationManager.translate("salmon_parade"), "normal"),
					Pair("19:15 hrs - " + TranslationManager.translate("carnival_inn"), "normal"),
					Pair("19:20 hrs - " + TranslationManager.translate("cornhill"), "normal"),
					Pair("19:30 hrs - " + TranslationManager.translate("penel_orlieu"), "normal"),
					Pair("19:45 hrs - " + TranslationManager.translate("finish"), "finish")
				).forEach { (item, type) ->
					Card(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 4.dp),
						elevation = 4.dp,
						backgroundColor = when (type) {
							"start" -> if (MaterialTheme.colors.isLight) Color(0xFF81C784) else Color(0xFF388E3C)
							"finish" -> if (MaterialTheme.colors.isLight) Color(0xFFEF5350) else Color(0xFFC62828)
							else -> if (MaterialTheme.colors.isLight) Color(0xFFBBDEFB) else Color(0xFF0D47A1)
						}
					) {
						Text(
							text = item,
							style = MaterialTheme.typography.h6,
							color = if (MaterialTheme.colors.isLight) Color.Black else Color.White,
							modifier = Modifier.padding(12.dp)
						)
					}
				}
			}

			Spacer(modifier = Modifier.height(16.dp))
		}
	}
}

@Composable
fun FullScreenImageDialog(imageRes: DrawableResource, onDismiss: () -> Unit) {
	var zoomState by remember { mutableStateOf(1f) }
	var offsetX by remember { mutableStateOf(0f) }
	var offsetY by remember { mutableStateOf(0f) }

	Dialog(onDismissRequest = onDismiss) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.pointerInput(Unit) {
					detectTransformGestures { _, pan, zoom, _ ->
						zoomState = (zoomState * zoom).coerceIn(1f..3f)
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
				}
		) {
			Image(
				painter = painterResource(imageRes),
				contentDescription = TranslationManager.translate("zoomable_map"),
				modifier = Modifier
					.fillMaxSize()
					.graphicsLayer(
						scaleX = zoomState,
						scaleY = zoomState,
						translationX = offsetX,
						translationY = offsetY
					)
					.clickable { onDismiss() }
			)
			// Close button
			Icon(
				imageVector = Icons.Default.Close,
				contentDescription = TranslationManager.translate("close"),
				modifier = Modifier
					.align(Alignment.TopEnd)
					.padding(16.dp)
					.clickable { onDismiss() }
			)
		}
	}
}