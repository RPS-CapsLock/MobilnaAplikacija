package com.example.tsp

class Tour(val dimension: Int, val startCityIndex: Int) {

    var distance: Double = Double.MAX_VALUE
    private val _path: Array<City?> = arrayOfNulls(dimension)

    constructor(other: Tour) : this(other.dimension, other.startCityIndex) {
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
}
