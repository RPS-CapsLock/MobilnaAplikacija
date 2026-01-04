package com.example.projektaplikacija.utils

import com.google.android.gms.maps.model.LatLng
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

data class OsrmRoute(
    val geometry: List<LatLng>,
    val distanceMeters: Double,
    val durationSeconds: Double
)

object OsrmService {
    private val http = OkHttpClient()

    fun routeGeoJson(stops: List<LatLng>): OsrmRoute {
        require(stops.size >= 2) { "Need at least 2 points" }

        val coords = stops.joinToString(";") { "${it.longitude},${it.latitude}" }
        val url = "https://router.project-osrm.org/route/v1/driving/$coords?overview=full&geometries=geojson"

        val req = Request.Builder().url(url).build()

        val body = http.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) error("OSRM HTTP ${resp.code}")
            resp.body?.string() ?: error("Empty body")
        }

        val json = JSONObject(body)
        val route0 = json.getJSONArray("routes").getJSONObject(0)

        val distance = route0.getDouble("distance")
        val duration = route0.getDouble("duration")

        val geom = route0.getJSONObject("geometry")
        val coordsArr = geom.getJSONArray("coordinates")

        val points = ArrayList<LatLng>(coordsArr.length())
        for (i in 0 until coordsArr.length()) {
            val c = coordsArr.getJSONArray(i)
            val lon = c.getDouble(0)
            val lat = c.getDouble(1)
            points.add(LatLng(lat, lon))
        }

        return OsrmRoute(points, distance, duration)
    }
}
