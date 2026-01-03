package com.example.tsp

fun main() {
    RandomUtils.setSeedFromTime()
    //var leaveOut: Array<Int> = emptyArray()
    var leaveOut: Array<Int> = arrayOf(101)
    val tsp = TSP("pr1002.tsp", leaveOut, 100_000)
    val ga = GeneticAlgorithm(
        tsp = tsp,
        popSize = 100,
        cr = 0.8,
        pm = 0.1
    )

    val best = ga.run()
    println("Best distance = ${best.distance}")
    println("${best.startCityIndex}, ${best.getPath().joinToString(", ") { it!!.index.toString() }} ${best.startCityIndex}")
}
