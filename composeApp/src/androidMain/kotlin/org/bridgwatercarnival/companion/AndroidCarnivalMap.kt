package org.bridgwatercarnival.companion

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.utsman.osmandcompose.DefaultMapProperties
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.Polyline
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import org.bridgwatercarnival.companion.map.Point
import org.bridgwatercarnival.companion.map.Points
import org.osmdroid.util.GeoPoint
import kotlin.math.roundToInt

fun List<Point>.asGeoPoints() = map { GeoPoint(it.lat, it.long) }

fun getBitmapFromDrawable(context: Context, drawableRes: Int): Bitmap {
	val drawable = ContextCompat.getDrawable(context, drawableRes) ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
	val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
	val canvas = Canvas(bitmap)
	drawable.setBounds(0, 0, canvas.width, canvas.height)
	drawable.draw(canvas)
	return bitmap
}

fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
	val matrix = android.graphics.Matrix()
	matrix.postRotate(degrees)
	return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

@SuppressLint("MissingPermission")
@Composable
fun getUserLocation(): Triple<State<GeoPoint?>, State<Float>, State<Float>> {
	val context = LocalContext.current
	val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

	val userLocation = remember { mutableStateOf<GeoPoint?>(null) }
	val userBearing = remember { mutableStateOf(0f) }
	val userSpeed = remember { mutableStateOf(0f) }

	LaunchedEffect(Unit) {
		try {
			if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
				val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
				val locationCallback = object : LocationCallback() {
					override fun onLocationResult(locationResult: LocationResult) {
						locationResult.lastLocation?.let { location ->
							userLocation.value = GeoPoint(location.latitude, location.longitude)
							userSpeed.value = location.speed // Speed in meters per second

							// Use GPS bearing only if moving fast enough (above 1 m/s)
							if (location.hasBearing() && location.speed > 1) {
								userBearing.value = location.bearing
							}
						}
					}
				}

				// Get last known location first
				try {
					fusedLocationClient.lastLocation.addOnSuccessListener { location ->
						location?.let {
							userLocation.value = GeoPoint(it.latitude, it.longitude)
							userSpeed.value = it.speed
							if (it.hasBearing() && it.speed > 1) {
								userBearing.value = it.bearing
							}
						}
					}.addOnFailureListener { e ->
						println("Error getting last location: ${e.message}")
					}
				} catch (e: Exception) {
					println("Error getting last location: ${e.message}")
				}

				// Request location updates
				try {
					fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
				} catch (e: Exception) {
					println("Error requesting location updates: ${e.message}")
				}
			} else {
				Toast.makeText(context, "Location permission required to be able to access location features", Toast.LENGTH_LONG).show()
			}
		} catch (e: Exception) {
			println("Error in location setup: ${e.message}")
		}
	}

	return Triple(userLocation, userBearing, userSpeed)
}


@Composable
fun getCompassBearing(): State<Float> {
	val context = LocalContext.current
	val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
	val compassBearing = remember { mutableStateOf(0f) }

	LaunchedEffect(Unit) {
		val sensorEventListener = object : SensorEventListener {
			override fun onSensorChanged(event: SensorEvent?) {
				if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
					val rotationMatrix = FloatArray(9)
					val orientationValues = FloatArray(3)
					SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
					SensorManager.getOrientation(rotationMatrix, orientationValues)

					val azimuth = Math.toDegrees(orientationValues[0].toDouble()).toFloat()
					compassBearing.value = (azimuth + 360) % 360
				}
			}

			override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
		}

		val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
		rotationVectorSensor?.let {
			sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_UI)
		}
	}

	return compassBearing
}


