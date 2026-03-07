package org.bridgwatercarnival.companion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import org.bridgwatercarnival.companion.map.Point
import org.bridgwatercarnival.companion.map.Points
import platform.CoreGraphics.CGRectMake
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKCoordinateSpanMake
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKPointAnnotation
import platform.MapKit.MKPolyline
import platform.MapKit.addOverlay
import platform.MapKit.overlays
import platform.MapKit.removeOverlays
import platform.UIKit.NSLayoutConstraint
import platform.UIKit.UIButton
import platform.UIKit.UIButtonTypeSystem
import platform.UIKit.UIColor
import platform.UIKit.UIControlContentHorizontalAlignmentCenter
import platform.UIKit.UIControlContentVerticalAlignmentCenter
import platform.UIKit.UIControlEventTouchUpInside
import platform.UIKit.UIControlEventValueChanged
import platform.UIKit.UIControlStateNormal
import platform.UIKit.UIFont
import platform.UIKit.UIFontWeightBold
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UISegmentedControl
import platform.UIKit.systemBackgroundColor
import platform.UIKit.systemBlueColor
import platform.UIKit.systemGray4Color
import platform.UIKit.systemIndigoColor
import platform.UIKit.systemPurpleColor
import platform.darwin.NSObject
import kotlin.math.*
import androidx.compose.ui.interop.UIKitView
import platform.Foundation.valueForKey
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import platform.UIKit.UIApplication
import platform.Foundation.NSURL
import platform.UIKit.UIApplicationOpenSettingsURLString
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


lateinit var getMapDelegate: () -> MKMapViewDelegateProtocol
lateinit var coloredPolyLine: (points: Array<Point>, count: Int, color: UIColor) -> MKPolyline

class CustomAnnotation : MKPointAnnotation() {
    var type: String = "default"
}

fun drawLines(map: MKMapView) {
    // Main parade route - make it more prominent and solid
    val paradeRoute = makePolyLine(
        points = Points.routeCoordinates,
        color = UIColor.blueColor.colorWithAlphaComponent(0.99)
    )

    // No-viewing areas - make them clearly warning zones with dashed lines
    val noViewingAreas = makePolyLine(
        points = Points.restAreaPoints,
        color = UIColor.redColor.colorWithAlphaComponent(0.6)
    )

    // Line-up area - make it distinct from main route
    val lineUpArea = makePolyLine(
        points = Points.lineUpCoordinates,
        color = UIColor.orangeColor.colorWithAlphaComponent(0.7)
    )

    // Add all overlays
    map.addOverlay(paradeRoute)
    map.addOverlay(noViewingAreas)
    map.addOverlay(lineUpArea)
}

