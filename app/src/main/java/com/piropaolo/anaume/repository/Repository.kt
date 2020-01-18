package com.piropaolo.anaume.repository

object Repository {

    var userName: String = setOf(
        "Zlatan",
        "Theo",
        "Gigio",
        "Alessio",
        "Rafael",
        "Lucas",
        "Ricardo",
        "Giacomo",
        "Andrea",
        "Davide"
    ).shuffled().first()

    var score: Int = 0
}