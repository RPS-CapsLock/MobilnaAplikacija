package com.example.projektaplikacija.api

import com.example.projektaplikacija.model.Location
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class DistanceMatrixClient(private val apiKey: String) {

    private val client = OkHttpClient()

    fun fetchMatrices(
        locations: List<Location>
    ): Pair<Array<IntArray>, Array<IntArray>> {

        val coords = locations.joinToString("|") { "${it.lat},${it.lon}" }

        val url =
            "https://maps.googleapis.com/maps/api/distancematrix/json" +
                    "?origins=$coords" +
                    "&destinations=$coords" +
                    "&mode=driving" +
                    "&units=metric" +
                    "&key=$apiKey"

        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            val json = JSONObject(response.body!!.string())
            val rows = json.getJSONArray("rows")

            val n = locations.size
            val dist = Array(n) { IntArray(n) }
            val time = Array(n) { IntArray(n) }

            for (i in 0 until n) {
                val elements = rows.getJSONObject(i).getJSONArray("elements")
                for (j in 0 until n) {
                    val e = elements.getJSONObject(j)
                    dist[i][j] = e.getJSONObject("distance").getInt("value")
                    time[i][j] = e.getJSONObject("duration").getInt("value")
                }
            }
            return dist to time
        }
    }
}
