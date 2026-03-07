package org.bridgwatercarnival.companion

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.utsman.osmandcompose.DefaultMapProperties
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import org.osmdroid.util.GeoPoint

private fun createMarkerBitmapFromResource(context: Context, drawableRes: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(context, drawableRes)
        ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    
    return Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    ).also { bitmap ->
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
    }
}

@Composable
actual fun CarParkMap(carParks: List<Pair<Double, Double>>) {
    val context = LocalContext.current
    val initialGeoPoint = GeoPoint(51.12832476302272, -3.003806519462665) // Center of Bridgwater
    val cameraState = rememberCameraState {
        geoPoint = initialGeoPoint
        zoom = 15.0
    }

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

    val mapProperties = remember {
        DefaultMapProperties.copy(
            isMultiTouchControls = true,
            minZoomLevel = 10.0,
            maxZoomLevel = 20.0
        )
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                return Offset(0f, available.y)
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                return Offset(0f, available.y)
            }
        }
    }

    OpenStreetMap(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection),
        cameraState = cameraState,
        properties = mapProperties
    ) {
        carParks.forEachIndexed { index, (lat, long) ->
            val markerState = rememberMarkerState(geoPoint = GeoPoint(lat, long))
            val iconBitmap = remember { 
                BitmapDrawable(
                    context.resources, 
                    createMarkerBitmapFromResource(context, R.drawable.park)
                )
            }
            
            Marker(
                state = markerState,
                title = carParkNames.getOrNull(index) ?: "Car Park",
                icon = iconBitmap
            )
        }
    }
} 