package com.example.projektaplikacija

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tsp.City
import com.example.tsp.GeneticAlgorithm
import com.example.tsp.TSP
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext

    val allLocations = mutableStateListOf<City>()
    val selectedIds = mutableStateListOf<String>()

    val popSize = mutableStateOf("100")
    val crossRate = mutableStateOf("0.8")
    val mutRate = mutableStateOf("0.1")
    val maxFes = mutableStateOf("3000")
    val repeats = mutableStateOf("30")
    val optimizeChoice = mutableStateOf(0)

    var calculatedRoutePoints = mutableStateOf<List<LatLng>>(emptyList())
    var calculatedDistance = mutableStateOf(0.0)

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            context.assets.open("distance.tsp").use { stream ->
                val tempTsp = TSP(stream, emptyArray())

                withContext(Dispatchers.Main) {
                    allLocations.clear()
                    allLocations.addAll(tempTsp.cities)
                    selectedIds.clear()
                    selectedIds.addAll(tempTsp.cities.map { it.index.toString() })
                }
            }
        }
    }

    fun isSelected(id: String): Boolean = selectedIds.contains(id)

    fun toggleSelection(id: String) {
        if (selectedIds.contains(id)) selectedIds.remove(id) else selectedIds.add(id)
    }

    fun runAlgorithm(onFinished: () -> Unit) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val fileName = if (optimizeChoice.value == 0) "time.tsp" else "distance.tsp"

                context.assets.open(fileName).use { stream ->
                    val allIndices = allLocations.map { it.index }
                    val selectedIndices = selectedIds.mapNotNull { it.toIntOrNull() }
                    val leaveOut = allIndices.filter { !selectedIndices.contains(it) }.toTypedArray()

                    val activeTsp = TSP(stream, leaveOut)

                    val ga = GeneticAlgorithm(
                        tsp = activeTsp,
                        popSize = popSize.value.toIntOrNull() ?: 100,
                        cr = crossRate.value.toDoubleOrNull() ?: 0.8,
                        pm = mutRate.value.toDoubleOrNull() ?: 0.1,
                        repeat = repeats.value.toIntOrNull() ?: 10,
                    )

                    val result = ga.run()
                    val bestTour = result.first

                    val route = bestTour.getPath().filterNotNull().map { LatLng(it.x, it.y) }

                    withContext(Dispatchers.Main) {
                        calculatedRoutePoints.value = if (route.isNotEmpty()) route + route.first() else route
                        calculatedDistance.value = bestTour.distance
                        onFinished()
                    }
                }
            } catch (e: Exception) {
                Log.e("TSP", "Napaka algoritma: ${e.message}")
            }
        }
    }
}