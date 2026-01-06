package com.example.projektaplikacija.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.projektaplikacija.SharedViewModel
import com.example.projektaplikacija.utils.OsrmService
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

@Composable
fun MapScreen(
    onBack: () -> Unit,
    sharedVm: SharedViewModel
) {

    val stops = sharedVm.calculatedRoutePoints.value

    val cameraPositionState = rememberCameraPositionState {
        if (stops.isNotEmpty()) {
            position = CameraPosition.fromLatLngZoom(stops.first(), 7.5f)
        }
    }

    var roadPoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var totalKm by remember { mutableStateOf<Double?>(null) }
    var totalMin by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(stops) {
        if (stops.isNotEmpty()) {
            try {
                val route = withContext(Dispatchers.IO) { OsrmService.routeGeoJson(stops) }
                roadPoints = route.geometry
                totalKm = route.distanceMeters / 1000.0
                totalMin = (route.durationSeconds / 60.0).roundToInt()
            } catch (e: Exception) {
                roadPoints = emptyList()
                totalKm = null
                totalMin = null
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.weight(1f),
            cameraPositionState = cameraPositionState
        ) {
            stops.dropLast(1).forEachIndexed { index, point ->
                Marker(
                    state = MarkerState(position = point),
                    title = "Lokacija ${index + 1}"
                )
            }

            Polyline(
                points = if (roadPoints.isNotEmpty()) roadPoints else stops,
                color = Color.Blue,
                width = 8f
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text("Rezultat poti", style = MaterialTheme.typography.titleLarge)

            if (totalKm != null && totalMin != null) {
                Text("Skupna razdalja: ${"%.2f".format(totalKm)} km")
                Text("Skupni čas: $totalMin min")
            } else {
                Text("Skupna razdalja: /")
                Text("Skupni čas: /")
            }

            Button(
                onClick = onBack,
                modifier = Modifier.padding(top = 12.dp)
            ) { Text("Nazaj") }
        }
    }
}