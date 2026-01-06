package com.example.projektaplikacija.api

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder

class GeocodingClient(private val apiKey: String) {

    private val client = OkHttpClient()

    fun geocode(address: String): Pair<Double, Double>? {
        val encoded = URLEncoder.encode(address, "UTF-8")
        val url =
            "https://maps.googleapis.com/maps/api/geocode/json?address=$encoded&key=$apiKey"

        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string() ?: return null
            val json = JSONObject(body)

            if (json.getString("status") != "OK") return null

            val results = json.getJSONArray("results")
            if (results.length() == 0) return null

            val loc = results.getJSONObject(0)
                .getJSONObject("geometry")
                .getJSONObject("location")

            return loc.getDouble("lat") to loc.getDouble("lng")
        }
    }
}
