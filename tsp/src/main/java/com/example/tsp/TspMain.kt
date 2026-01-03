package com.example.tsp

fun testProblem(file: String, writeBestOnly: Boolean = true){
    RandomUtils.setSeedFromTime()
    val fileIO = java.io.File("CapsLock_${file.removeSuffix(".tsp")}.txt")
    fileIO.takeIf { it.exists() }?.delete()
    var leaveOut: Array<Int> = emptyArray()
    //var leaveOut: Array<Int> = arrayOf(101)
    val tsp = TSP(file, leaveOut)
    val ga = GeneticAlgorithm(
        tsp = tsp,
        popSize = 100,
        cr = 0.8,
        pm = 0.1,
        repeat = 30
    )

    val (best, bestList) = ga.run();
    println("Best distance = ${best.distance}")
    println("${best.startCity.index} -> ${best.getPath().joinToString(" -> ") { it!!.index.toString() }} -> ${best.startCity.index}")

    bestList.sortBy { it.distance }

    if (!writeBestOnly)
        for (i in bestList)
            i.writeToFile(fileIO)
    else
        best.writeToFile(fileIO)
}

fun main() {
    val problems: Array<String> = arrayOf("a280.tsp", "bays29.tsp", "dca1389.tsp", "eil101.tsp", "pr1002.tsp")

    for (i in problems)
        testProblem(i, false)
}
