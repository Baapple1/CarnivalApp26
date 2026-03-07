package org.bridgwatercarnival.companion.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bridgwatercarnival.composeapp.generated.resources.Res
import bridgwatercarnival.composeapp.generated.resources.clock
import bridgwatercarnival.composeapp.generated.resources.england
import bridgwatercarnival.composeapp.generated.resources.flag
import bridgwatercarnival.composeapp.generated.resources.france
import bridgwatercarnival.composeapp.generated.resources.german
import bridgwatercarnival.composeapp.generated.resources.italian
import org.bridgwatercarnival.companion.util.TranslationManager
import org.bridgwatercarnival.companion.util.TranslatedText
import org.jetbrains.compose.resources.painterResource
import org.bridgwatercarnival.companion.util.Storage
import org.bridgwatercarnival.companion.util.StorageKeys
import org.bridgwatercarnival.companion.notifications.NotificationService
import org.bridgwatercarnival.companion.notifications.getNotificationService
import org.bridgwatercarnival.companion.theme.bungeeFont
import org.bridgwatercarnival.companion.util.PlatformInfo

@Composable
fun Settings(onNavigateBack: () -> Unit) {
	var expanded by remember { mutableStateOf(false) }
	var showDisableDialog by remember { mutableStateOf(false) }
	var showNotificationTypes by remember { mutableStateOf(false) }
	val currentLanguage = TranslationManager.currentLanguage
	val languages = listOf("en", "fr", "es", "de", "it")
	var isDarkMode by remember { mutableStateOf(false) }
	val notificationService: NotificationService = getNotificationService()

	// Use rememberSaveable to persist state across recompositions and configuration changes
	var notificationsEnabled by rememberSaveable {
		mutableStateOf(notificationService.isNotificationPermissionGranted())
	}
	var countdownEnabled by rememberSaveable {
		mutableStateOf(Storage.getBoolean(StorageKeys.COUNTDOWN_NOTIFICATIONS_ENABLED, notificationsEnabled))
	}
	var votingEnabled by rememberSaveable {
		mutableStateOf(Storage.getBoolean(StorageKeys.VOTING_NOTIFICATIONS_ENABLED, false))
	}
	var resultsEnabled by rememberSaveable {
		mutableStateOf(Storage.getBoolean(StorageKeys.RESULTS_NOTIFICATIONS_ENABLED, false))
	}
	var isRequestingPermission by rememberSaveable { mutableStateOf(false) }

	// Only update notification states when explicitly changed by user
	LaunchedEffect(notificationsEnabled, countdownEnabled, votingEnabled, resultsEnabled) {
		// This effect will only run when the user explicitly changes these values
		// It won't run on theme changes or other recompositions
	}

	// Handle permission request when explicitly triggered
	LaunchedEffect(isRequestingPermission) {
		if (isRequestingPermission) {
			notificationService.requestNotificationPermission()
			// Wait for permission dialog to complete
			kotlinx.coroutines.delay(1000)
			val newPermissionState = notificationService.isNotificationPermissionGranted()
			notificationsEnabled = newPermissionState
			countdownEnabled = newPermissionState
			isRequestingPermission = false
		}
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		// Header
		Column(
			modifier = Modifier.fillMaxWidth(),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				text = TranslationManager.translate("settings"),
				modifier = Modifier.padding(bottom = 10.dp),
				style = MaterialTheme.typography.h4.copy(
					color = MaterialTheme.colors.onBackground,
					fontWeight = FontWeight.Bold,
					fontFamily = bungeeFont
				),
				textAlign = TextAlign.Center
			)
			Divider(
				color = Color.Gray,
				modifier = Modifier.fillMaxWidth()
			)
			Spacer(modifier = Modifier.height(16.dp))
		}

		// Language Selection Card
		Card(
			modifier = Modifier.fillMaxWidth(),
			backgroundColor = Color.White,
			elevation = 4.dp,
			shape = RoundedCornerShape(16.dp)
		) {
			Column(
				modifier = Modifier.padding(16.dp),
				verticalArrangement = Arrangement.spacedBy(24.dp)
			) {
				// Language Section
				Column {
					Text(
						text = TranslationManager.translate("selected_language"),
						style = MaterialTheme.typography.h6.copy(
							fontSize = 20.sp,
							color = Color(0xFF2196F3)
						),
						modifier = Modifier.fillMaxWidth()
					)

					Spacer(modifier = Modifier.height(8.dp))

					Button(
						onClick = { expanded = true },
						modifier = Modifier.fillMaxWidth(),
						colors = ButtonDefaults.buttonColors(
							backgroundColor = Color(0xFFE3F2FD)
						),
						shape = RoundedCornerShape(24.dp)
					) {
						Row(
							modifier = Modifier.fillMaxWidth(),
							horizontalArrangement = Arrangement.Start,
							verticalAlignment = Alignment.CenterVertically
						) {
							val flagRes = when (currentLanguage) {
								"en" -> Res.drawable.england
								"fr" -> Res.drawable.france
								"es" -> Res.drawable.flag
								"de" -> Res.drawable.german
								"it" -> Res.drawable.italian
								else -> Res.drawable.england
							}

							Image(
								painter = painterResource(flagRes),
								contentDescription = TranslationManager.translate("language_flag"),
								modifier = Modifier.size(24.dp)
							)

							Spacer(modifier = Modifier.width(12.dp))

							Text(
								text = TranslationManager.translate(when (currentLanguage) {
									"en" -> "english"
									"fr" -> "french"
									"es" -> "spanish"
									"de" -> "german"
									"it" -> "italian"
									else -> "english"
								}),
								color = Color(0xFF2196F3),
								style = MaterialTheme.typography.body1.copy(fontSize = 18.sp)
							)
						}
					}
				}

				Divider(color = Color.LightGray, thickness = 1.dp)

				// Notifications Section
				Column(
					verticalArrangement = Arrangement.spacedBy(8.dp)
				) {
					Text(
						text = TranslationManager.translate("notifications"),
						style = MaterialTheme.typography.h6.copy(
							fontSize = 20.sp,
							color = Color(0xFF2196F3)
						)
					)

					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically
					) {
						Text(
							text = TranslationManager.translate("enable_notifications"),
							style = MaterialTheme.typography.body1,
							color = Color.Black
						)
						Switch(
							checked = notificationsEnabled,
							onCheckedChange = { isEnabled ->
								if (!isEnabled) {
									showDisableDialog = true
								} else {
									if (PlatformInfo.isIOS) {
										// For iOS, open settings to enable notifications
										notificationService.openNotificationSettings()
									} else {
										// For Android, request permissions normally
										isRequestingPermission = true
									}
								}
							},
							colors = SwitchDefaults.colors(
								checkedThumbColor = Color(0xFF2196F3),
								checkedTrackColor = Color(0xFF90CAF9),
								uncheckedThumbColor = Color.Gray,
								uncheckedTrackColor = Color.LightGray
							)
						)
					}

					if (notificationsEnabled) {
						Row(
							modifier = Modifier.fillMaxWidth(),
							horizontalArrangement = Arrangement.SpaceBetween,
							verticalAlignment = Alignment.CenterVertically
						) {
							Text(
								text = TranslationManager.translate("countdown_notifications"),
								style = MaterialTheme.typography.body1,
								color = Color.Black
							)
							Switch(
								checked = countdownEnabled,
								onCheckedChange = { isEnabled ->
									countdownEnabled = isEnabled
									Storage.saveBoolean(StorageKeys.COUNTDOWN_NOTIFICATIONS_ENABLED, isEnabled)
									if (isEnabled) {
										notificationService.scheduleDailyCountdownNotification()
									} else {
										notificationService.cancelDailyCountdownNotification()
									}
								},
								colors = SwitchDefaults.colors(
									checkedThumbColor = Color(0xFF2196F3),
									checkedTrackColor = Color(0xFF90CAF9),
									uncheckedThumbColor = Color.Gray,
									uncheckedTrackColor = Color.LightGray
								)
							)
						}


						// Voting notifications toggle
//						Row(
//							modifier = Modifier.fillMaxWidth(),
//							horizontalArrangement = Arrangement.SpaceBetween,
//							verticalAlignment = Alignment.CenterVertically
//						) {
//							Text(
//								text = TranslationManager.translate("voting_notifications"),
//								style = MaterialTheme.typography.body1,
//								color = Color.Black
//							)
//							Switch(
//								checked = votingEnabled,
//								onCheckedChange = { isEnabled ->
//									votingEnabled = isEnabled
//									Storage.saveBoolean(StorageKeys.VOTING_NOTIFICATIONS_ENABLED, isEnabled)
//									if (isEnabled) {
//										notificationService.scheduleVotingNotifications()
//									} else {
//										notificationService.cancelVotingNotifications()
//									}
//								},
//								colors = SwitchDefaults.colors(
//									checkedThumbColor = Color(0xFF2196F3),
//									checkedTrackColor = Color(0xFF90CAF9),
//									uncheckedThumbColor = Color.Gray,
//									uncheckedTrackColor = Color.LightGray
//								)
//							)
//						}

						// Results notifications toggle
//						Row(
//							modifier = Modifier.fillMaxWidth(),
//							horizontalArrangement = Arrangement.SpaceBetween,
//							verticalAlignment = Alignment.CenterVertically
//						) {
//							Text(
//								text = TranslationManager.translate("results_notifications"),
//								style = MaterialTheme.typography.body1,
//								color = Color.Black
//							)
//							Switch(
//								checked = resultsEnabled,
//								onCheckedChange = { isEnabled ->
//									resultsEnabled = isEnabled
//									Storage.saveBoolean(StorageKeys.RESULTS_NOTIFICATIONS_ENABLED, isEnabled)
//									if (isEnabled) {
//										notificationService.scheduleResultsNotifications()
//									} else {
//										notificationService.cancelResultsNotifications()
//									}
//								},
//								colors = SwitchDefaults.colors(
//									checkedThumbColor = Color(0xFF2196F3),
//									checkedTrackColor = Color(0xFF90CAF9),
//									uncheckedThumbColor = Color.Gray,
//									uncheckedTrackColor = Color.LightGray
//								)
//							)
//						}
					}
				}
			}
		}
	}

	if (expanded) {
		AlertDialog(
			onDismissRequest = { expanded = false },
			backgroundColor = Color(0xFFFFF3F0),
			shape = RoundedCornerShape(24.dp),
			title = {
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 16.dp, bottom = 24.dp),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(
						text = TranslationManager.translate("select_language"),
						style = MaterialTheme.typography.h5.copy(
							fontSize = 28.sp,
							letterSpacing = 1.sp,
							color = Color(0xFFFF6B6B)
						),
						textAlign = TextAlign.Center
					)
					Text(
						text = "",
						style = MaterialTheme.typography.h5.copy(
							fontSize = 28.sp,
							letterSpacing = 1.sp,
							color = Color(0xFFFF6B6B)
						),
						textAlign = TextAlign.Center
					)
				}
			},
			text = {
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 16.dp),
					verticalArrangement = Arrangement.spacedBy(12.dp)
				) {
					languages.forEach { language ->
						Surface(
							modifier = Modifier.fillMaxWidth(),
							color = when (language) {
								"en" -> Color(0xFFE3F2FD)
								"fr" -> Color(0xFFF8BBD0)
								"es" -> Color(0xFFDCEDC8)
								"de" -> Color(0xFFFFE0B2)
								"it" -> Color(0xFFE1BEE7)
								else -> Color(0xFFE3F2FD)
							},
							shape = RoundedCornerShape(24.dp)
						) {
							TextButton(
								onClick = {
									TranslationManager.setLanguage(language)
									Storage.saveString(StorageKeys.LANGUAGE_PREFERENCE, language)
									expanded = false
								},
								modifier = Modifier.fillMaxWidth(),
								colors = ButtonDefaults.textButtonColors(
									contentColor = when (language) {
										"en" -> Color(0xFF2196F3)
										"fr" -> Color(0xFFEC407A)
										"es" -> Color(0xFF4CAF50)
										"de" -> Color(0xFFFF9800)
										"it" -> Color(0xFF9C27B0)
										else -> Color(0xFF2196F3)
									}
								)
							) {
								Row(
									modifier = Modifier
										.fillMaxWidth()
										.padding(vertical = 12.dp),
									horizontalArrangement = Arrangement.Start,
									verticalAlignment = Alignment.CenterVertically
								) {
									val flagRes = when (language) {
										"en" -> Res.drawable.england
										"fr" -> Res.drawable.france
										"es" -> Res.drawable.flag
										"de" -> Res.drawable.german
										"it" -> Res.drawable.italian
										else -> Res.drawable.england
									}

									Image(
										painter = painterResource(flagRes),
										contentDescription = TranslationManager.translate("language_flag"),
										modifier = Modifier.size(28.dp)
									)

									Spacer(modifier = Modifier.width(16.dp))

									Text(
										text = TranslationManager.translate(when (language) {
											"en" -> "english"
											"fr" -> "french"
											"es" -> "spanish"
											"de" -> "german"
											"it" -> "italian"
											else -> "english"
										}),
										style = MaterialTheme.typography.h6.copy(
											fontSize = 20.sp,
											letterSpacing = 0.5.sp
										)
									)
								}
							}
						}
					}
				}
			},
			confirmButton = {
				TextButton(
					onClick = { expanded = false },
					colors = ButtonDefaults.textButtonColors(
						contentColor = Color(0xFFFF6B6B)
					),
					modifier = Modifier.padding(bottom = 8.dp)
				) {
					Text(
						text = "✨ " + TranslationManager.translate("close") + " ✨",
						style = MaterialTheme.typography.button.copy(
							fontSize = 16.sp,
							letterSpacing = 1.sp
						)
					)
				}
			}
		)
	}

	if (showDisableDialog) {
		AlertDialog(
			onDismissRequest = { showDisableDialog = false },
			title = { Text(text = TranslationManager.translate("disable_notifications_title")) },
			text = {
				Column {
					Text(text = TranslationManager.translate("disable_notifications_message"))
					if (PlatformInfo.isIOS) {
						Text(
							text = "Please disable notifications in the iOS Settings that will open.",
							style = MaterialTheme.typography.body2,
							color = Color.Gray,
							modifier = Modifier.padding(top = 8.dp)
						)
					}
				}
			},
			confirmButton = {
				TextButton(onClick = {
					showDisableDialog = false
					if (PlatformInfo.isIOS) {
						// For iOS, open settings to disable notifications
						notificationService.openNotificationSettings()
					} else {
						// For Android, directly update the state
						notificationsEnabled = false
						countdownEnabled = false
						notificationService.cancelDailyCountdownNotification()
					}
				}) {
					Text(text = TranslationManager.translate("yes_disable"))
				}
			},
			dismissButton = {
				TextButton(onClick = { showDisableDialog = false }) {
					Text(text = TranslationManager.translate("keep_enabled"))
				}
			}
		)
	}
}
