package com.piropaolo.anaume

data class Word(
    val meanings: List<Meaning>,
    val variants: List<Variant>
)

data class Meaning(
    val glosses: List<String>
)

data class Variant(
    val written: String,
    val pronounced: String,
    val priorities: List<String>
)

data class Kanji(
    val kanji: String,
    val grade: Int,
    val stroke_count: Int,
    val meanings: List<String>,
    val kun_readings: List<String>,
    val on_readings: List<String>,
    val name_readings: List<String>,
    val jlpt: String,
    val unicode: String
)