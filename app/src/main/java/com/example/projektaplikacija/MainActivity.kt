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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val apiKey = "YOUR_GOOGLE_API_KEY"

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

        Text(text = "Direct4.me ATSP Export")

        Button(onClick = {
            status = "Running pipeline..."

            activity.lifecycleScope.launch {

                try {
                    val assetFiles = activity.assets.list("")
                    if (assetFiles?.contains("Direct4me-seznam lokacij.pdf") != true) {
                        status = "PDF file not found in assets!"
                        return@launch
                    }

                    val pdfText = withContext(Dispatchers.IO) {
                        readPdfFromAssets(activity, "Direct4me-seznam lokacij.pdf")
                    }

                    val rawLocations = parseLocations(pdfText)
                    if (rawLocations.isEmpty()) {
                        status = "No valid locations found in PDF"
                        return@launch
                    }

                    val geocoder = GeocodingClient(apiKey)
                    val locations = mutableListOf<Location>()

                    withContext(Dispatchers.IO) {
                        rawLocations.forEach { raw ->

                            val fullAddress =
                                com.example.projektaplikacija.util.cleanAddress(raw.city, raw.address)

                            val coords = geocoder.geocode(fullAddress)

                            if (coords != null) {
                                val (lat, lon) = coords
                                locations.add(
                                    Location(
                                        id = raw.id,
                                        city = raw.city,
                                        address = fullAddress,
                                        lat = lat,
                                        lon = lon
                                    )
                                )
                            } else {
                                println("❌ Geocoding failed: $fullAddress")
                            }

                            Thread.sleep(300)
                        }
                    }

                    val matrixClient = DistanceMatrixClient(apiKey)
                    val (distanceMatrix, timeMatrix) = try {
                        withContext(Dispatchers.IO) {
                            matrixClient.fetchMatrices(locations)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        status = "Failed to fetch distance/time matrices: ${e.message}"
                        return@launch
                    }

                    val distanceFile =
                        activity.getExternalFilesDir(null)?.resolve("direct4me_distance.atsp")
                    val timeFile =
                        activity.getExternalFilesDir(null)?.resolve("direct4me_time.atsp")

                    distanceFile?.let { TspExporter.exportAtsp(activity, it.name, distanceMatrix) }
                    timeFile?.let { TspExporter.exportAtsp(activity, it.name, timeMatrix) }

                    status =
                        "ATSP files exported to:\n${distanceFile?.absolutePath}\n${timeFile?.absolutePath}"

                } catch (e: Exception) {
                    status = "Unexpected error: ${e.message}"
                    e.printStackTrace()
                }
            }

        }) {
            Text("Run full pipeline")
        }

        Text(text = status)
    }
}

