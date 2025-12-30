package com.example.projektaplikacija.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox

// Testni podatki - zamenjajo kasneje
data class Location(
    val id: String,
    val name: String,
    val address: String,
    val lat: Double,
    val lon: Double
)

private val TEST_LOCATIONS: List<Location> = listOf(
    Location("LJ_Center", "Ljubljana - Center", "Prešernov trg, Ljubljana", 46.05108, 14.50513),
    Location("LJ_BTC", "Ljubljana - BTC", "Ameriška ulica, Ljubljana", 46.06545, 14.54877),
    Location("KR", "Kranj", "Glavni trg, Kranj", 46.23887, 14.35561),
    Location("CE", "Celje", "Krekov trg, Celje", 46.23102, 15.26044),
    Location("MB", "Maribor", "Glavni trg, Maribor", 46.55731, 15.64588),
    Location("KP", "Koper", "Titov trg, Koper", 45.54806, 13.73019),
    Location("NG", "Nova Gorica", "Trg Edvarda Kardelja, NG", 45.95604, 13.64837),
    Location("NM", "Novo mesto", "Glavni trg, Novo mesto", 45.80342, 15.16890)
)




class SelectLocationsViewModel : ViewModel() {
    val locations: List<Location> = TEST_LOCATIONS
    val selectedIds = mutableStateListOf<String>()

    init {
        selectedIds.addAll(locations.map { it.id })
    }

    fun isSelected(id: String): Boolean = selectedIds.contains(id)

    fun toggle(id: String) {
        if (selectedIds.contains(id)) {
            selectedIds.remove(id)
        } else {
            selectedIds.add(id)
        }
    }
}

@Composable
fun SelectLocationsScreen(
    onNext: () -> Unit,
    vm: SelectLocationsViewModel = viewModel()
) {
    val locations = vm.locations
    val selectedCount = vm.selectedIds.size
    val canProceed = selectedCount >= 2

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        Text(
            text = "Izbira lokacije",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(locations, key = { it.id }) { loc ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { vm.toggle(loc.id) }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = vm.isSelected(loc.id),
                        onCheckedChange = { vm.toggle(loc.id) }
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(loc.name, style = MaterialTheme.typography.titleMedium)
                        Text(loc.address, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNext,
            enabled = canProceed,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Naprej")
        }
    }
}