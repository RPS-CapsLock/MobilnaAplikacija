package com.example.projektaplikacija.util

fun cleanAddress(city: String, address: String): String {
    val cleaned = address
        .replace(Regex("Ob .*"), "")
        .replace(Regex("Multispace .*"), "")
        .replace(Regex("Gostilna .*"), "")
        .replace(Regex("\\s{2,}"), " ")
        .trim()

    return "$cleaned, $city, Slovenia"
}
