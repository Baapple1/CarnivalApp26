package org.bridgwatercarnival.companion

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.*
import platform.UIKit.UIColor
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun CarParkMap(carParks: List<Pair<Double, Double>>) {
    val carParkNames = listOf(
        "Morrison's car park",
        "B&M car park",
        "ST Matthews field",
        "Northgate",
        "Asda car park",
        "Wickes car park",
        "Bridgwater Hospital",
        "Morganians Rugby Football Club"
    )

    val mapView = remember { 
        MKMapView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)).apply {
            setShowsUserLocation(true)
            setShowsCompass(true)
            setZoomEnabled(true)
            setScrollEnabled(true)
            setRotateEnabled(true)
        }
    }

    DisposableEffect(mapView) {
        // Center on Bridgwater
        val bridgwaterLocation = CLLocationCoordinate2DMake(
            51.12832476302272,
            -3.003806519462665
        )
        val region = MKCoordinateRegionMakeWithDistance(
            bridgwaterLocation,
            2000.0, // 2km span
            2000.0
        )
        mapView.setRegion(region, animated = false)

        // Add car park annotations
        carParks.forEachIndexed { index, (lat, long) ->
            val annotation = MKPointAnnotation().apply {
                setCoordinate(CLLocationCoordinate2DMake(lat, long))
                setTitle(carParkNames.getOrNull(index) ?: "Car Park")
            }
            mapView.addAnnotation(annotation)
        }

        onDispose {
            mapView.removeAnnotations(mapView.annotations)
        }
    }

    UIKitView(
        modifier = Modifier.fillMaxSize(),
        factory = { mapView }
    )
} 