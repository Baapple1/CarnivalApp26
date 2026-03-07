package org.bridgwatercarnival.companion.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bridgwatercarnival.composeapp.generated.resources.ComingSoonEntertainment
import bridgwatercarnival.composeapp.generated.resources.Guide
import bridgwatercarnival.composeapp.generated.resources.Res
import bridgwatercarnival.composeapp.generated.resources.Voting
import bridgwatercarnival.composeapp.generated.resources.cinemamask
import bridgwatercarnival.composeapp.generated.resources.comingsoon
import bridgwatercarnival.composeapp.generated.resources.content
import bridgwatercarnival.composeapp.generated.resources.firework
import bridgwatercarnival.composeapp.generated.resources.forbidden_sign
import bridgwatercarnival.composeapp.generated.resources.image_gallery
import bridgwatercarnival.composeapp.generated.resources.info_32
import bridgwatercarnival.composeapp.generated.resources.news
import bridgwatercarnival.composeapp.generated.resources.parkingplace
import bridgwatercarnival.composeapp.generated.resources.planner
import bridgwatercarnival.composeapp.generated.resources.podium
import bridgwatercarnival.composeapp.generated.resources.question
import bridgwatercarnival.composeapp.generated.resources.time
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.bridgwatercarnival.companion.PageIndex
import org.bridgwatercarnival.companion.theme.bungeeFont
import org.bridgwatercarnival.companion.util.TranslationManager
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.random.Random

// Two color sets for light and dark modes
object ThemeColors {
	// Base colors for icons and buttons (bright colors)
	private val essentialBase = Color(0xFF1976D2) // Brighter blue
	private val travelBase = Color(0xFF009688) // Brighter teal
	private val entertainmentBase = Color(0xFF388E3C) // Brighter green
	private val votingBase = Color(0xFFF57C00) // Brighter orange
	private val additionalBase = Color(0xFFD32F2F) // Brighter red

	// Card background colors (darker versions)
	val essentialColor = Color(0xFF1565C0) // Darker blue
	val travelColor = Color(0xFF00796B) // Darker teal
	val entertainmentColor = Color(0xFF2E7D32) // Darker green
	val votingColor = Color(0xFFEF6C00) // Darker orange
	val additionalColor = Color(0xFFC62828) // Darker red for additional info

	// Pastel background colors for dropdown cards
	val essentialPastel = Color(0xFFBBDEFB) // Light pastel blue
	val travelPastel = Color(0xFFB2DFDB) // Light pastel teal
	val entertainmentPastel = Color(0xFFC8E6C9) // Light pastel green
	val votingPastel = Color(0xFFFFE0B2) // Light pastel orange
	val additionalPastel = Color(0xFFFFCDD2) // Light pastel red

	// New pastel color for countdown
	val countdownTextColor = Color(0xFF000000) // Black for countdown text
	val countdownBackgroundColor = Color(0xFFE1BEE7) // Light pastel lavender for countdown background

	// Button colors with light/dark mode support (using brighter base colors)
	val essentialButton: (Boolean) -> Color = { isLight ->
		if (isLight) essentialBase else essentialBase.copy(alpha = 0.8f)
	}
	val travelButton: (Boolean) -> Color = { isLight ->
		if (isLight) travelBase else travelBase.copy(alpha = 0.8f)
	}
	val entertainmentButton: (Boolean) -> Color = { isLight ->
		if (isLight) entertainmentBase else entertainmentBase.copy(alpha = 0.8f)
	}
	val votingButton: (Boolean) -> Color = { isLight ->
		if (isLight) votingBase else votingBase.copy(alpha = 0.8f)
	}
	val additionalButton: (Boolean) -> Color = { isLight ->
		if (isLight) additionalBase else additionalBase.copy(alpha = 0.8f)
	}
}

@Composable
private fun CategoryLabel(text: String, colorType: (Boolean) -> Color) {
	Text(
		text = text,
		style = MaterialTheme.typography.h6.copy(
			fontWeight = FontWeight.Bold
		),
		fontSize = 25.sp,
		color = colorType(MaterialTheme.colors.isLight),
		modifier = Modifier.padding(start = 8.dp, top = 24.dp, bottom = 12.dp)
	)
}

