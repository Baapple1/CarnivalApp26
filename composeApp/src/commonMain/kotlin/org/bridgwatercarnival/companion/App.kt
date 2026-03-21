package org.bridgwatercarnival.companion

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import bridgwatercarnival.composeapp.generated.resources._2025CarnivalBanner
import bridgwatercarnival.composeapp.generated.resources.Res
import bridgwatercarnival.composeapp.generated.resources.AllOut
import bridgwatercarnival.composeapp.generated.resources.TwentyFiveBanner
import bridgwatercarnival.composeapp.generated.resources.info_32
import bridgwatercarnival.composeapp.generated.resources.map_32
import bridgwatercarnival.composeapp.generated.resources.settings_32
import bridgwatercarnival.composeapp.generated.resources.store_32
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.bridgwatercarnival.companion.firebase.FirebaseInit
import org.bridgwatercarnival.companion.pages.AppInfo
import org.bridgwatercarnival.companion.pages.DaytimeEntertainment
import org.bridgwatercarnival.companion.pages.EstimatedTime
import org.bridgwatercarnival.companion.pages.FAQ
import org.bridgwatercarnival.companion.pages.FirstTimeVisitorGuide
import org.bridgwatercarnival.companion.pages.Gallery
import org.bridgwatercarnival.companion.pages.Gallery2023
import org.bridgwatercarnival.companion.pages.Gallery2024
import org.bridgwatercarnival.companion.pages.Gallery2025
import org.bridgwatercarnival.companion.pages.Gallery2026
import org.bridgwatercarnival.companion.pages.Help
import org.bridgwatercarnival.companion.pages.News
import org.bridgwatercarnival.companion.pages.Parking
import org.bridgwatercarnival.companion.pages.Planner
import org.bridgwatercarnival.companion.pages.Results
import org.bridgwatercarnival.companion.pages.RoadClosure
import org.bridgwatercarnival.companion.pages.Settings
import org.bridgwatercarnival.companion.pages.SocialMedia
import org.bridgwatercarnival.companion.pages.Squibbing
import org.bridgwatercarnival.companion.pages.Videos
import org.bridgwatercarnival.companion.pages.Voting
import org.bridgwatercarnival.companion.pages.store.Store
import org.bridgwatercarnival.companion.theme.bungeeFont
import org.bridgwatercarnival.companion.util.Storage
import org.bridgwatercarnival.companion.util.StorageKeys
import org.bridgwatercarnival.companion.util.TranslatedText
import org.bridgwatercarnival.companion.util.TranslationManager
import org.bridgwatercarnival.companion.util.TranslationProvider
import org.bridgwatercarnival.companion.util.getPlatform
import org.bridgwatercarnival.companion.util.getStorageInitializer
import org.bridgwatercarnival.companion.viewmodel.VotingViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

enum class PageIndex(val subPage: Boolean = false) {
	EXPLORE,
	EXPLORE_ROUTE,
	MAP,
	STORE,
	SETTINGS,
	GALLERY,
	SQUIBBING(true),
	PARKING(true),
	ESTIMATED_TIME(true),
	DAYTIME_ENTERTAINMENT(true),
	ROAD_CLOSURE(true),
	FIRST_TIME_VISITOR_GUIDE(true),
	GALLERY2023(true),
	GALLERY2024(true),
	GALLERY2025(true),
	GALLERY2026(true),
	SOCIAL_MEDIA(true),
	APP_INFO(true),
	VOTING(true),
	VOTINGRESULTS(true),
	PLANNER(true),
	NEWS(true),
	VIDEOS(true),
	CHATBOT(true),
	FAQ(true)
}

const val VERSION_NUMBER = "1.1"
const val CONTACT_EMAIL = "enquiries@bridgwatercarnival.org.uk"
val brightColor = Color(0xFFFEB7D00)

@Composable
expect fun CarnivalMap()

