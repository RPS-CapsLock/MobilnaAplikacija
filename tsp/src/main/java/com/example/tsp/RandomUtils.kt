package com.example.tsp

import java.util.Random

object RandomUtils {

    private var seed: Long = 123
    private val random = Random(seed)

    fun setSeed(seed: Long) {
        this.seed = seed
        random.setSeed(seed)
    }

    fun setSeedFromTime() {
        setSeed(System.currentTimeMillis())
    }

    fun nextDouble(): Double = random.nextDouble()

    fun nextInt(upperBound: Int): Int = random.nextInt(upperBound)

    fun nextInt(lowerBound: Int, upperBound: Int): Int =
        lowerBound + random.nextInt(upperBound - lowerBound)
}