@Composable
private fun IconMenuButton(
	title: String,
	icon: ImageVector,
	onClick: () -> Unit,
	color: Color
) {
	val iconTint = if (MaterialTheme.colors.isLight) {
		Color.Black.copy(alpha = 9.0f)  // Darker in light mode
	} else {
		Color.White.copy(alpha = 0.9f)    // Pure white in dark mode
	}

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier
			.padding(horizontal = 80.dp, vertical = 40.dp)
			.width(80.dp)
			.height(90.dp)
	) {
		Card(
			modifier = Modifier.size(64.dp),
			backgroundColor = color.copy(alpha = 0.15f),
			elevation = 0.dp,
			shape = RoundedCornerShape(16.dp)
		) {
			IconButton(onClick = onClick) {
				Icon(
					imageVector = icon,
					contentDescription = title,
					tint = iconTint,
					modifier = Modifier
						.size(28.dp)
						.padding(4.dp)
				)
			}
		}
		Spacer(modifier = Modifier.height(4.dp))
		Text(
			text = title,
			fontSize = 12.sp,
			textAlign = TextAlign.Center,
			color = MaterialTheme.colors.onBackground,
			maxLines = 2,
			modifier = Modifier.padding(horizontal = 4.dp)
		)
	}
}

@Composable
private fun NavButton(
	page: PageIndex,
	title: String,
	icon: DrawableResource,
	onClick: () -> Unit,
	color: Color,
	textColor: Color,
	textSize: TextUnit,
	iconSize: Dp
) {
	val iconTint = if (MaterialTheme.colors.isLight) {
		Color.Black
	} else {
		Color.White
	}

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier
			.width(120.dp)
			.padding(vertical = 4.dp)
	) {
		Card(
			modifier = Modifier.size(65.dp),
			backgroundColor = if (MaterialTheme.colors.isLight) {
				color.copy(alpha = 0.08f)
			} else {
				color.copy(alpha = 0.65f)
			},
			elevation = 0.dp,
			shape = RoundedCornerShape(20.dp)
		) {
			IconButton(
				onClick = onClick,
				modifier = Modifier.fillMaxSize()
			) {
				Icon(
					painter = painterResource(icon),
					contentDescription = title,
					tint = iconTint,
					modifier = Modifier
						.size(iconSize)
						.padding(2.dp)
				)
			}
		}
		Spacer(modifier = Modifier.height(8.dp))
		Text(
			text = title,
			fontSize = textSize,
			fontWeight = FontWeight.Bold,
			textAlign = TextAlign.Center,
			color = textColor,
			maxLines = 2,
			modifier = Modifier.padding(horizontal = 4.dp)
		)
	}
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Help(onNavigate: (PageIndex) -> Unit) {
	val scrollState = rememberScrollState()
	val isLight = MaterialTheme.colors.isLight

	// Use rememberSaveable for countdown state to persist across recompositions
	var days by rememberSaveable { mutableStateOf(0L) }
	var hours by rememberSaveable { mutableStateOf(0L) }
	var minutes by rememberSaveable { mutableStateOf(0L) }
	var seconds by rememberSaveable { mutableStateOf(0L) }
	var isFinished by rememberSaveable { mutableStateOf(false) }

	// Expanded states for each category
	var essentialExpanded by rememberSaveable { mutableStateOf(false) }
	var travelExpanded by rememberSaveable { mutableStateOf(false) }
	var entertainmentExpanded by rememberSaveable { mutableStateOf(false) }
	var additionalExpanded by rememberSaveable { mutableStateOf(false) }
	var votingExpanded by rememberSaveable { mutableStateOf(false) }

	// Animation states
	val scale by animateFloatAsState(
		targetValue = if (isFinished) 1.2f else 1f,
		animationSpec = repeatable(
			iterations = if (isFinished) Int.MAX_VALUE else 0,
			animation = tween(durationMillis = 1000),
			repeatMode = RepeatMode.Reverse
		)
	)

	// Use a stable key for LaunchedEffect to prevent reinitialization
	LaunchedEffect(key1 = Unit) {
		// Initial calculation
		updateCountdownState { newDays, newHours, newMinutes, newSeconds, newIsFinished ->
			days = newDays
			hours = newHours
			minutes = newMinutes
			seconds = newSeconds
			isFinished = newIsFinished
		}

		// Update every second
		while (true) {
			delay(1000)
			updateCountdownState { newDays, newHours, newMinutes, newSeconds, newIsFinished ->
				days = newDays
				hours = newHours
				minutes = newMinutes
				seconds = newSeconds
				isFinished = newIsFinished
			}
		}
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(scrollState)
			.padding(10.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		// Main title
		Text(
			text = TranslationManager.translate("explore"),
			modifier = Modifier.padding(bottom = 10.dp),
			style = MaterialTheme.typography.h4.copy(
				color = MaterialTheme.colors.onBackground,
				fontWeight = FontWeight.Bold,
				fontFamily = bungeeFont
			)
		)
		Divider(color = Color.Gray)
		Spacer(modifier = Modifier.height(4.dp))

		CountdownSection(
			days = days,
			hours = hours,
			minutes = minutes,
			seconds = seconds,
			isFinished = isFinished
		)
		Spacer(modifier = Modifier.height(2.dp))

		// Essential Information Section
		ExpandableCategory(
			title = TranslationManager.translate("essential_info"),
			expanded = essentialExpanded,
			onExpandedChange = { essentialExpanded = it },
			color = ThemeColors.essentialColor,
			backgroundColor = ThemeColors.essentialPastel
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.horizontalScroll(rememberScrollState())
					.padding(horizontal = 8.dp),
				horizontalArrangement = Arrangement.spacedBy(16.dp)
			) {
				NavButton(
					PageIndex.FIRST_TIME_VISITOR_GUIDE,
					TranslationManager.translate("first_time_guide"),
					Res.drawable.Guide,
					onClick = { onNavigate(PageIndex.FIRST_TIME_VISITOR_GUIDE) },
					color = ThemeColors.essentialButton(isLight),
					textColor = Color.Black,
					textSize = 16.sp,
					iconSize = 24.dp
				)
				NavButton(
					PageIndex.ESTIMATED_TIME,
					TranslationManager.translate("arrival_times"),
					Res.drawable.time,
					onClick = { onNavigate(PageIndex.ESTIMATED_TIME) },
					color = ThemeColors.essentialButton(isLight),
					textColor = Color.Black,
					textSize = 16.sp,
					iconSize = 24.dp
				)
				NavButton(
					PageIndex.SQUIBBING,
					TranslationManager.translate("squibbing_guide"),
					Res.drawable.firework,
					onClick = { onNavigate(PageIndex.SQUIBBING) },
					color = ThemeColors.essentialButton(isLight),
					textColor = Color.Black,
					textSize = 16.sp,
					iconSize = 24.dp
				)
			}
		}

		// Travel & Access Section
		ExpandableCategory(
			title = TranslationManager.translate("travel_access"),
			expanded = travelExpanded,
			onExpandedChange = { travelExpanded = it },
			color = ThemeColors.travelColor,
			backgroundColor = ThemeColors.travelPastel
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.Start
			) {
				NavButton(
					PageIndex.PARKING,
					TranslationManager.translate("parking"),
					Res.drawable.parkingplace,
					onClick = { onNavigate(PageIndex.PARKING) },
					color = ThemeColors.travelButton(isLight),
					textColor = Color.Black,
					textSize = 20.sp,
					iconSize = 25.dp
				)
				NavButton(
					PageIndex.ROAD_CLOSURE,
					TranslationManager.translate("road_closures"),
					Res.drawable.forbidden_sign,
					onClick = { onNavigate(PageIndex.ROAD_CLOSURE) },
					color = ThemeColors.travelButton(isLight),
					textColor = Color.Black,
					textSize = 20.sp,
					iconSize = 25.dp
				)
				NavButton(
					PageIndex.PLANNER,
					TranslationManager.translate("planner"),
					Res.drawable.planner,
					onClick = { onNavigate(PageIndex.PLANNER) },
					color = ThemeColors.travelButton(isLight),
					textColor = Color.Black,
					textSize = 20.sp,
					iconSize = 25.dp
				)
			}
		}

		// Entertainment Section
		ExpandableCategory(
			title = TranslationManager.translate("entertainment"),
			expanded = entertainmentExpanded,
			onExpandedChange = { entertainmentExpanded = it },
			color = ThemeColors.entertainmentColor,
			backgroundColor = ThemeColors.entertainmentPastel
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.Start
			) {
				NavButton(
					PageIndex.DAYTIME_ENTERTAINMENT,
					TranslationManager.translate("daytime_events"),
					Res.drawable.cinemamask,
					onClick = { onNavigate(PageIndex.DAYTIME_ENTERTAINMENT) }, //chnage ii back to DAYTIME_ENTERTAINMENT) when adding back
					color = ThemeColors.entertainmentButton(isLight),
					textColor = Color.Black,
					textSize = 20.sp,
					iconSize = 40.dp // Increased icon size for logo prominence
				)
				NavButton(
					PageIndex.GALLERY,
					TranslationManager.translate("gallery"),
					Res.drawable.image_gallery,
					onClick = { onNavigate(PageIndex.GALLERY) },
					color = ThemeColors.entertainmentButton(isLight),
					textColor = Color.Black,
					textSize = 20.sp,
					iconSize = 25.dp
				)
			}
		}

		// Voting Section
