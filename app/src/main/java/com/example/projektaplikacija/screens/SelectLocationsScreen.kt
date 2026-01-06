package com.example.projektaplikacija.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.projektaplikacija.SharedViewModel

@Composable
fun SelectLocationsScreen(
    onNext: () -> Unit,
    sharedVm: SharedViewModel
) {
    val locations = sharedVm.allLocations
    val selectedCount = sharedVm.selectedIds.size
    val canProceed = selectedCount >= 2

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        Text(
            text = "Izbira lokacije ($selectedCount)",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(locations, key = { it.index }) { loc ->
                val stringIndex = loc.index.toString()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { sharedVm.toggleSelection(stringIndex) }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = sharedVm.isSelected(stringIndex),
                        onCheckedChange = { sharedVm.toggleSelection(stringIndex) }
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = loc.address,
                            style = MaterialTheme.typography.titleMedium
                        )
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