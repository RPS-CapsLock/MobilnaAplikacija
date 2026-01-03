package com.example.tsp

import kotlin.math.max
import kotlin.math.min

class GeneticAlgorithm(
    private val tsp: TSP,
    private val popSize: Int,
    private val cr: Double,
    private val pm: Double
) {

    private val population = ArrayList<Tour>(popSize)
    private val offspring = ArrayList<Tour>(popSize)

    fun run(): Tour {
        population.clear()
        offspring.clear()

        repeat(popSize) {
            val t = tsp.generateTour()
            tsp.evaluate(t)
            population.add(t)
        }

        var best = population.minBy { it.distance }.clone()

        while (tsp.getNumberOfEvaluations() < tsp.getMaxEvaluations()) {
            offspring.add(best.clone())

            while (offspring.size < popSize) {
                val parent1 = tournamentSelection()
                val parent2 = tournamentSelectionDifferentFrom(parent1)

                if (RandomUtils.nextDouble() < cr) {
                    val (c1, c2) = pmx(parent1, parent2)
                    offspring.add(c1)
                    if (offspring.size < popSize) offspring.add(c2)
                } else {
                    offspring.add(parent1.clone())
                    if (offspring.size < popSize) offspring.add(parent2.clone())
                }
            }

            for (i in 1 until offspring.size) {
                if (RandomUtils.nextDouble() < pm) {
                    swapMutation(offspring[i])
                }
            }

            for (t in offspring) {
                if (t.distance == Double.MAX_VALUE) {
                    tsp.evaluate(t)
                }
                if (t.distance < best.distance) {
                    best = t.clone()
                }
            }

            population.clear()
            population.addAll(offspring)
            offspring.clear()
        }

        return best
    }

    private fun tournamentSelection(): Tour {
        val a = RandomUtils.nextInt(population.size)
        var b = RandomUtils.nextInt(population.size)
        while (b == a) b = RandomUtils.nextInt(population.size)

        val t1 = population[a]
        val t2 = population[b]
        return if (t1.distance <= t2.distance) t1 else t2
    }

    private fun tournamentSelectionDifferentFrom(other: Tour): Tour {
        var t = tournamentSelection()
        var guard = 0
        while (t === other && guard < 10) {
            t = tournamentSelection()
            guard++
        }
        return t
    }

    private fun swapMutation(off: Tour) {
        val path = off.getPath()
        val i = RandomUtils.nextInt(off.dimension)
        var j = RandomUtils.nextInt(off.dimension)
        while (j == i) j = RandomUtils.nextInt(off.dimension)

        val tmp = path[i]
        path[i] = path[j]
        path[j] = tmp

        off.distance = Double.MAX_VALUE
    }

    private fun pmx(p1: Tour, p2: Tour): Pair<Tour, Tour> {
        val size = p1.dimension
        val a = RandomUtils.nextInt(size)
        val b = RandomUtils.nextInt(size)
        val from = min(a, b)
        val to = max(a, b)

        val p1Idx = p1.getPath().map { it!!.index }.toIntArray()
        val p2Idx = p2.getPath().map { it!!.index }.toIntArray()

        val c1Idx = pmxOne(p1Idx, p2Idx, from, to)
        val c2Idx = pmxOne(p2Idx, p1Idx, from, to)

        val c1 = Tour(size, 0)
        val c2 = Tour(size, 0)

        val indexToCity = HashMap<Int, City>(tsp.cities.size + 1)
        indexToCity[tsp.start.index] = tsp.start
        for (c in tsp.cities) indexToCity[c.index] = c

        for (i in 0 until size) {
            c1.setCity(i, indexToCity[c1Idx[i]]!!)
            c2.setCity(i, indexToCity[c2Idx[i]]!!)
        }

        return Pair(c1, c2)
    }

    private fun pmxOne(a: IntArray, b: IntArray, from: Int, to: Int): IntArray {
        val size = a.size
        val child = IntArray(size) { -1 }

        for (i in from..to) child[i] = a[i]

        for (i in from..to) {
            val v = b[i]
            if (!child.contains(v)) {
                var pos = i
                while (pos in from..to) {
                    val mapped = a[pos]
                    pos = b.indexOf(mapped)
                }
                child[pos] = v
            }
        }

        for (i in 0 until size) {
            if (child[i] == -1) child[i] = b[i]
        }

        return child
    }
}
