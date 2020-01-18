package com.piropaolo.anaume.domain

data class Meaning(
    val english: String
)

data class Onyomi(
    val romaji: String,
    val katakana: String
)

data class Strokes(
    val count: Int,
    val timings: List<Float>,
    val images: List<String>
)

data class Kunyomi(
    val romaji: String,
    val hiragana: String
)

data class Video(
    val poster: String,
    val mp4: String,
    val webm: String
)

data class Kanji(
    val character: String,
    val meaning: Meaning,
    val strokes: Strokes,
    val onyomi: Onyomi,
    val kunyomi: Kunyomi,
    val video: Video
)

data class Position(
    val hiragana: String,
    val romaji: String,
    val icon: String
)

data class Name(
    val hiragana: String,
    val romaji: String
)

data class Radical(
    val character: String,
    val strokes: Int,
    val image: String,
    val position: Position,
    val name: Name,
    val meaning: Meaning,
    val animation: List<String>
)

data class References(
    val grade: Int,
    val kodansha: String,
    val classic_nelson: String
)

data class Audio(
    val opus: String,
    val aac: String,
    val ogg: String,
    val mp3: String
)

data class Example(
    val japanese: String,
    val meaning: Meaning,
    val audio: Audio
)

data class KanjiDetails(
    val kanji: Kanji,
    val radical: Radical,
    val references: References,
    val examples: List<Example>
)

data class PlainKanji(
    val character: String,
    val stroke: Int
)

data class PlainRadical(
    val character: String,
    val stroke: Int,
    val order: Int
)

data class GradeKanji(
    val kanji: PlainKanji,
    val radical: PlainRadical
)