//		ExpandableCategory(
//			title = TranslationManager.translate("voting"),
//			expanded = votingExpanded,
//			onExpandedChange = { votingExpanded = it },
//			color = ThemeColors.votingColor,
//			backgroundColor = ThemeColors.votingPastel
//		) {
//			Row(
//				modifier = Modifier.fillMaxWidth(),
//				horizontalArrangement = Arrangement.Start
//			) {
//				NavButton(
//					PageIndex.VOTING,
//					TranslationManager.translate("vote_now"),
//					Res.drawable.Voting,
//					onClick = { onNavigate(PageIndex.VOTING) },
//					color = ThemeColors.votingButton(isLight),
//					textColor = Color.Black,
//					textSize = 20.sp,
//					iconSize = 25.dp
//				)
//				NavButton(
//					PageIndex.VOTINGRESULTS,
//					TranslationManager.translate("view_results"),
//					Res.drawable.podium,
//					onClick = { onNavigate(PageIndex.VOTINGRESULTS) },
//					color = ThemeColors.votingButton(isLight),
//					textColor = Color.Black,
//					textSize = 20.sp,
//					iconSize = 25.dp
//				)
//			}
//		}
		//end of voting

		// News Button above Additional Information dropdown
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = 8.dp),
			backgroundColor = Color(0xFFFFF3E0), // Pastel orange
			elevation = 8.dp,
			shape = RoundedCornerShape(16.dp)
		) {
			Surface(
				modifier = Modifier
					.fillMaxWidth()
					.clickable { onNavigate(PageIndex.NEWS) }
					.height(72.dp) // Match dropdown height
					.padding(horizontal = 20.dp),
				color = Color.Transparent,
				shape = RoundedCornerShape(16.dp)
			) {
				Row(
					modifier = Modifier
						.fillMaxWidth(),
					horizontalArrangement = Arrangement.Start, // Left align
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						text = TranslationManager.translate("help_news_title") + " \uD83D\uDEA8", // 🚨 emoji after text
						style = MaterialTheme.typography.h6.copy(
							fontWeight = FontWeight.Bold,
							color = ThemeColors.additionalColor, // Match dropdown text color
							fontSize = 20.sp // Match dropdown font size
						),
						textAlign = TextAlign.Start
					)
				}
			}
		}

		// Additional Information Section
		ExpandableCategory(
			title = TranslationManager.translate("additional_info"),
			expanded = additionalExpanded,
			onExpandedChange = { additionalExpanded = it },
			color = ThemeColors.additionalColor,
			backgroundColor = ThemeColors.additionalPastel
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.horizontalScroll(rememberScrollState())
					.padding(horizontal = 8.dp),
				horizontalArrangement = Arrangement.spacedBy(16.dp)
			) {
				NavButton(
					PageIndex.SOCIAL_MEDIA,
					TranslationManager.translate("social_media"),
					Res.drawable.content,
					onClick = { onNavigate(PageIndex.SOCIAL_MEDIA) },
					color = ThemeColors.additionalButton(isLight),
					textColor = Color.Black,
					textSize = 20.sp,
					iconSize = 25.dp
				)
				NavButton(
					PageIndex.APP_INFO,
					TranslationManager.translate("app_info"),
					Res.drawable.info_32,
					onClick = { onNavigate(PageIndex.APP_INFO) },
					color = ThemeColors.additionalButton(isLight),
					textColor = Color.Black,
					textSize = 20.sp,
					iconSize = 25.dp
				)
				NavButton(
					PageIndex.FAQ,
					TranslationManager.translate("faq_title"),
					Res.drawable.question,
					onClick = { onNavigate(PageIndex.FAQ) },
					color = ThemeColors.additionalButton(isLight),
					textColor = Color.Black,
					textSize = 20.sp,
					iconSize = 25.dp
				)
			}
		}

		Spacer(modifier = Modifier.height(24.dp))
	}

	// Confetti overlay when finished
	if (isFinished) {
		Fireworks()
		Fireworks()
	}
}

