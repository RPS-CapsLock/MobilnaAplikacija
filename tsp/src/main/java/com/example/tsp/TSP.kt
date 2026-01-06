package com.example.tsp

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.math.sqrt

class TSP(path: String, _leaveOutArray: Array<Int>, private var maxEvaluations: Int = -1) {

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
        loadData(path)
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

    private fun loadData(path: String) {
        val inputStream: InputStream = javaClass.classLoader?.getResourceAsStream(path)
            ?: error("File $path not found in resources (put it in src/main/resources)")

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

        var readingCoords = false
        var readingMatrix = false

        val allCities = mutableListOf<City>()
        val matrixValues = mutableListOf<Double>()

        for (raw in lines) {
            val l = raw.trim()
            if (l.isEmpty()) continue

            when {
                l.startsWith("NAME") -> {
                    name = l.substringAfter(":").trim()
                    continue
                }

                l.startsWith("DIMENSION") -> {
                    dimension = l.substringAfter(":").trim().toInt()
                    continue
                }

                l.startsWith("EDGE_WEIGHT_TYPE") -> {
                    edgeWeightType = l.substringAfter(":").trim()
                    continue
                }

                l.startsWith("EDGE_WEIGHT_FORMAT") -> {
                    edgeWeightFormat = l.substringAfter(":").trim()
                    continue
                }

                l.startsWith("NODE_COORD_SECTION") -> {
                    readingCoords = true
                    readingMatrix = false
                    continue
                }

                l.startsWith("EDGE_WEIGHT_SECTION") -> {
                    readingMatrix = true
                    readingCoords = false
                    continue
                }

                l.endsWith("_SECTION") && !l.startsWith("NODE_COORD_SECTION") && !l.startsWith("EDGE_WEIGHT_SECTION") -> {
                    readingCoords = false
                    readingMatrix = false
                    continue
                }

                l == "EOF" -> break
            }

            if (readingCoords) {
                val p = l.split("""\s+""".toRegex())
                if (p.size >= 3 && (p[0].toInt() - 1) !in leaveOutArray) {
                    allCities.add(
                        City(
                            index = p[0].toInt() - 1,
                            x = p[1].toDouble(),
                            y = p[2].toDouble()
                        )
                    )
                }
            }

            if (readingMatrix) {
                l.split("""\s+""".toRegex())
                    .filter { it.isNotBlank() }
                    .forEach { matrixValues.add(it.toDouble()) }
            }
        }

        distanceType = if (edgeWeightType == "EUC_2D") DistanceType.EUCLIDEAN else DistanceType.WEIGHTED

        val builtCities: List<City> =
            if (distanceType == DistanceType.WEIGHTED) {
                require(dimension > 0) { "DIMENSION missing or invalid" }
                List(dimension) { i -> City(index = i, x = 0.0, y = 0.0) }
            } else {
                require(allCities.size == (dimension - leaveOutArray.size) || dimension == 0) {
                    "NODE_COORD_SECTION parsed ${allCities.size} cities, DIMENSION says $dimension"
                }
                allCities
            }

        require(builtCities.isNotEmpty()) { "No cities loaded. Check TSPLIB file format." }

        start = builtCities[0]

        cities.clear()
        cities.addAll(builtCities.drop(1))
        numberOfCities = cities.size

        if (maxEvaluations == -1)
            maxEvaluations = 1000 * numberOfCities

        if (distanceType == DistanceType.WEIGHTED) {
            require(edgeWeightFormat == "" || edgeWeightFormat == "FULL_MATRIX") {
                "Only FULL_MATRIX is supported for EXPLICIT. Found: $edgeWeightFormat"
            }
            val expected = dimension * dimension
            require(matrixValues.size >= expected) {
                "EDGE_WEIGHT_SECTION has ${matrixValues.size} values, expected $expected (FULL_MATRIX)"
            }
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
