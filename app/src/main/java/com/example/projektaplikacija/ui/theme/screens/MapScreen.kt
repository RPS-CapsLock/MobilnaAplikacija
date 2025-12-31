package com.example.projektaplikacija.ui.theme.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.MarkerState

@Composable
fun MapScreen( onBack: () -> Unit ) {

    val route = listOf(
        LatLng(46.05108, 14.50513),
        LatLng(46.23887, 14.35561),
        LatLng(46.23102, 15.26044),
        LatLng(46.55731, 15.64588),
        LatLng(46.05108, 14.50513)
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(route.first(), 7.5f)
    }

    Column(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.weight(1f),
            cameraPositionState = cameraPositionState
        ) {
            route.dropLast(1).forEachIndexed { index, point ->
                Marker(
                    state = MarkerState(position = point),
                    title = "Postaja ${index + 1}"
                )
            }

            Polyline(
                points = route,
                width = 8f
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text("Rezultat poti", style = MaterialTheme.typography.titleLarge)
            Text("Skupna razdalja: km")
            Text("Skupni čas: min")

            Button(
                onClick = onBack,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text("Nazaj")
            }
        }
    }
}