// Helper function to update countdown state
private suspend fun updateCountdownState(
	updateState: (Long, Long, Long, Long, Boolean) -> Unit
) {
	val now = Clock.System.now()
	val ukTimeZone = TimeZone.of("Europe/London")
	val currentDateTime = now.toLocalDateTime(ukTimeZone)
	
	// Check if we're in BST by comparing with UTC
	val utcDateTime = now.toLocalDateTime(TimeZone.UTC)
	val isBST = currentDateTime.hour != utcDateTime.hour

	// Get current year
	val currentYear = currentDateTime.year

	// Create target date for this year's November 1st
	// If in BST, target 5 PM UTC (6 PM BST)
	// If in GMT, target 6 PM UTC (6 PM GMT)
	val targetHour = if (isBST) 17 else 18
	var target = LocalDateTime(currentYear, 11, 1, targetHour, 0)
		.toInstant(ukTimeZone)

	// If we're past this year's carnival, set target to next year
	if (currentDateTime.month == kotlinx.datetime.Month.NOVEMBER && currentDateTime.dayOfMonth > 1) {
		target = LocalDateTime(currentYear + 1, 11, 1, targetHour, 0)
			.toInstant(ukTimeZone)
	}

	// Check if it's carnival day (November 1st) and after target time
	val isCarnivalDay = currentDateTime.month == kotlinx.datetime.Month.NOVEMBER &&
			currentDateTime.dayOfMonth == 1 &&
			currentDateTime.hour >= targetHour

	if (isCarnivalDay) {
		updateState(0, 0, 0, 0, true)
	} else {
		val duration = target - now
		updateState(
			duration.inWholeDays,
			duration.inWholeHours % 24,
			duration.inWholeMinutes % 60,
			duration.inWholeSeconds % 60,
			false
		)
	}
}

