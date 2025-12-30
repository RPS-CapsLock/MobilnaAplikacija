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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AlgoSettingsScreen( onBack: () -> Unit, onNext: () -> Unit ) {

    var np by remember { mutableStateOf("100") }
    var pc by remember { mutableStateOf("0.8") }
    var pm by remember { mutableStateOf("0.1") }
    var maxFes by remember { mutableStateOf("3000") }
    var repeats by remember { mutableStateOf("30") }
    var optimizeChoice by remember { mutableStateOf(0) }

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
            value = np,
            onValueChange = { np = it },
            label = { Text("Velikost populacije") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = pc,
            onValueChange = { pc = it },
            label = { Text("Verjetnost križanja") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = pm,
            onValueChange = { pm = it },
            label = { Text("Verjetnost mutacije") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = maxFes,
            onValueChange = { maxFes = it },
            label = { Text("Max FES") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = repeats,
            onValueChange = { repeats = it },
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
                selected = optimizeChoice == 0,
                onClick = { optimizeChoice = 0 }
            )
            Text("Čas")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            RadioButton(
                selected = optimizeChoice == 1,
                onClick = { optimizeChoice = 1 }
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