@Composable
fun App(navController: NavHostController = rememberNavController()) {
	val scope = rememberCoroutineScope()
	val platform = getPlatform()

	// Initialize storage
	getStorageInitializer().initialize()

	// Initialize Firebase only once when the app starts
	LaunchedEffect(Unit) {
		scope.launch {
			FirebaseInit.initializeFirebase()
		}
	}

	// Initialize translations
	val translationManager = remember {
		TranslationManager.apply {
			// Get system language and set it
			val systemLanguage = platform.language
			setLanguage(systemLanguage)

			// Save the initial language preference
			Storage.saveString(StorageKeys.LANGUAGE_PREFERENCE, currentLanguage)
		}
	}

	// Listen for system language changes
	LaunchedEffect(platform.language) {
		translationManager.setLanguage(platform.language)
		Storage.saveString(StorageKeys.LANGUAGE_PREFERENCE, translationManager.currentLanguage)
		translationManager.forceUpdate() // Force UI update
	}

	// Listen for manual language changes
	LaunchedEffect(translationManager.currentLanguage) {
		Storage.saveString(StorageKeys.LANGUAGE_PREFERENCE, translationManager.currentLanguage)
		translationManager.forceUpdate() // Force UI update
	}

	// Toggle Welcome Dialog
	var showWelcomeDialog by remember { mutableStateOf(false) }
	val backStackEntry by navController.currentBackStackEntryAsState()

	val colorsLight = lightColors(
		primary = Color(0xFFd32f2f),
		primaryVariant = Color(0xFFe57373),
		onPrimary = Color(0xFFFAFAFA),
		secondary = Color(0xFFFFC107),
		secondaryVariant = Color(0xFFFFD54F),
		onSecondary = Color(0xFF212121),
		background = Color(0xFFFDFDFD),
		onBackground = Color(0xFF212121),
		surface = Color(0xFFFFE082),
		onSurface = Color(0xFF212121)
	)

	val colorsDark = darkColors(
		primary = Color(0xFFFF5252),
		primaryVariant = Color(0xFFFF8A80),
		onPrimary = Color(0xFF121212),
		secondary = Color(0xFFFFC107),
		secondaryVariant = Color(0xFFFFD54F),
		onSecondary = Color(0xFF121212),
		background = Color(0xFF121212),
		onBackground = Color(0xFFF5F5F5),
		surface = Color(0xFF424242),
		onSurface = Color(0xFFF5F5F5)
	)

	// Show Welcome Dialog
	if (showWelcomeDialog) {
		WelcomeDialog(onDismiss = { showWelcomeDialog = false })
	}

	// Navigation Button for Bottom Navigation
	@Composable
	fun RowScope.NavButton(page: PageIndex, text: String, icon: DrawableResource) {
		val isSelected = backStackEntry?.destination?.hierarchy?.any { it.route == page.name } == true
		val scale = remember { Animatable(1f) }

		LaunchedEffect(isSelected) {
			if (isSelected) {
				scale.animateTo(
					targetValue = 1.2f,
					animationSpec = spring(
						dampingRatio = 0.6f,
						stiffness = 300f
					)
				)
				scale.animateTo(
					targetValue = 1f,
					animationSpec = spring(
						dampingRatio = 0.6f,
						stiffness = 300f
					)
				)
			}
		}

		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.weight(1f)
				.padding(vertical = 4.dp)
				.clickable {
					// Navigate if not already on the selected page
					if (backStackEntry?.destination?.route != page.name) {
						navController.navigate(page.name) {
							launchSingleTop = true
							restoreState = true
							popUpTo(navController.graph.findStartDestination().route!!) {
								saveState = true
								inclusive = false
							}
						}
					}
				}
		) {
			// Selection indicator at top
			if (isSelected) {
				Box(
					modifier = Modifier
						.width(32.dp)
						.height(2.dp)
						.background(
							Color.White,
							RoundedCornerShape(1.dp)
						)
				)
			} else {
				Spacer(modifier = Modifier.height(2.dp))
			}

			Spacer(modifier = Modifier.height(4.dp))

			Box(
				modifier = Modifier
					.size(48.dp)
					.graphicsLayer {
						scaleX = if (isSelected) scale.value else 1f
						scaleY = if (isSelected) scale.value else 1f
					}
			) {
				// Background glow
				if (isSelected) {
					Box(
						modifier = Modifier
							.matchParentSize()
							.background(
								color = MaterialTheme.colors.primary.copy(alpha = 0.2f),
								shape = RoundedCornerShape(12.dp)
							)
					)
				}

				Icon(
					painter = painterResource(icon),
					contentDescription = text,
					modifier = Modifier
						.size(28.dp)
						.align(Alignment.Center),
					tint = if (isSelected) MaterialTheme.colors.primary else Color.White.copy(alpha = 0.7f)
				)
			}

			Spacer(modifier = Modifier.height(4.dp))

			TranslatedText(
				key = text,
				style = MaterialTheme.typography.caption.copy(
					color = if (isSelected) MaterialTheme.colors.primary else Color.White.copy(alpha = 0.7f),
					fontSize = 12.sp,
					letterSpacing = 0.5.sp
				)
			)
		}
	}

	TranslationProvider {
		MaterialTheme(colors = if (isSystemInDarkTheme()) colorsDark else colorsLight) {
			Box(
				modifier = Modifier.fillMaxSize()
			) {
				Scaffold(
					modifier = Modifier.fillMaxSize(),
					topBar = {
						TopAppBar()
					},
					bottomBar = {
						BottomNavigation(
							modifier = Modifier
								.fillMaxWidth()
								.height(90.dp),
							backgroundColor = Color(0xFF121212),
							contentColor = Color.White,
							elevation = 0.dp
						) {
							NavButton(PageIndex.EXPLORE, "explore", Res.drawable.info_32)
							NavButton(PageIndex.MAP, "map", Res.drawable.map_32)
							//NavButton(PageIndex.STORE, "store", Res.drawable.store_32)
							NavButton(PageIndex.SETTINGS, "settings", Res.drawable.settings_32)
						}
					}
				) { paddingValues ->
					NavHost(
						navController = navController,
						startDestination = PageIndex.MAP.name,
						enterTransition = { EnterTransition.None },
						exitTransition = { ExitTransition.None },
						modifier = Modifier
							.fillMaxSize()
							.padding(paddingValues)
					) {
						composable(route = PageIndex.SETTINGS.name) { Settings(onNavigateBack = { /* Handle navigation back */ }) }
						composable(route = PageIndex.MAP.name) { CarnivalMap() }
						composable(route = PageIndex.STORE.name) { Store() }

						navigation(
							startDestination = PageIndex.EXPLORE.name,
							route = PageIndex.EXPLORE_ROUTE.name
						) {
							composable(route = PageIndex.EXPLORE.name) {
								Help { newPage ->
									navController.navigate(newPage.name)
								}
							}

							composable(route = PageIndex.APP_INFO.name) { AppInfo(navController) }
							composable(route = PageIndex.SQUIBBING.name) { Squibbing(navController) }
							composable(route = PageIndex.PARKING.name) { Parking(navController) }
							composable(route = PageIndex.ESTIMATED_TIME.name) { EstimatedTime(navController) }
							composable(route = PageIndex.DAYTIME_ENTERTAINMENT.name) { DaytimeEntertainment(navController) }
							composable(route = PageIndex.ROAD_CLOSURE.name) { RoadClosure(navController) }
							composable(route = PageIndex.FIRST_TIME_VISITOR_GUIDE.name) { FirstTimeVisitorGuide(navController) }
							composable(route = PageIndex.GALLERY2023.name) { Gallery2023(navController) }
							composable(route = PageIndex.GALLERY.name) { Gallery(navController) }
							composable(route = PageIndex.GALLERY2025.name) { Gallery2025(navController) }
							composable(route = PageIndex.GALLERY2026.name) { Gallery2026(navController) }
							composable(route = PageIndex.NEWS.name) { News(navController) }
							composable(route = PageIndex.FAQ.name) { FAQ(navController) }

							composable(route = PageIndex.SOCIAL_MEDIA.name) { SocialMedia(navController) }
							composable(route = PageIndex.GALLERY2024.name) { Gallery2024(navController) }
							composable(route = PageIndex.VIDEOS.name) { Videos() }
							composable(route = PageIndex.VOTING.name) {
								Voting(
									viewModel = remember { VotingViewModel() },
									navController = navController
								)
							}
							composable(route = PageIndex.VOTINGRESULTS.name) {
								Results(
									onNavigateBack = {
										navController.navigateUp()
									}
								)
							}
							composable(route = PageIndex.PLANNER.name) {
								Planner(navController)
							}
							composable(route = PageIndex.CHATBOT.name) {

							}
						}
					}
				}
			}
		}
	}
}

