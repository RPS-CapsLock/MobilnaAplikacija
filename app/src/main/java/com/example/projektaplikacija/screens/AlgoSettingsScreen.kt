package com.example.projektaplikacija.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.projektaplikacija.SharedViewModel

@Composable
fun AlgoSettingsScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    sharedVm: SharedViewModel
) {
    var isLoading by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nastavitve algoritma",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = sharedVm.popSize.value,
            onValueChange = { sharedVm.popSize.value = it },
            label = { Text("Velikost populacije") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        OutlinedTextField(
            value = sharedVm.crossRate.value,
            onValueChange = { sharedVm.crossRate.value = it },
            label = { Text("Verjetnost križanja") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        OutlinedTextField(
            value = sharedVm.mutRate.value,
            onValueChange = { sharedVm.mutRate.value = it },
            label = { Text("Verjetnost mutacije") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        OutlinedTextField(
            value = sharedVm.maxFes.value,
            onValueChange = { sharedVm.maxFes.value = it },
            label = { Text("Max FES") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        OutlinedTextField(
            value = sharedVm.repeats.value,
            onValueChange = { sharedVm.repeats.value = it },
            label = { Text("Število ponovitev") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Optimiziraj za:", style = MaterialTheme.typography.titleMedium)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            RadioButton(
                selected = sharedVm.optimizeChoice.value == 0,
                onClick = { sharedVm.optimizeChoice.value = 0 },
                enabled = !isLoading
            )
            Text("Čas")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            RadioButton(
                selected = sharedVm.optimizeChoice.value == 1,
                onClick = { sharedVm.optimizeChoice.value = 1 },
                enabled = !isLoading
            )
            Text("Razdalja")
        }

        Spacer(modifier = Modifier.weight(1f))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            Text("Izvajanje algoritma", style = MaterialTheme.typography.bodySmall)
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) { Text("Nazaj") }

                Button(
                    onClick = {
                        isLoading = true
                        sharedVm.runAlgorithm(onFinished = {
                            isLoading = false
                            onNext()
                        })
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Zaženi") }
            }
        }
    }
}