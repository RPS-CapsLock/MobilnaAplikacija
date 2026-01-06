package com.example.projektaplikacija.api

import com.example.projektaplikacija.model.Location
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class DistanceMatrixClient(private val apiKey: String) {

    private val client = OkHttpClient()
    private val MAX_ELEMENTS = 100

    fun fetchMatrices(
        locations: List<Location>
    ): Pair<Array<IntArray>, Array<IntArray>> {

        val n = locations.size
        require(n > 1) { "Need at least 2 locations" }

        val distance = Array(n) { IntArray(n) }
        val duration = Array(n) { IntArray(n) }

        val chunkSize = MAX_ELEMENTS / n // dynamic safe chunk

        for (i in 0 until n step chunkSize) {
            val originChunk = locations.subList(i, minOf(i + chunkSize, n))

            val origins = originChunk.joinToString("|") { "${it.lat},${it.lon}" }
            val destinations = locations.joinToString("|") { "${it.lat},${it.lon}" }

            val url =
                "https://maps.googleapis.com/maps/api/distancematrix/json" +
                        "?origins=$origins" +
                        "&destinations=$destinations" +
                        "&key=$apiKey"

            val request = Request.Builder().url(url).build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: error("Empty response")
                val json = JSONObject(body)

                if (json.getString("status") != "OK") {
                    error("Distance Matrix API error:\n$body")
                }

                val rows = json.getJSONArray("rows")

                for (r in 0 until rows.length()) {
                    val elements = rows.getJSONObject(r).getJSONArray("elements")
                    for (c in 0 until elements.length()) {
                        val element = elements.getJSONObject(c)

                        val rowIndex = i + r
                        if (element.getString("status") == "OK") {
                            distance[rowIndex][c] =
                                element.getJSONObject("distance").getInt("value")
                            duration[rowIndex][c] =
                                element.getJSONObject("duration").getInt("value")
                        } else {
                            distance[rowIndex][c] = Int.MAX_VALUE
                            duration[rowIndex][c] = Int.MAX_VALUE
                        }
                    }
                }
            }

            Thread.sleep(300) // API friendly
        }

        return distance to duration
    }
}
