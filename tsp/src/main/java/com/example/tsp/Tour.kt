package com.example.tsp

class Tour(val dimension: Int, val startCity: City) {

    var distance: Double = Double.MAX_VALUE
    private val _path: Array<City?> = arrayOfNulls(dimension)

    constructor(other: Tour) : this(other.dimension, other.startCity) {
        distance = other.distance
        for (i in 0 until dimension) {
            _path[i] = other._path[i]
        }
    }

    fun clone(): Tour = Tour(this)

    fun getPath(): Array<City?> = _path

    fun setPath(path: Array<City?>) {
        for (i in 0 until dimension) {
            _path[i] = path[i]
        }
        distance = Double.MAX_VALUE
    }

    fun setCity(index: Int, city: City) {
        _path[index] = city
        distance = Double.MAX_VALUE
    }

    fun writeToFile(file: java.io.File) {
        file.parentFile?.mkdirs()

        java.io.FileWriter(file, true).buffered().use { writer ->
            writer.write("dimension=$dimension\n")
            writer.write("startCityIndex=$startCity\n")
            writer.write("distance=$distance\n")

            writer.write("path:\n")
            for (i in 0 until dimension) {
                val city: City? = getPath()[i]
                val prev: City? = if (i > 0) getPath()[i - 1] else startCity
                if (city != null) {
                    writer.write("${prev?.index} -> ${city.index}\n")
                } else {
                    writer.write("${prev?.index} -> ${startCity.index}\n")
                }
            }
            writer.write("\n")
        }
    }
}
