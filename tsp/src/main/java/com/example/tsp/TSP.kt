package com.example.tsp

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.math.sqrt

class TSP(private val inputStream: InputStream, _leaveOutArray: Array<Int>, private var maxEvaluations: Int = -1) {

    enum class DistanceType { EUCLIDEAN, WEIGHTED }

    var name: String = ""
        private set

    lateinit var start: City
        private set

    val cities: MutableList<City> = mutableListOf()
    var leaveOutArray: Array<Int> = _leaveOutArray

    var numberOfCities: Int = 0
        private set

    lateinit var weights: Array<DoubleArray>
        private set

    var distanceType: DistanceType = DistanceType.EUCLIDEAN
        private set

    private var numberOfEvaluations: Int = 0

    init {
        loadData()
        numberOfEvaluations = 0
    }

    fun evaluate(tour: Tour) {
        val pathArr = tour.getPath()
        require(pathArr.size == numberOfCities) { "Tour dimension mismatch" }
        require(pathArr.all { it != null }) { "Tour contains null city" }

        var dist = 0.0
        dist += calculateDistance(start, pathArr[0]!!)

        for (i in 0 until numberOfCities) {
            dist += if (i + 1 < numberOfCities)
                calculateDistance(pathArr[i]!!, pathArr[i + 1]!!)
            else
                calculateDistance(pathArr[i]!!, start)
        }

        tour.distance = dist
        numberOfEvaluations++
    }

    private fun calculateDistance(from: City, to: City): Double {
        return when (distanceType) {
            DistanceType.EUCLIDEAN -> {
                val dx = from.x - to.x
                val dy = from.y - to.y
                sqrt(dx * dx + dy * dy)
            }
            DistanceType.WEIGHTED -> weights[from.index][to.index]
        }
    }

    fun generateTour(): Tour {
        val tour = Tour(numberOfCities, start)

        val arr = cities.toMutableList()
        for (i in arr.size - 1 downTo 1) {
            val j = RandomUtils.nextInt(i + 1)
            val tmp = arr[i]
            arr[i] = arr[j]
            arr[j] = tmp
        }

        for (i in 0 until numberOfCities) {
            tour.setCity(i, arr[i])
        }
        return tour
    }

    private fun loadData() {
        val lines = mutableListOf<String>()
        BufferedReader(InputStreamReader(inputStream)).use { br ->
            var line = br.readLine()
            while (line != null) {
                lines.add(line.trim())
                line = br.readLine()
            }
        }

        var dimension = 0
        var edgeWeightType = ""
        var edgeWeightFormat = ""

        for (l in lines) {
            if (l.startsWith("DIMENSION")) {
                dimension = l.substringAfter(":").trim().toInt()
                break
            }
        }

        val xArr = DoubleArray(dimension)
        val yArr = DoubleArray(dimension)
        val addrArr = Array(dimension) { "" }
        val matrixValues = mutableListOf<Double>()

        var readingCoords = false
        var readingMatrix = false
        var readingNames = false

        for (raw in lines) {
            val l = raw.trim()
            if (l.isEmpty()) continue

            when {
                l.startsWith("NAME") -> {
                    name = l.substringAfter(":").trim()
                    continue
                }
                l.startsWith("DIMENSION") -> continue
                l.startsWith("EDGE_WEIGHT_TYPE") -> {
                    edgeWeightType = l.substringAfter(":").trim()
                    continue
                }

                l.startsWith("EDGE_WEIGHT_FORMAT") -> {
                    edgeWeightFormat = l.substringAfter(":").trim()
                    continue
                }
                l.startsWith("DISPLAY_DATA_SECTION") || l.startsWith("NODE_COORD_SECTION") -> {
                    readingCoords = true
                    readingMatrix = false
                    readingNames = false
                    continue
                }
                l.startsWith("CITY_NAME_SECTION") -> {
                    readingNames = true
                    readingCoords = false
                    readingMatrix = false
                    continue
                }
                l.startsWith("EDGE_WEIGHT_SECTION") -> {
                    readingMatrix = true
                    readingCoords = false
                    readingNames = false
                    continue
                }
                l.endsWith("_SECTION") -> {
                    readingCoords = false
                    readingMatrix = false
                    readingNames = false
                    continue
                }

                l == "EOF" -> break
            }

            if (readingCoords) {
                val p = l.split("""\s+""".toRegex())
                if (p.size >= 3) {
                    val idx = p[0].toInt() - 1
                    if (idx in 0 until dimension) {
                        xArr[idx] = p[1].toDouble()
                        yArr[idx] = p[2].toDouble()
                    }
                }
            }

            if (readingNames) {
                val p = l.split(" ", limit = 2)
                if (p.size >= 2) {
                    val idx = p[0].toIntOrNull()?.minus(1) ?: -1
                    if (idx in 0 until dimension) {
                        addrArr[idx] = p[1].trim()
                    }
                }
            }

            if (readingMatrix) {
                l.split("""\s+""".toRegex())
                    .filter { it.isNotBlank() }
                    .forEach { matrixValues.add(it.toDouble()) }
            }
        }

        distanceType = if (edgeWeightType == "EUC_2D") DistanceType.EUCLIDEAN else DistanceType.WEIGHTED

        val builtCities = mutableListOf<City>()
        for (i in 0 until dimension) {
            if (i !in leaveOutArray) {
                builtCities.add(City(i, addrArr[i], xArr[i], yArr[i]))
            }
        }

        require(builtCities.isNotEmpty()) { "No cities loaded." }

        start = builtCities[0]

        cities.clear()
        cities.addAll(builtCities.drop(1))
        numberOfCities = cities.size

        if (maxEvaluations == -1) maxEvaluations = 1000 * numberOfCities

        if (distanceType == DistanceType.WEIGHTED) {
            require(edgeWeightFormat == "" || edgeWeightFormat == "FULL_MATRIX")
            val expected = dimension * dimension
            require(matrixValues.size >= expected)
            weights = Array(dimension) { DoubleArray(dimension) }
            var idx = 0
            for (i in 0 until dimension) {
                for (j in 0 until dimension) {
                    weights[i][j] = matrixValues[idx++]
                }
            }
        }
    }

    fun getMaxEvaluations(): Int = maxEvaluations

    fun getNumberOfEvaluations(): Int = numberOfEvaluations
}