@Composable
private fun TimeUnit(value: Long, unit: String, color: Color, backgroundColor: Color) {
	var scale by remember { mutableStateOf(1f) }
	val infiniteTransition = rememberInfiniteTransition()
	val glowAlpha by infiniteTransition.animateFloat(
		initialValue = 0.3f,
		targetValue = 0.7f,
		animationSpec = infiniteRepeatable(
			animation = tween(1500),
			repeatMode = RepeatMode.Reverse
		)
	)

	// Animate number changes
	LaunchedEffect(value) {
		scale = 0.8f
		delay(50)
		scale = 1.1f
		delay(100)
		scale = 1f
	}

	Box(
		contentAlignment = Alignment.Center,
		modifier = Modifier
			.padding(horizontal = 2.dp)
			.width(65.dp)
			.height(85.dp)
			.drawWithCache {
				onDrawWithContent {
					drawContent()
					// Draw glowing effect
					drawCircle(
						color = color.copy(alpha = glowAlpha * 0.3f),
						radius = size.width * 0.8f,
						center = Offset(size.width / 2, size.height / 2)
					)
				}
			}
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			Text(
				text = value.toString().padStart(2, '0'),
				style = MaterialTheme.typography.h4.copy(
					fontWeight = FontWeight.Bold,
					fontSize = 32.sp,
					fontFamily = bungeeFont
				),
				color = color,
				modifier = Modifier.scale(scale)
			)
			Text(
				text = unit,
				style = MaterialTheme.typography.caption.copy(
					fontSize = 9.sp,
					fontWeight = FontWeight.Medium
				),
				color = color.copy(alpha = 0.8f)
			)
		}
	}
}