@Composable
fun TopAppBar() {
	Column(
		Modifier
			.fillMaxWidth()
			.background(Color.Black)
			.padding(top = getBannerTopPadding()),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Image(
			painter = painterResource(Res.drawable._2025CarnivalBanner),
			contentDescription = "Bridgwater Carnival Banner",
			contentScale = ContentScale.FillWidth,
			modifier = Modifier
				.fillMaxWidth()
				.height(getBannerHeight())
		)
	}
}

@Composable
fun TimeUnitWithCircle(
	days: Long,
	hours: Long,
	minutes: Long,
	modifier: Modifier = Modifier
) {
	// Create animated color transition
	val infiniteTransition = rememberInfiniteTransition()
	val circleColor by infiniteTransition.animateColor(
		initialValue = Color(0xFF2196F3),
		targetValue = Color(0xFF00BCD4),
		animationSpec = infiniteRepeatable(
			animation = tween(3000, easing = LinearEasing),
			repeatMode = RepeatMode.Reverse
		)
	)

	// Create pulsing animation
	val scale by infiniteTransition.animateFloat(
		initialValue = 1f,
		targetValue = 1.05f,
		animationSpec = infiniteRepeatable(
			animation = tween(1000, easing = FastOutSlowInEasing),
			repeatMode = RepeatMode.Reverse
		)
	)

	// Create rotating sparkles animation
	val rotation by infiniteTransition.animateFloat(
		initialValue = 0f,
		targetValue = 360f,
		animationSpec = infiniteRepeatable(
			animation = tween(10000, easing = LinearEasing)
		)
	)

	Box(
		modifier = modifier
			.width(240.dp)
			.height(160.dp),
		contentAlignment = Alignment.Center
	) {
		// Animated progress based on total minutes
		val progress = remember { Animatable(0f) }
		val totalMinutesInYear = 525600f
		val currentTotalMinutes = (days * 24 * 60 + hours * 60 + minutes).toFloat()

		LaunchedEffect(days, hours, minutes) {
			progress.animateTo(
				targetValue = 1f - (currentTotalMinutes / totalMinutesInYear),
				animationSpec = tween(1500, easing = FastOutSlowInEasing)
			)
		}

		// Draw sparkles
		Canvas(
			modifier = Modifier
				.size(200.dp)
				.graphicsLayer {
					rotationZ = rotation
				}
		) {
			val radius = size.minDimension / 2
			repeat(8) { index ->
				val angle = (index * (360f / 8) + rotation) * (PI / 180f)
				val x = center.x + cos(angle).toFloat() * (radius - 20.dp.toPx())
				val y = center.y + sin(angle).toFloat() * (radius - 20.dp.toPx())

				drawCircle(
					color = circleColor.copy(alpha = 0.3f),
					radius = 4.dp.toPx(),
					center = Offset(x, y)
				)
			}
		}

		// Main circle with progress
		Canvas(
			modifier = Modifier
				.size(160.dp)
				.graphicsLayer {
					scaleX = scale
					scaleY = scale
				}
		) {
			// Glowing background circle
			drawCircle(
				color = circleColor.copy(alpha = 0.15f),
				radius = size.minDimension / 2,
				style = Fill
			)

			// Background circle
			drawArc(
				color = circleColor.copy(alpha = 0.2f),
				startAngle = 0f,
				sweepAngle = 360f,
				useCenter = false,
				style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
			)

			// Progress arc with gradient
			drawArc(
				color = circleColor,
				startAngle = -90f,
				sweepAngle = 360f * progress.value,
				useCenter = false,
				style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
			)
		}

		// Center text
		if (days <= 0 && hours <= 0 && minutes <= 0) {
			Text(
				text = "it's\nCarnival Day🎉",
				style = MaterialTheme.typography.h6.copy(
					fontWeight = FontWeight.ExtraBold,
					textAlign = TextAlign.Center,
					fontFamily = bungeeFont,
					fontSize = 13.sp,
					lineHeight = 26.sp,
					shadow = Shadow(
						color = circleColor.copy(alpha = 0.3f),
						offset = Offset(2f, 2f),
						blurRadius = 4f
					)
				),
				color = circleColor,
				modifier = Modifier
					.graphicsLayer {
						scaleX = scale
						scaleY = scale
					}
			)
		} else {
			// Time text with pulsing effect
			Text(
				text = "${abs(days)}d ${abs(hours)}h\n${abs(minutes)}m",
				style = MaterialTheme.typography.h6.copy(
					fontWeight = FontWeight.Bold,
					textAlign = TextAlign.Center,
					fontFamily = bungeeFont
				),
				color = circleColor,
				modifier = Modifier
					.graphicsLayer {
						scaleX = scale
						scaleY = scale
					}
			)
		}
	}
}

