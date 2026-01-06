package com.example.projektaplikacija

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.projektaplikacija.api.DistanceMatrixClient
import com.example.projektaplikacija.api.GeocodingClient
import com.example.projektaplikacija.export.TspExporter
import com.example.projektaplikacija.model.Location
import com.example.projektaplikacija.parser.parseLocations
import com.example.projektaplikacija.pdf.readPdfFromAssets
import com.example.projektaplikacija.ui.theme.ProjektAplikacijaTheme
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val apiKey = "Key"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        PDFBoxResourceLoader.init(applicationContext)

        setContent {
            ProjektAplikacijaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    PipelineScreen(
                        modifier = Modifier.padding(padding),
                        activity = this,
                        apiKey = apiKey
                    )
                }
            }
        }
    }
}

@Composable
fun PipelineScreen(
    modifier: Modifier = Modifier,
    activity: ComponentActivity,
    apiKey: String
) {
    var status by remember { mutableStateOf("Idle") }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text("Direct4.me ATSP Export")

        Button(onClick = {
            status = "Running pipeline..."

            activity.lifecycleScope.launch {
                try {
                    val pdfText = withContext(Dispatchers.IO) {
                        readPdfFromAssets(activity, "Direct4me-seznam lokacij.pdf")
                    }

                    val raw = parseLocations(pdfText)
                    val geocoder = GeocodingClient(apiKey)

                    val locations = mutableListOf<Location>()

                    withContext(Dispatchers.IO) {
                        raw.forEach {
                            val full =
                                "${it.address}, ${it.city}, Slovenia"
                            geocoder.geocode(full)?.let { (lat, lon) ->
                                locations.add(
                                    Location(it.id, it.city, full, lat, lon)
                                )
                            }
                            delay(300)
                        }
                    }

                    if (locations.size < 2) {
                        status = "Not enough geocoded locations"
                        return@launch
                    }

                    val limited = locations.take(15) // SAFE DEFAULT

                    val matrixClient = DistanceMatrixClient(apiKey)
                    val (dist, time) = withContext(Dispatchers.IO) {
                        matrixClient.fetchMatrices(limited)
                    }

                    val dFile =
                        activity.getExternalFilesDir(null)!!.resolve("distance.atsp")
                    val tFile =
                        activity.getExternalFilesDir(null)!!.resolve("time.atsp")

                    TspExporter.exportAtsp(activity, dFile.name, dist)
                    TspExporter.exportAtsp(activity, tFile.name, time)

                    status = "Exported:\n${dFile.absolutePath}\n${tFile.absolutePath}"

                } catch (e: Exception) {
                    status = "Error: ${e.message}"
                    e.printStackTrace()
                }
            }
        }) {
            Text("Run full pipeline")
        }

        Text(status)
    }
}