@SuppressLint("MissingPermission", "UnrememberedMutableState")
@Composable
actual fun CarnivalMap() {
	val initialGeoPoint = GeoPoint(51.12832476302272, -3.003806519462665)
	var userZoom by remember { mutableStateOf(17.0) }
	val minZoom = 10.0
	val maxZoom = 25.0
	var trackUserPosition by remember { mutableStateOf(false) }
	var showLocationDialog by remember { mutableStateOf(false) }
	var showFilterMenu by remember { mutableStateOf(false) }
	var isLocationTrackingPaused by remember { mutableStateOf(false) }
	var showDistanceInfo by remember { mutableStateOf(false) }
	var selectedMarker by remember { mutableStateOf<Pair<Point, Pair<String, Points.MarkerCategory>>?>(null) }

	// Add filter state with explicit type
	var selectedFilters by remember {
		mutableStateOf<Set<Points.MarkerCategory>>(setOf(
			Points.MarkerCategory.Toilet,
			Points.MarkerCategory.CarPark,
			Points.MarkerCategory.Start,
			Points.MarkerCategory.Finish,
			Points.MarkerCategory.Entertainment,
			Points.MarkerCategory.Break,
			Points.MarkerCategory.Hospitality,
			Points.MarkerCategory.Grandstand,
			Points.MarkerCategory.Assemble,
			Points.MarkerCategory.Infomation
		))
	}

	val cameraState = rememberCameraState {
		geoPoint = initialGeoPoint
		zoom = userZoom
	}

	// Simplified gesture state tracking
	var lastZoomTime by remember { mutableStateOf(0L) }
	val zoomThreshold = 200L // Increased threshold for better control

	val mapProperties = remember {
		DefaultMapProperties.copy(
			isMultiTouchControls = true,
			minZoomLevel = minZoom,
			maxZoomLevel = maxZoom
		)
	}

	val (userLocation, userBearing, userSpeed) = getUserLocation()
	val compassBearing = getCompassBearing()

	val effectiveBearing by derivedStateOf {
		if (userSpeed.value > 1) userBearing.value else compassBearing.value
	}

	// Calculate distances to nearby POIs
	val nearbyPOIs by derivedStateOf {
		userLocation.value?.let { location ->
			Points.markers
				.filter { (_, info) -> info.second in selectedFilters }
				.map { (poi, info) ->
					val distance = calculateDistance(
						Point(location.latitude, location.longitude),
						poi
					)
					Triple(poi, info, distance)
				}
				.filter { (_, _, distance) -> distance <= 500 } // Within 500 meters
				.sortedBy { (_, _, distance) -> distance }
				.take(5) // Show closest 5
		} ?: emptyList()
	}

	// Improved location tracking with pause functionality
	LaunchedEffect(userLocation.value, trackUserPosition, isLocationTrackingPaused) {
		if (trackUserPosition && userLocation.value != null && !isLocationTrackingPaused) {
			try {
				userLocation.value?.let { location ->
					// Update camera position for location tracking
					cameraState.geoPoint = location
				}
			} catch (e: Exception) {
				println("Error updating user location: ${e.message}")
			}
		}
	}

	// Add state for location permission dialog
	var showLocationPermissionDialog by remember { mutableStateOf(false) }

	if (showLocationDialog) {
		AlertDialog(
			onDismissRequest = { showLocationDialog = false },
			title = { Text("Location Required") },
			text = { Text("Location services must be enabled to use this feature.") },
			confirmButton = {
				Button(onClick = { showLocationDialog = false }) {
					Text("OK")
				}
			}
		)
	}

	var expanded by remember { mutableStateOf(false) }

	// Color palette
	val CarnivalBlue: Color = Color(0xFF1976D2)
	 val CarnivalBlueLight = Color(0xFF2196F3)
	 val CarnivalGreen = Color(0xFF43A047)
	 val CarnivalRed = Color(0xFFE53935)
	 val CarnivalGrey = Color(0xFFF7F7F7)
	 val CarnivalDarkText = Color(0xFF222222)
	 val CarnivalButtonBlue = Color(0xFF2196F3)
	 val CarnivalButtonBlueMuted = Color(0xFF1976D2)
	 val CarnivalStatusGreen = Color(0xFF66BB6A)
	 val CarnivalStatusOrange = Color(0xFFFFB300)

	Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
		Box(modifier = Modifier.fillMaxSize()) {
			OpenStreetMap(
				modifier = Modifier.fillMaxSize(),
				cameraState = cameraState,
				properties = mapProperties,
				onMapClick = {
					selectedMarker = null
				}
			) {
				// Enhanced polyline rendering with better styling
				Polyline(geoPoints = Points.routeCoordinates.asGeoPoints(), color = Color(0xFF2196F3), width = 15f)
				Polyline(geoPoints = Points.restAreaPoints.asGeoPoints(), color = Color(0xFFF44336), width = 10f)
				Polyline(geoPoints = Points.lineUpCoordinates.asGeoPoints(), color = Color(0xFFFF9800), width = 10f)

				val context = LocalContext.current
				Points.markers.forEach { (location, pair) ->
					val (title, category) = pair
					// Only show markers that are in the selected filters
					if (category in selectedFilters) {
						val markerState = rememberMarkerState(geoPoint = GeoPoint(location.lat, location.long))
						val iconRes = when (category) {
							Points.MarkerCategory.CarPark -> R.drawable.park
							Points.MarkerCategory.Toilet -> R.drawable.toilet
							Points.MarkerCategory.Start -> R.drawable.start
							Points.MarkerCategory.Finish -> R.drawable.finish
							Points.MarkerCategory.Entertainment -> R.drawable.entertainment
							Points.MarkerCategory.Break -> R.drawable.do_not_enter
							Points.MarkerCategory.Assemble -> R.drawable.assemble
							Points.MarkerCategory.Hospitality -> R.drawable.hospitality
							Points.MarkerCategory.Grandstand -> R.drawable.crowd
							Points.MarkerCategory.Infomation -> R.drawable.information
							else -> R.drawable.arrowcircle
						}
						val iconBitmap = remember { BitmapDrawable(context.resources, getBitmapFromDrawable(context, iconRes)) }
						Marker(
							state = markerState,
							title = title,
							icon = iconBitmap,
							onClick = {
								selectedMarker = location to pair
								true
							}
						)
					}
				}

				userLocation.value?.let { location ->
					val context = LocalContext.current
					val userMarkerState = rememberMarkerState(geoPoint = location)
					val rotatedIcon = remember(effectiveBearing) {
						val originalBitmap = getBitmapFromDrawable(context, R.drawable.arrowcircle)
						rotateBitmap(originalBitmap, effectiveBearing)
					}

					LaunchedEffect(effectiveBearing) {
						userMarkerState.geoPoint = location
					}

					Marker(
						state = userMarkerState,
						title = "You are here",
						icon = BitmapDrawable(context.resources, rotatedIcon)
					)
				}
			}

			// Enhanced marker info popup with distance information
			selectedMarker?.let { (location, pair) ->
				val (title, category) = pair
				val distance = userLocation.value?.let { userLoc ->
					calculateDistance(
						Point(userLoc.latitude, userLoc.longitude),
						location
					)
				}
				
				Box(
					modifier = Modifier
						.fillMaxSize()
						.padding(16.dp)
						.clickable { selectedMarker = null },
					contentAlignment = Alignment.Center
				) {
					Card(
						modifier = Modifier
							.width(320.dp)
							.wrapContentSize()
							.clickable { /* Prevent clicks from reaching the background */ },
						elevation = 16.dp,
						backgroundColor = Color.White,
						shape = RoundedCornerShape(24.dp)
					) {
						Column(
							modifier = Modifier.padding(24.dp),
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							// Category indicator with enhanced styling
							Box(
								modifier = Modifier
									.background(
										when (category) {
											Points.MarkerCategory.CarPark -> Color(0xFF4CAF50)
											Points.MarkerCategory.Toilet -> Color(0xFF2196F3)
											Points.MarkerCategory.Start -> Color(0xFF9C27B0)
											Points.MarkerCategory.Finish -> Color(0xFFE91E63)
											Points.MarkerCategory.Entertainment -> Color(0xFFFF9800)
											Points.MarkerCategory.Break -> Color(0xFFF44336)
											Points.MarkerCategory.Assemble -> Color(0xFF795548)
											Points.MarkerCategory.Hospitality -> Color(0xFF009688)
											Points.MarkerCategory.Grandstand -> Color(0xFF607D8B)
											Points.MarkerCategory.Infomation -> Color(0xFF607D8B)
											else -> Color(0xFF9E9E9E)
										}.copy(alpha = 0.15f)
									)
									.padding(horizontal = 20.dp, vertical = 10.dp)
									.fillMaxWidth(),
								contentAlignment = Alignment.Center
							) {
								Text(
									text = when (category) {
										Points.MarkerCategory.CarPark -> "🚗 Parking"
										Points.MarkerCategory.Toilet -> "🚻 Toilets"
										Points.MarkerCategory.Start -> "🏁 Start Point"
										Points.MarkerCategory.Finish -> "🏁 Finish Point"
										Points.MarkerCategory.Entertainment -> "🎪 Entertainment"
										Points.MarkerCategory.Break -> "⛔ Break Area"
										Points.MarkerCategory.Assemble -> "🛠️ Cart Assemble Point"
										Points.MarkerCategory.Hospitality -> "🍽️ Hospitality"
										Points.MarkerCategory.Grandstand -> "👥 Grandstand"
										Points.MarkerCategory.Infomation -> "ℹ️ Information"
										else -> "📍 Location"
									},
									fontSize = 16.sp,
									color = Color.DarkGray,
									fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
								)
							}

							Spacer(modifier = Modifier.height(20.dp))

							// Title with enhanced typography
							Text(
								text = title,
								fontSize = 22.sp,
								color = Color.Black,
								fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
								modifier = Modifier.padding(bottom = 12.dp),
								textAlign = androidx.compose.ui.text.style.TextAlign.Center
							)

							// Add description for Carnival Center
							if (category == Points.MarkerCategory.Infomation) {
								Text(
									text = "Information and shop",
									fontSize = 16.sp,
									color = Color.Gray,
									fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
									modifier = Modifier.padding(bottom = 12.dp),
									textAlign = androidx.compose.ui.text.style.TextAlign.Center
								)
							}

							// Distance information
							distance?.let { dist ->
								Row(
									modifier = Modifier
										.background(Color(0xFFE3F2FD), RoundedCornerShape(12.dp))
										.padding(horizontal = 16.dp, vertical = 8.dp),
									verticalAlignment = Alignment.CenterVertically
								) {
									Icon(
										imageVector = Icons.Default.KeyboardArrowDown,
										contentDescription = "Distance",
										tint = Color(0xFF1976D2),
										modifier = Modifier.size(20.dp)
									)
									Spacer(modifier = Modifier.width(8.dp))
									Text(
										text = if (dist < 1000) {
											"${dist.toInt()}m"
										} else {
											"${(dist / 1000).roundToInt()}km"
										},
										fontSize = 14.sp,
										color = Color(0xFF1976D2),
										fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
									)
								}
							}
						}
					}
				}
			}

			// Enhanced location controls with better styling
			Box(modifier = Modifier.align(Alignment.TopEnd).padding(20.dp)) {
				Column {
					Surface(
						shape = CircleShape,
						color = Color.White.copy(alpha = 0.95f),
						elevation = 12.dp,
					) {
						IconButton(
							onClick = { expanded = true },
							modifier = Modifier.size(52.dp)
						) {
							Icon(
								imageVector = Icons.Default.MoreVert,
								contentDescription = "Location Options",
								tint = Color.Black,
								modifier = Modifier.size(28.dp)
							)
						}
					}
					DropdownMenu(
						expanded = expanded, 
						onDismissRequest = { expanded = false },
						modifier = Modifier.background(CarnivalGrey, RoundedCornerShape(14.dp))
					) {
						DropdownMenuItem(
							onClick = {
								if (userLocation.value == null) {
									showLocationPermissionDialog = true
								} else {
									trackUserPosition = true
									isLocationTrackingPaused = false
								}
								expanded = false
							}
						) {
							Text("📍 My Location", fontSize = 16.sp, color = CarnivalDarkText)
						}
						if (trackUserPosition) {
							if (isLocationTrackingPaused) {
								DropdownMenuItem(
									onClick = {
										if (userLocation.value == null) {
											showLocationPermissionDialog = true
										} else {
											isLocationTrackingPaused = false
										}
										expanded = false
									}
								) {
									Text("🔄 Resume Tracking", fontSize = 16.sp, color = CarnivalDarkText)
								}
							} else {
								DropdownMenuItem(
									onClick = {
										isLocationTrackingPaused = true
										expanded = false
									}
								) {
									Text("⏸️ Pause Tracking", fontSize = 16.sp, color = CarnivalDarkText)
								}
							}
						}
						Divider(color = Color.LightGray)
						DropdownMenuItem(
							onClick = {
								if (userLocation.value == null) {
									showLocationPermissionDialog = true
								} else {
									showDistanceInfo = !showDistanceInfo
								}
								expanded = false
							}
						) {
							Text(if (showDistanceInfo) "📏 Hide Distances" else "📏 Show Distances", fontSize = 16.sp, color = CarnivalDarkText)
						}
						Divider(color = Color.LightGray)
						DropdownMenuItem(
							onClick = {
								trackUserPosition = false
								isLocationTrackingPaused = false
								cameraState.geoPoint = GeoPoint(51.12823578970213, -3.004670534060195)
								expanded = false
							}
						) {
							Text("🎪Bridgwater", fontSize = 16.sp, color = CarnivalDarkText)
						}
					}
				}
			}

			// Enhanced location tracking status indicator
			if (trackUserPosition) {
				Box(
					modifier = Modifier
						.align(Alignment.TopCenter)
						.padding(top = 12.dp)
				) {
					Surface(
						shape = RoundedCornerShape(16.dp),
						color = if (isLocationTrackingPaused) CarnivalStatusOrange else CarnivalStatusGreen,
						elevation = 6.dp
					) {
						Row(
							modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							Text(
								text = if (isLocationTrackingPaused) 
									"📍 Location tracking paused" 
								else 
									"📍 Following your location",
								color = Color.White,
								fontSize = 13.sp,
								fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
							)
							Spacer(modifier = Modifier.width(8.dp))
							IconButton(
								onClick = { 
									isLocationTrackingPaused = !isLocationTrackingPaused 
								},
								modifier = Modifier.size(22.dp)
							) {
								Icon(
									imageVector = if (isLocationTrackingPaused) Icons.Default.PlayArrow else Icons.Default.KeyboardArrowDown,
									contentDescription = if (isLocationTrackingPaused) "Resume tracking" else "Pause tracking",
									tint = Color.White,
									modifier = Modifier.size(14.dp)
								)
							}
						}
					}
				}
			}

			// Nearby POIs distance panel
			if (showDistanceInfo && nearbyPOIs.isNotEmpty()) {
				Box(
					modifier = Modifier
						.align(Alignment.TopStart)
						.padding(top = 12.dp, start = 9.dp)
				) {
					Surface(
						shape = RoundedCornerShape(12.dp),
						color = Color.White.copy(alpha = 0.97f),
						elevation = 6.dp,
						modifier = Modifier.widthIn(max = 340.dp)
					) {
						Column(
							modifier = Modifier.padding(12.dp)
						) {
							Row(
								modifier = Modifier.fillMaxWidth(),
								horizontalArrangement = Arrangement.SpaceBetween,
								verticalAlignment = Alignment.CenterVertically
							) {
								Text(
									text = "📍 Nearby Locations",
									fontSize = 16.sp,
									fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
									color = Color.Black,
									modifier = Modifier.padding(bottom = 2.dp)
								)
							}
							nearbyPOIs.forEach { (poi, info, distance) ->
								val (title, category) = info
								Row(
									modifier = Modifier
										.fillMaxWidth()
										.padding(vertical = 2.dp)
										.clickable {
											selectedMarker = poi to info
											cameraState.geoPoint = GeoPoint(poi.lat, poi.long)
										},
									verticalAlignment = Alignment.CenterVertically
								) {
									Text(
										text = when (category) {
											Points.MarkerCategory.CarPark -> "🚗"
											Points.MarkerCategory.Toilet -> "🚻"
											Points.MarkerCategory.Start -> "🏁"
											Points.MarkerCategory.Finish -> "🏁"
											Points.MarkerCategory.Entertainment -> "🎪"
											Points.MarkerCategory.Break -> "⛔"
											Points.MarkerCategory.Assemble -> "🛠️"
											Points.MarkerCategory.Hospitality -> "🍽️"
											Points.MarkerCategory.Grandstand -> "👥"
											else -> "📍"
										},
										fontSize = 15.sp,
										modifier = Modifier.width(22.dp)
									)
									Text(
										text = title,
										fontSize = 13.sp,
										color = Color.Black,
										modifier = Modifier.weight(1f).padding(start = 4.dp),
										maxLines = 1,
										overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
									)
									Surface(
										shape = RoundedCornerShape(8.dp),
										color = CarnivalBlue.copy(alpha = 0.10f),
										modifier = Modifier.padding(start = 4.dp)
									) {
										Text(
											text = if (distance < 1000) "${distance.toInt()}m" else "${(distance / 1000).roundToInt()}km",
											fontSize = 12.sp,
											color = CarnivalBlue,
											fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
											modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
										)
									}
								}
							}
						}
					}
				}
			}

			// Enhanced zoom controls
			Box(
				modifier = Modifier
					.align(Alignment.BottomEnd)
					.padding(20.dp)
			) {
				Surface(
					shape = RoundedCornerShape(16.dp),
					color = Color.White.copy(alpha = 0.95f),
					elevation = 8.dp,
					modifier = Modifier.width(44.dp)
				) {
					Column(
						horizontalAlignment = Alignment.CenterHorizontally,
						modifier = Modifier.padding(vertical = 6.dp)
					) {
						IconButton(
							onClick = {
								val currentTime = System.currentTimeMillis()
								if (currentTime - lastZoomTime > zoomThreshold) {
									cameraState.zoom = (cameraState.zoom + 1.0).coerceIn(minZoom, maxZoom)
									lastZoomTime = currentTime
								}
							},
							modifier = Modifier.size(36.dp)
						) {
							Icon(
								imageVector = Icons.Default.Add,
								contentDescription = "Zoom in",
								tint = CarnivalDarkText,
								modifier = Modifier.size(20.dp)
							)
						}
						Divider(
							color = Color.LightGray,
							modifier = Modifier
								.padding(horizontal = 8.dp)
								.width(20.dp)
						)
						IconButton(
							onClick = {
								val currentTime = System.currentTimeMillis()
								if (currentTime - lastZoomTime > zoomThreshold) {
									cameraState.zoom = (cameraState.zoom - 1.0).coerceIn(minZoom, maxZoom)
									lastZoomTime = currentTime
								}
							},
							modifier = Modifier.size(36.dp)
						) {
							Icon(
								imageVector = Icons.Default.KeyboardArrowDown,
								contentDescription = "Zoom out",
								tint = CarnivalDarkText,
								modifier = Modifier.size(20.dp)
							)
						}
					}
				}
			}

			// Enhanced Filter Menu
			Box(
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.padding(bottom = 16.dp)
			) {
				if (showFilterMenu) {
					// Hide Filters button when open
					// Overlay
					Box(
						modifier = Modifier
							.fillMaxSize()
							.background(Color.Black.copy(alpha = 0.3f))
							.clickable { showFilterMenu = false }
					)
					Surface(
						shape = RoundedCornerShape(18.dp),
						color = Color.White,
						elevation = 12.dp,
						modifier = Modifier
							.width(320.dp)
							.align(Alignment.BottomCenter)
							.padding(bottom = 2.dp)
							.clickable { /* Prevent clicks from reaching the overlay */ }
					) {
						Column(
							modifier = Modifier.padding(14.dp)
						) {
							// Header with X close button
							Row(
								modifier = Modifier.fillMaxWidth(),
								horizontalArrangement = Arrangement.SpaceBetween,
								verticalAlignment = Alignment.CenterVertically
							) {
								Text(
									text = "📍 Filter Locations",
									fontSize = 18.sp,
									color = CarnivalBlue,
									fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
								)
								IconButton(
									onClick = { showFilterMenu = false },
									modifier = Modifier.size(28.dp)
								) {
									Icon(
										imageVector = Icons.Default.Close,
										contentDescription = "Close",
										tint = CarnivalBlue,
										modifier = Modifier.size(20.dp)
									)
								}
							}
							Divider(color = Color.LightGray, modifier = Modifier.padding(vertical = 8.dp))
							// Action buttons
							Row(
								modifier = Modifier
									.fillMaxWidth()
									.padding(vertical = 6.dp),
								horizontalArrangement = Arrangement.spacedBy(8.dp)
							) {
								Button(
									onClick = { selectedFilters = emptySet() },
									modifier = Modifier.weight(1f).height(36.dp),
									colors = ButtonDefaults.buttonColors(
										backgroundColor = CarnivalGrey,
										contentColor = CarnivalBlue
									),
									shape = RoundedCornerShape(8.dp)
								) {
									Text("Clear All", fontSize = 13.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
								}
								Button(
									onClick = { 
										selectedFilters = setOf(
											Points.MarkerCategory.Toilet,
											Points.MarkerCategory.CarPark,
											Points.MarkerCategory.Start,
											Points.MarkerCategory.Finish,
											Points.MarkerCategory.Entertainment,
											Points.MarkerCategory.Break,
											Points.MarkerCategory.Hospitality,
											Points.MarkerCategory.Grandstand,
											Points.MarkerCategory.Assemble,
											Points.MarkerCategory.Infomation
										)
									},
									modifier = Modifier.weight(1f).height(36.dp),
									colors = ButtonDefaults.buttonColors(
										backgroundColor = CarnivalBlue,
										contentColor = Color.White
									),
									shape = RoundedCornerShape(8.dp)
								) {
									Text("Select All", fontSize = 13.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
								}
							}
							// Filter options
							Column(
								modifier = Modifier.padding(vertical = 4.dp)
							) {
								val allCategories = listOf(
									Points.MarkerCategory.Toilet,
									Points.MarkerCategory.CarPark,
									Points.MarkerCategory.Start,
									Points.MarkerCategory.Finish,
									Points.MarkerCategory.Entertainment,
									Points.MarkerCategory.Break,
									Points.MarkerCategory.Hospitality,
									Points.MarkerCategory.Grandstand,
									Points.MarkerCategory.Assemble,
									Points.MarkerCategory.Infomation
								)
								allCategories.chunked(2).forEach { rowCategories ->
									Row(
										modifier = Modifier
											.fillMaxWidth()
											.padding(vertical = 4.dp),
										horizontalArrangement = Arrangement.spacedBy(8.dp)
									) {
										rowCategories.forEach { category ->
											Row(
												modifier = Modifier
													.weight(1f)
													.clickable {
														selectedFilters = if (category in selectedFilters) {
															selectedFilters - category
														} else {
															selectedFilters + category
														}
													}
													.padding(horizontal = 6.dp, vertical = 6.dp)
													.background(
														if (category in selectedFilters) 
															CarnivalBlue.copy(alpha = 0.10f) 
														else 
															Color.Transparent,
														RoundedCornerShape(6.dp)
													),
												verticalAlignment = Alignment.CenterVertically
											) {
												androidx.compose.material.Checkbox(
													checked = category in selectedFilters,
													onCheckedChange = { checked ->
														selectedFilters = if (checked) {
															selectedFilters + category
														} else {
															selectedFilters - category
														}
													},
													colors = CheckboxDefaults.colors(
														checkedColor = CarnivalBlue,
														uncheckedColor = Color.LightGray,
														checkmarkColor = Color.White
													)
												)
												Text(
													text = when (category) {
														Points.MarkerCategory.CarPark -> "🚗 Parking"
														Points.MarkerCategory.Toilet -> "🚻 Toilets"
														Points.MarkerCategory.Start -> "🏁 Start"
														Points.MarkerCategory.Finish -> "🏁 Finish"
														Points.MarkerCategory.Entertainment -> "🎪 Events"
														Points.MarkerCategory.Break -> "⛔ Break Area"
														Points.MarkerCategory.Hospitality -> "🍽️ Hospitality"
														Points.MarkerCategory.Grandstand -> "👥 Grandstand"
														Points.MarkerCategory.Assemble -> "🛠️Cart Assemble Point"
														Points.MarkerCategory.Infomation -> "🏡 Carnival Centre (Infomation and shop) "
													},
													fontSize = 13.sp,
													color = Color.Black,
													fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
													modifier = Modifier.padding(start = 4.dp)
												)
											}
										}
									}
								}
							}
						}
					}
				} else {
					// Show Filters button only when filter dialog is closed
					Surface(
						shape = RoundedCornerShape(24.dp),
						color = CarnivalButtonBlue,
						elevation = 8.dp,
						modifier = Modifier
							.width(120.dp)
							.height(40.dp)
							.clickable { showFilterMenu = !showFilterMenu }
					) {
						Row(
							modifier = Modifier
								.fillMaxSize()
								.padding(horizontal = 12.dp),
							horizontalArrangement = Arrangement.Center,
							verticalAlignment = Alignment.CenterVertically,
						) {
							Icon(
								imageVector = if (showFilterMenu) Icons.Default.KeyboardArrowDown else Icons.Default.MoreVert,
								contentDescription = "Filter",
								tint = Color.White,
								modifier = Modifier.size(18.dp)
							)
							Text(
								text = "Filters",
								color = Color.White,
								fontSize = 14.sp,
								fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
								modifier = Modifier.padding(start = 6.dp)
							)
						}
					}
				}
			}

			// Centered popup dialog for location permission
			if (showLocationPermissionDialog) {
				AlertDialog(
					onDismissRequest = { showLocationPermissionDialog = false },
					title = { Text("Location Required") },
					text = { Text("Please enable location in device settings.") },
					confirmButton = {
						Button(onClick = { showLocationPermissionDialog = false }) {
							Text("OK")
						}
					},
					modifier = Modifier
						.fillMaxWidth(0.85f)
						.wrapContentSize(Alignment.Center)
				)
			}
		}
	}
}

// Helper function to calculate distance between two points
private fun calculateDistance(point1: Point, point2: Point): Double {
	val lat1 = point1.lat
	val lon1 = point1.long
	val lat2 = point2.lat
	val lon2 = point2.long

	val earthRadius = 6371000.0 // meters
	val dLat = Math.toRadians(lat2 - lat1)
	val dLon = Math.toRadians(lon2 - lon1)

	val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
			Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
			Math.sin(dLon / 2) * Math.sin(dLon / 2)

	val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

	return earthRadius * c
}