@Composable
private fun AnimatedBorder(
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit
) {
	val infiniteTransition = rememberInfiniteTransition()
	val angle by infiniteTransition.animateFloat(
		initialValue = 0f,
		targetValue = 360f,
		animationSpec = infiniteRepeatable(
			animation = tween(3000, easing = LinearEasing),
			repeatMode = RepeatMode.Restart
		)
	)

	val sparkleAlpha by infiniteTransition.animateFloat(
		initialValue = 0f,
		targetValue = 1f,
		animationSpec = infiniteRepeatable(
			animation = tween(1000),
			repeatMode = RepeatMode.Reverse
		)
	)

	Box(
		modifier = modifier
			.drawWithCache {
				val path = Path()
				val strokeWidth = 4.dp.toPx()
				val cornerRadius = 16.dp.toPx()

				path.addRoundRect(
					RoundRect(
						left = strokeWidth / 2,
						top = strokeWidth / 2,
						right = size.width - strokeWidth / 2,
						bottom = size.height - strokeWidth / 2,
						cornerRadius = CornerRadius(cornerRadius)
					)
				)

				onDrawWithContent {
					drawContent()

					// Draw the animated border
					val gradientColors = listOf(
						Color(0xFF00BCD4), // Cyan
						Color(0xFF3F51B5), // Indigo
						Color(0xFF9C27B0), // Purple
						Color(0xFF00BCD4)  // Cyan again for seamless loop
					)

					// Calculate rotating gradient start and end points
					val radius = maxOf(size.width, size.height)
					val angleInRadians = angle * (kotlin.math.PI / 180f).toFloat()

					val startX = size.width / 2 + radius * kotlin.math.cos(angleInRadians.toDouble()).toFloat()
					val startY = size.height / 2 + radius * kotlin.math.sin(angleInRadians.toDouble()).toFloat()
					val endX = size.width / 2 + radius * kotlin.math.cos((angleInRadians + kotlin.math.PI.toFloat()).toDouble()).toFloat()
					val endY = size.height / 2 + radius * kotlin.math.sin((angleInRadians + kotlin.math.PI.toFloat()).toDouble()).toFloat()

					drawPath(
						path = path,
						brush = Brush.linearGradient(
							colors = gradientColors,
							start = Offset(startX, startY),
							end = Offset(endX, endY)
						),
						style = Stroke(
							width = strokeWidth,
							cap = StrokeCap.Round,
							join = StrokeJoin.Round
						)
					)

					// Draw sparkles at corners
					val sparkleRadius = 4.dp.toPx()
					val sparkleColor = Color(0xFFFFD700).copy(alpha = sparkleAlpha)
					val corners = listOf(
						Offset(strokeWidth, strokeWidth),
						Offset(size.width - strokeWidth, strokeWidth),
						Offset(size.width - strokeWidth, size.height - strokeWidth),
						Offset(strokeWidth, size.height - strokeWidth)
					)
					corners.forEach { corner ->
						drawCircle(
							color = sparkleColor,
							radius = sparkleRadius,
							center = corner
						)
					}
				}
			}
	) {
		content()
	}
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun CountdownSection(
	days: Long,
	hours: Long,
	minutes: Long,
	seconds: Long,
	isFinished: Boolean
) {
	val scale by animateFloatAsState(
		targetValue = if (isFinished) 1.2f else 1f,
		animationSpec = repeatable(
			iterations = if (isFinished) Int.MAX_VALUE else 0,
			animation = tween(durationMillis = 200),
			repeatMode = RepeatMode.Reverse
		)
	)

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		AnimatedContent(
			targetState = isFinished,
			transitionSpec = {
				fadeIn() + scaleIn() with fadeOut() + scaleOut()
			}
		) { finished ->
			if (finished) {
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 16.dp),
					contentAlignment = Alignment.Center
				) {
					Text(
						text = TranslationManager.translate("carnival_started"),
						style = MaterialTheme.typography.h5.copy(
							fontWeight = FontWeight.Bold,
							fontSize = 20.sp,
							fontFamily = bungeeFont
						),
						color = MaterialTheme.colors.primary,
						modifier = Modifier.scale(scale)
					)
				}
			} else {
				Column(
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center,
					modifier = Modifier.padding(vertical = 8.dp)
				) {
					Text(
						text = TranslationManager.translate("parade_starts_in"),
						style = MaterialTheme.typography.h6.copy(
							fontWeight = FontWeight.Bold,
							fontSize = 20.sp,
							fontFamily = bungeeFont
						),
						color = MaterialTheme.colors.primary,
						modifier = Modifier.padding(bottom = 8.dp)
					)

					Row(
						horizontalArrangement = Arrangement.Center,
						modifier = Modifier
							.fillMaxWidth()
							.padding(horizontal = 8.dp, vertical = 8.dp)
					) {
						TimeUnit(
							value = days,
							unit = TranslationManager.translate("days"),
							color = Color(0xFF2196F3),
							backgroundColor = Color.Transparent
						)
						TimeUnit(
							value = hours,
							unit = TranslationManager.translate("hours"),
							color = Color(0xFFE91E63),
							backgroundColor = Color.Transparent
						)
						TimeUnit(
							value = minutes,
							unit = TranslationManager.translate("minutes"),
							color = Color(0xFF4CAF50),
							backgroundColor = Color.Transparent
						)
						TimeUnit(
							value = seconds,
							unit = TranslationManager.translate("seconds"),
							color = Color(0xFFFF9800),
							backgroundColor = Color.Transparent
						)
					}
				}
			}
		}

		Spacer(modifier = Modifier.height(12.dp))
	}
}

