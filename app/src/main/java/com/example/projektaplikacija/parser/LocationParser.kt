package com.example.projektaplikacija.parser

import com.example.projektaplikacija.model.RawLocation

fun parseLocations(text: String): List<RawLocation> {
    val result = mutableListOf<RawLocation>()
    var id = 0

    text.lines().forEach { line ->
        val trimmed = line.trim()
        if (trimmed.isEmpty()) return@forEach
        if (trimmed.first().isDigit()) return@forEach

        val parts = trimmed.split(Regex("\\s+"))
        if (parts.size < 6) return@forEach

        val postal = parts.last()
        if (!postal.all { it.isDigit() }) return@forEach

        val city = parts.first()
        val address = parts.subList(1, parts.size - 2).joinToString(" ")

        result.add(
            RawLocation(
                id = ++id,
                city = city,
                address = "$address, $postal, Slovenia"
            )
        )
    }
    return result
}
