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