@Composable
fun Fireworks() {
	val particles = remember { List(80) { FireworkParticle() } }
	val animatable = remember { Animatable(0f) }

	LaunchedEffect(Unit) {
		animatable.animateTo(
			targetValue = 1f,
			animationSpec = tween(3000, easing = LinearEasing)
		)
	}

	Canvas(modifier = Modifier.fillMaxSize()) {
		particles.forEach { particle ->
			drawFireworkParticle(particle, animatable.value)
		}
	}
}

class FireworkParticle {
	val color = listOf(
		Color(0xFFE57373), // Red
		Color(0xFF81C784), // Green
		Color(0xFF64B5F6), // Blue
		Color(0xFFFFB74D), // Orange
		Color(0xFFBA68C8), // Purple
		Color(0xFFFFD54F)  // Yellow
	).random()

	val startX = Random.nextFloat()
	val startY = Random.nextFloat()
	val size = Random.nextFloat() * 15f + 5f
	val speedX = Random.nextFloat() * 2f - 1f
	val speedY = Random.nextFloat() * 2f + 1f
	val rotation = Random.nextFloat() * 360f
	val rotationSpeed = Random.nextFloat() * 360f - 180f
}

private fun DrawScope.drawFireworkParticle(particle: FireworkParticle, progress: Float) {
	val x = (particle.startX * size.width) + (particle.speedX * size.width * progress)
	val y = (particle.startY * size.height) + (particle.speedY * size.height * progress)
	val rotation = particle.rotation + (particle.rotationSpeed * progress)

	rotate(rotation, Offset(x, y)) {
		drawCircle(
			color = particle.color.copy(alpha = 1f - progress),
			radius = particle.size * (1 - progress)
		)
	}
}

@Composable
private fun ExpandableCategory(
	title: String,
	expanded: Boolean,
	onExpandedChange: (Boolean) -> Unit,
	color: Color,
	backgroundColor: Color,
	content: @Composable () -> Unit
) {
	val elevation = if (expanded) 8.dp else 4.dp
	val scale = if (expanded) 1.02f else 1f
	val scaleState = remember { Animatable(1f) }
	
	LaunchedEffect(expanded) {
		scaleState.animateTo(
			targetValue = scale,
			animationSpec = spring(
				dampingRatio = Spring.DampingRatioMediumBouncy,
				stiffness = Spring.StiffnessLow
			)
		)
	}

	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp)
			.scale(scaleState.value),
		elevation = elevation,
		backgroundColor = backgroundColor,
		shape = RoundedCornerShape(16.dp)
	) {
		Column {
			Surface(
				modifier = Modifier
					.fillMaxWidth()
					.clickable { onExpandedChange(!expanded) },
				color = backgroundColor,
				shape = RoundedCornerShape(16.dp)
			) {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.height(72.dp)  // Increased height for better touch target
						.padding(horizontal = 20.dp),  // Increased horizontal padding
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						text = title,
						style = MaterialTheme.typography.h6.copy(
							fontWeight = FontWeight.Bold,
							color = color,
							fontSize = 20.sp  // Increased font size
						)
					)
					Icon(
						imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
						contentDescription = if (expanded) "Collapse" else "Expand",
						tint = color,
						modifier = Modifier.size(32.dp)  // Increased icon size
					)
				}
			}
			
			AnimatedVisibility(
				visible = expanded,
				enter = expandVertically(
					animationSpec = spring(
						dampingRatio = Spring.DampingRatioMediumBouncy,
						stiffness = Spring.StiffnessLow
					)
				) + fadeIn(),
				exit = shrinkVertically(
					animationSpec = spring(
						dampingRatio = Spring.DampingRatioMediumBouncy,
						stiffness = Spring.StiffnessLow
					)
				) + fadeOut()
			) {
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.padding(20.dp)  // Increased padding
				) {
					content()
				}
			}
		}
	}
}