fun makePolyLine(
    points: List<Point>,
    color: UIColor
): MKPolyline {
    println("makePolyLine called with ${points.size} points")
    println("First point: ${points.firstOrNull()}")
    println("Last point: ${points.lastOrNull()}")
    println("Color: $color")

    val result = coloredPolyLine(
        points.toTypedArray(),
        points.size,
        color
    )

    println("Created polyline: $result")
    return result
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun CarnivalMap() {
    val delegate = remember { getMapDelegate() }
    val mapView = remember { MKMapView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)) }

    // Compose state for user location
    var userLat by remember { mutableStateOf<Double?>(null) }
    var userLon by remember { mutableStateOf<Double?>(null) }
    var showNearbyBox by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
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
    var selectedFilters by remember { mutableStateOf(allCategories.toSet()) }

    // Function to redraw markers based on current filters
    fun redrawMarkers() {
        // Remove all existing annotations (but keep overlays)
        mapView.removeAnnotations(mapView.annotations)

        // Add only filtered markers
        Points.markers.forEach {
            val point = it.first
            val info = it.second
            val title = info.first
            val category = info.second

            // Only add markers that are in the selected filters
            if (category in selectedFilters) {
                val annotation = CustomAnnotation().apply {
                    setCoordinate(CLLocationCoordinate2DMake(point.lat, point.long))
                    setTitle(title)
                    this.type = when (category) {
                        is Points.MarkerCategory.Start -> "Start"
                        is Points.MarkerCategory.Finish -> "Finish"
                        is Points.MarkerCategory.Toilet -> "Toilet"
                        is Points.MarkerCategory.CarPark -> "CarPark"
                        is Points.MarkerCategory.Entertainment -> "Entertainment"
                        is Points.MarkerCategory.Break -> "Break"
                        is Points.MarkerCategory.Hospitality -> "Hospitality"
                        is Points.MarkerCategory.Grandstand -> "Grandstand"
                        is Points.MarkerCategory.Assemble -> "Cart Assemble Point"
                        is Points.MarkerCategory.Infomation -> "information"
                        else -> "default"
                    }
                }
                mapView.addAnnotation(annotation)
            }
        }
    }

    // Function to ensure route lines are drawn
    fun ensureRouteLines() {
        println("=== ensureRouteLines called ===")

        // Check if coloredPolyLine function is available
        println("coloredPolyLine function available: ${::coloredPolyLine.isInitialized}")

        // Remove existing overlays first
        mapView.removeOverlays(mapView.overlays)
        println("Removed existing overlays")

        // Check if route coordinates exist
        println("Route coordinates count: ${Points.routeCoordinates.size}")
        println("Rest area points count: ${Points.restAreaPoints.size}")
        println("Line up coordinates count: ${Points.lineUpCoordinates.size}")

        // Check if coordinates are valid
        if (Points.routeCoordinates.isNotEmpty()) {
            println("First route coordinate: ${Points.routeCoordinates.first()}")
            println("Last route coordinate: ${Points.routeCoordinates.last()}")
        }

        try {
            // Main parade route - make it vibrant and carnival-like
            println("Creating main parade route polyline...")
            val paradeRoute = makePolyLine(
                points = Points.routeCoordinates,
                color = UIColor.blueColor.colorWithAlphaComponent(0.99) // Original blue
            )
            println("Created parade route polyline: $paradeRoute")

            // No-viewing areas - make them clearly warning zones with bright red
            println("Creating no-viewing areas polyline...")
            val noViewingAreas = makePolyLine(
                points = Points.restAreaPoints,
                color = UIColor.redColor.colorWithAlphaComponent(0.6) // Original red
            )
            println("Created no-viewing areas polyline: $noViewingAreas")

            // Line-up area - make it distinct with bright orange
            println("Creating line-up area polyline...")
            val lineUpArea = makePolyLine(
                points = Points.lineUpCoordinates,
                color = UIColor.orangeColor.colorWithAlphaComponent(0.7) // Original orange
            )
            println("Created line-up area polyline: $lineUpArea")

            // Add just the main overlays (no multiple layers)
            println("Adding overlays to map...")
            mapView.addOverlay(paradeRoute) // Main purple route
            mapView.addOverlay(noViewingAreas) // Red warning areas
            mapView.addOverlay(lineUpArea) // Orange line-up area

            println("Added ${mapView.overlays.size} overlays to map")
            println("Map overlays: ${mapView.overlays}")

            // Force map to refresh
            mapView.setNeedsDisplay()
            println("Forced map to refresh")

        } catch (e: Exception) {
            println("Error creating route lines: ${e.message}")
            e.printStackTrace()
        }

        println("=== ensureRouteLines completed ===")
    }

    // Redraw markers when filters change
    androidx.compose.runtime.LaunchedEffect(selectedFilters) {
        redrawMarkers()
    }

    // Ensure route lines are always drawn
    androidx.compose.runtime.LaunchedEffect(Unit) {
        // Add a small delay to ensure map view is fully initialized
        kotlinx.coroutines.delay(500)
        ensureRouteLines()
    }

    // Update user location periodically
    androidx.compose.runtime.LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000) // Check every second
            try {
                val userLocation = mapView.userLocation
                val location = userLocation.location
                if (location != null) {
                    val desc = location.description ?: ""
                    println("Checking location: $desc")
                    val regex = Regex("""<\+?([0-9.\-]+),([0-9.\-]+)>""")
                    val match = regex.find(desc)
                    val lat = match?.groups?.get(1)?.value?.toDoubleOrNull()
                    val lon = match?.groups?.get(2)?.value?.toDoubleOrNull()
                    if (lat != null && lon != null && lat != 0.0 && lon != 0.0) {
                        userLat = lat
                        userLon = lon
                        println("Updated user location: $lat, $lon")
                    }
                }
            } catch (e: Exception) {
                println("Error updating location: ${e.message}")
            }
        }
    }

    // Get the overlay delegate for polyline rendering
    val overlayDelegate = remember { getMapDelegate() }

    // Create a simple delegate that only handles location updates and UI interactions
    val simpleDelegate = remember {
        object : NSObject(), MKMapViewDelegateProtocol {
            override fun mapView(mapView: MKMapView, didUpdateUserLocation: platform.MapKit.MKUserLocation) {
                // Handle location updates for our state
                val location = didUpdateUserLocation.location
                val desc = location?.description ?: ""
                println("CLLocation description: $desc")
                val regex = Regex("""<\+?([0-9.\-]+),([0-9.\-]+)>""")
                val match = regex.find(desc)
                val lat = match?.groups?.get(1)?.value?.toDoubleOrNull()
                val lon = match?.groups?.get(2)?.value?.toDoubleOrNull()
                if (lat != null && lon != null && lat != 0.0 && lon != 0.0) {
                    userLat = lat
                    userLon = lon
                    println("Updated user location: $lat, $lon")
                }
            }

            // Add UI interaction methods
            @ObjCAction
            fun handleMapTypeChange(sender: UISegmentedControl) {
                println("Map type changed to index: ${sender.selectedSegmentIndex}")
                when (sender.selectedSegmentIndex) {
                    0L -> mapView.mapType = platform.MapKit.MKMapTypeStandard
                    1L -> mapView.mapType = platform.MapKit.MKMapTypeSatellite
                    2L -> mapView.mapType = platform.MapKit.MKMapTypeHybrid
                }
            }

            @ObjCAction
            fun handleCenterButton(sender: UIButton) {
                println("Center button pressed - centering on carnival route")
                val coordinate = CLLocationCoordinate2DMake(51.12832476302272, -3.003806519462665)
                val span = MKCoordinateSpanMake(0.02, 0.02)
                val region = MKCoordinateRegionMake(coordinate, span)
                mapView.setRegion(region, animated = true)
            }
        }
    }

    // Compose UI
    Box(Modifier.fillMaxSize()) {
        UIKitView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                println("Setting up map view...")
                mapView.delegate = simpleDelegate // Use simple delegate for location updates and UI interactions
                println("Simple delegate set for location updates and UI interactions")
                mapView.showsUserLocation = true
                mapView.showsUserTrackingButton = true
                mapView.showsCompass = true
                mapView.showsScale = false
                mapView.showsTraffic = true
                mapView.showsBuildings = true
                mapView.setPitchEnabled(true)
                mapView.setRotateEnabled(true)
                mapView.setZoomEnabled(true)
                mapView.setScrollEnabled(true)

                val coordinate = CLLocationCoordinate2DMake(51.12832476302272, -3.003806519462665)
                val span = MKCoordinateSpanMake(0.02, 0.02)
                val region = MKCoordinateRegionMake(coordinate, span)
                mapView.setRegion(region, animated = true)

                // Create segmented control for map types
                val items = listOf("Map", "Satellite", "Hybrid")
                val segmentedControl = UISegmentedControl(items = items)
                segmentedControl.setFrame(CGRectMake(10.0, 10.0, 200.0, 32.0))
                segmentedControl.addTarget(
                    simpleDelegate,
                    platform.objc.sel_registerName("handleMapTypeChange:"),
                    UIControlEventValueChanged
                )
                mapView.addSubview(segmentedControl)

                // Create and add the center button (smaller)
                val centerButton = UIButton.buttonWithType(UIButtonTypeSystem).apply {
                    setTitle("View Carnival route", UIControlStateNormal)
                    setTitleColor(UIColor.whiteColor, UIControlStateNormal)
                    val vibrantColor = UIColor.systemIndigoColor.colorWithAlphaComponent(0.85)
                    backgroundColor = vibrantColor
                    layer.cornerRadius = 8.0
                    layer.borderWidth = 1.0
                    layer.borderColor = UIColor.systemPurpleColor.colorWithAlphaComponent(0.3).CGColor
                    titleLabel?.font = UIFont.systemFontOfSize(13.0, UIFontWeightBold)
                    contentHorizontalAlignment = UIControlContentHorizontalAlignmentCenter
                    contentVerticalAlignment = UIControlContentVerticalAlignmentCenter
                    layer.shadowColor = UIColor.blackColor.CGColor
                    layer.shadowOffset = platform.CoreGraphics.CGSizeMake(0.0, 2.0)
                    layer.shadowRadius = 4.0
                    layer.shadowOpacity = 0.18F
                    setTranslatesAutoresizingMaskIntoConstraints(false)
                }
                mapView.addSubview(centerButton)
                val constraints = mutableListOf<NSLayoutConstraint>()
                constraints.add(centerButton.centerXAnchor.constraintEqualToAnchor(mapView.centerXAnchor))
                constraints.add(centerButton.bottomAnchor.constraintEqualToAnchor(
                    mapView.safeAreaLayoutGuide.bottomAnchor, constant = -24.0))
                constraints.add(centerButton.widthAnchor.constraintEqualToConstant(160.0))
                constraints.add(centerButton.heightAnchor.constraintEqualToConstant(36.0))
                constraints.add(centerButton.leadingAnchor.constraintGreaterThanOrEqualToAnchor(mapView.leadingAnchor, constant = 10.0))
                constraints.add(centerButton.trailingAnchor.constraintLessThanOrEqualToAnchor(mapView.trailingAnchor, constant = -10.0))
                NSLayoutConstraint.activateConstraints(constraints)
                centerButton.addTarget(
                    simpleDelegate,
                    platform.objc.sel_registerName("handleCenterButton:"),
                    UIControlEventTouchUpInside
                )

                // Now set the overlay delegate for polyline rendering
                mapView.delegate = overlayDelegate
                println("Overlay delegate set for polyline rendering")

                // Draw initial markers and lines
                println("Drawing initial markers and route lines...")
                redrawMarkers()
                ensureRouteLines()

                // Also try drawing with a delay to ensure map is fully initialized
                GlobalScope.launch {
                    kotlinx.coroutines.delay(2000)
                    println("Delayed route drawing...")
                    ensureRouteLines()
                }
                mapView
            }
        )
        // Floating Nearby button (bottom right, iOS style, adaptive background)
        val nearbyButtonBg = if (isSystemInDarkTheme()) Color(0xFF232323) else Color.White
        Surface(
            shape = CircleShape,
            color = nearbyButtonBg,
            elevation = 8.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 32.dp, end = 20.dp)
                .size(44.dp)
        ) {
            IconButton(
                onClick = {
                    val lat = userLat
                    val lon = userLon
                    if (lat == null || lon == null || lat == 0.0 || lon == 0.0) {
                        showLocationDialog = true
                    } else {
                        showNearbyBox = true
                    }
                },
                modifier = Modifier
                    .background(Color.Transparent, CircleShape)
                    .size(44.dp)
            ) {
                androidx.compose.material.Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Show Nearby Locations",
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        // Floating Filter button (above Nearby)
        Surface(
            shape = CircleShape,
            color = nearbyButtonBg,
            elevation = 8.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 88.dp, end = 20.dp)
                .size(44.dp)
        ) {
            IconButton(
                onClick = { showFilterDialog = true },
                modifier = Modifier
                    .background(Color.Transparent, CircleShape)
                    .size(44.dp)
            ) {
                androidx.compose.material.Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "Filter Locations",
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        // Location permission dialog
        if (showLocationDialog) {
            AlertDialog(
                onDismissRequest = { showLocationDialog = false },
                title = { Text("Location Required") },
                text = { Text("Location is required. Please enable location in device settings.") },
                confirmButton = {
                    Button(onClick = {
                        showLocationDialog = false
                        UIApplication.sharedApplication.openURL(NSURL(string = UIApplicationOpenSettingsURLString)!!)
                    }) {
                        Text("Open Settings")
                    }
                },
                dismissButton = {
                    Button(onClick = { showLocationDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        // Filter dialog (Android-style)
        if (showFilterDialog) {
            // Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable { showFilterDialog = false }
            ) {}
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    elevation = 12.dp,
                    modifier = Modifier
                        .widthIn(max = 340.dp)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
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
                                text = "\uD83D\uDCCD Filter Locations",
                                fontSize = 18.sp,
                                color = Color(0xFF1976D2),
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                            IconButton(
                                onClick = { showFilterDialog = false },
                                modifier = Modifier.size(28.dp)
                            ) {
                                androidx.compose.material.Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color(0xFF1976D2),
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
                                colors = androidx.compose.material.ButtonDefaults.buttonColors(
                                    backgroundColor = Color(0xFFF7F7F7),
                                    contentColor = Color(0xFF1976D2)
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
                                colors = androidx.compose.material.ButtonDefaults.buttonColors(
                                    backgroundColor = Color(0xFF1976D2),
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
                                                        Color(0xFF1976D2).copy(alpha = 0.10f)
                                                    else
                                                        Color.Transparent,
                                                    RoundedCornerShape(6.dp)
                                                ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = category in selectedFilters,
                                                onCheckedChange = { checked ->
                                                    selectedFilters = if (checked) {
                                                        selectedFilters + category
                                                    } else {
                                                        selectedFilters - category
                                                    }
                                                },
                                                colors = androidx.compose.material.CheckboxDefaults.colors(
                                                    checkedColor = Color(0xFF1976D2),
                                                    uncheckedColor = Color.LightGray,
                                                    checkmarkColor = Color.White
                                                )
                                            )//
                                            Text(
                                                text = when (category) {
                                                    Points.MarkerCategory.CarPark -> "\uD83D\uDE97 Parking"
                                                    Points.MarkerCategory.Toilet -> "\uD83D\uDEBB Toilets"
                                                    Points.MarkerCategory.Start -> "\uD83C\uDFC1 Start"
                                                    Points.MarkerCategory.Finish -> "\uD83C\uDFC1 Finish"
                                                    Points.MarkerCategory.Entertainment -> "\uD83C\uDFAA Events"
                                                    Points.MarkerCategory.Break -> "\u26D4 Break Area"
                                                    Points.MarkerCategory.Hospitality -> "\uD83C\uDF7D\uFE0F Hospitality"
                                                    Points.MarkerCategory.Grandstand -> "\uD83D\uDC65 Grandstand"
                                                    Points.MarkerCategory.Assemble -> "\uD83D\uDEE0\uFE0FCart Assemble Point"
                                                    Points.MarkerCategory.Infomation -> "ℹ️ Carnival Centre (Information and Shop)"
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
            }
        }
        // Floating Nearby Locations box (if user location available and showNearbyBox is true)
        val nearestPOIs = if (userLat != null && userLon != null && userLat != 0.0 && userLon != 0.0) {
            Points.markers
                .filter { it.second.second in selectedFilters }
                .map { pair ->
                    val point = pair.first
                    val info = pair.second
                    val dist = haversine(userLat!!, userLon!!, point.lat, point.long)
                    Triple(point, info, dist)
                }
                .sortedBy { it.third }
                .take(3)
        } else emptyList()
        if (showNearbyBox && nearestPOIs.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White.copy(alpha = 0.97f),
                elevation = 8.dp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 18.dp)
                    .widthIn(max = 340.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
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
                        IconButton(onClick = { showNearbyBox = false }) {
                            androidx.compose.material.Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Nearby Locations",
                                tint = Color.Gray
                            )
                        }
                    }
                    nearestPOIs.forEach { (point, info, dist) ->
                        val (title, category) = info
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
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
                                    Points.MarkerCategory.Infomation -> "ℹ️"

                                    else -> "📍"
                                },
                                fontSize = 15.sp,
                                modifier = Modifier.widthIn(min = 22.dp, max = 28.dp)
                            )
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                color = Color.Black,
                                modifier = Modifier.weight(1f).padding(start = 4.dp),
                                maxLines = 1
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1976D2).copy(alpha = 0.10f),
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                Text(
                                    text = if (dist < 1000) "${dist.toInt()}m" else "${(dist / 1000).roundToInt()}km",
                                    fontSize = 12.sp,
                                    color = Color(0xFF1976D2),
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
}

// Haversine formula for distance in meters
private fun toRadians(degrees: Double): Double = degrees * (kotlin.math.PI / 180.0)
fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371000.0 // Earth radius in meters
    val dLat = toRadians(lat2 - lat1)
    val dLon = toRadians(lon2 - lon1)
    val a = kotlin.math.sin(dLat / 2).pow(2.0) + kotlin.math.cos(toRadians(lat1)) * kotlin.math.cos(toRadians(lat2)) * kotlin.math.sin(dLon / 2).pow(2.0)
    val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
    return R * c
}