package com.example.projektaplikacija.ui.theme.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class AlgoSettingsViewModel : ViewModel() {
    val np = mutableStateOf("100")
    val pc = mutableStateOf("0.8")
    val pm = mutableStateOf("0.1")
    val maxFes = mutableStateOf("3000")
    val repeats = mutableStateOf("30")
    val optimizeChoice = mutableStateOf(0)
}

@Composable
fun AlgoSettingsScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    vm: AlgoSettingsViewModel = viewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nastavitve algoritma",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = vm.np.value,
            onValueChange = { vm.np.value = it },
            label = { Text("Velikost populacije") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = vm.pc.value,
            onValueChange = { vm.pc.value = it },
            label = { Text("Verjetnost križanja") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = vm.pm.value,
            onValueChange = { vm.pm.value = it },
            label = { Text("Verjetnost mutacije") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = vm.maxFes.value,
            onValueChange = { vm.maxFes.value = it },
            label = { Text("Max FES") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = vm.repeats.value,
            onValueChange = { vm.repeats.value = it },
            label = { Text("Število ponovitev") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Optimiziraj", style = MaterialTheme.typography.titleMedium)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            RadioButton(
                selected = vm.optimizeChoice.value == 0,
                onClick = { vm.optimizeChoice.value = 0 }
            )
            Text("Čas")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            RadioButton(
                selected = vm.optimizeChoice.value == 1,
                onClick = { vm.optimizeChoice.value = 1 }
            )
            Text("Dolžina poti")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) { Text("Nazaj") }

            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f)
            ) { Text("Zaženi") }
        }
    }
}