@Composable
fun WelcomeDialog(onDismiss: () -> Unit) {
	// Get current time in London timezone
	val now = Clock.System.now()
	val ukTimeZone = TimeZone.of("Europe/London")
	val currentDateTime = now.toLocalDateTime(ukTimeZone)
	
	// Get current year
	val currentYear = currentDateTime.year

	// Create target date for this year's November 1st at midnight
	var target = LocalDateTime(currentYear, 11, 1, 0, 0)
		.toInstant(ukTimeZone)

	// If we're past this year's carnival, set target to next year
	if (currentDateTime.month == Month.NOVEMBER && currentDateTime.dayOfMonth > 1) {
		target = LocalDateTime(currentYear + 1, 11, 1, 0, 0)
			.toInstant(ukTimeZone)
	}

	// Calculate time until carnival
	val duration = target - now
	val days = duration.inWholeDays
	val hours = duration.inWholeHours % 24
	val minutes = duration.inWholeMinutes % 60

	// Check if it's carnival day (November 1st)
	val isCarnivalDay = currentDateTime.month == Month.NOVEMBER && 
			currentDateTime.dayOfMonth == 1

	AlertDialog(
		onDismissRequest = onDismiss,
		title = {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 4.dp),
				contentAlignment = Alignment.Center
			) {
				Column(
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.spacedBy(2.dp)
				) {
					Text(
						text = "🎭",
						fontSize = 28.sp,
						modifier = Modifier.padding(bottom = 1.dp)
					)
					Text(
						text = "Welcome to the\nBridgwater Carnival App",
						style = MaterialTheme.typography.h6.copy(
							color = Color(0xFF2979FF),
							fontSize = 20.sp,
							textAlign = TextAlign.Center,
							fontFamily = bungeeFont,
							shadow = Shadow(
								color = Color.Black.copy(alpha = 0.15f),
								offset = Offset(1f, 1f),
								blurRadius = 2f
							)
						)
					)
				}
			}
		},
		text = {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp, vertical = 2.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				// Countdown section
				Column(
					modifier = Modifier.fillMaxWidth(),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.spacedBy(2.dp)
				) {
					Text(
						text = if (isCarnivalDay) "Today is Carnival Day!" else "Carnival Day starts in:",
						style = MaterialTheme.typography.body1.copy(
							color = Color(0xFF1A237E),
							fontSize = 18.sp,
							textAlign = TextAlign.Center
						)
					)
					Box(
						modifier = Modifier.padding(vertical = 8.dp),
						contentAlignment = Alignment.Center
					) {
						TimeUnitWithCircle(
							days = if (isCarnivalDay) 0 else abs(days),
							hours = if (isCarnivalDay) 0 else abs(hours),
							minutes = if (isCarnivalDay) 0 else abs(minutes)
						)
					}
				}

				// Features section
				Column(
					verticalArrangement = Arrangement.spacedBy(4.dp)
				) {
					Text(
						text = "Plan your visit now with our features:",
						style = MaterialTheme.typography.subtitle1.copy(
							color = Color(0xFF1A237E),
							fontWeight = FontWeight.Bold,
							fontSize = 16.sp
						),
						modifier = Modifier.padding(bottom = 1.dp)
					)
					listOf(
						"🗺️ Interactive carnival route map",
						//"🎭 Live voting from 10pm on carnival days",
						"🚗 Parking information",
						"📅 Event schedule & updates",
						"🎪 Entertainment guide",
						"📍 Key locations & facilities",
						"🚨 Latest News"
					).forEach { feature ->
						Row(
							modifier = Modifier
								.fillMaxWidth()
								.padding(start = 4.dp),
							horizontalArrangement = Arrangement.Start,
							verticalAlignment = Alignment.CenterVertically
						) {
							Text(
								text = feature,
								style = MaterialTheme.typography.body1.copy(
									color = Color(0xFF1A237E),
									fontSize = 14.sp,
									fontWeight = FontWeight.Medium
								)
							)
						}
					}
				}
			}
		},
		confirmButton = {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp, vertical = 8.dp),
				contentAlignment = Alignment.Center
			) {
				Button(
					onClick = onDismiss,
					modifier = Modifier
						.fillMaxWidth()
						.height(48.dp),
					colors = ButtonDefaults.buttonColors(
						backgroundColor = Color(0xFF2979FF),
						contentColor = Color.White
					),
					shape = RoundedCornerShape(24.dp),
					elevation = ButtonDefaults.elevation(
						defaultElevation = 4.dp,
						pressedElevation = 8.dp
					)
				) {
					Text(
						text = "Get Started",
						style = MaterialTheme.typography.button.copy(
							fontFamily = bungeeFont,
							fontSize = 18.sp,
							fontWeight = FontWeight.Bold,
							shadow = Shadow(
								color = Color.Black.copy(alpha = 0.2f),
								offset = Offset(1f, 1f),
								blurRadius = 2f
							)
						)
					)
				}
			}
		}
	)
}
