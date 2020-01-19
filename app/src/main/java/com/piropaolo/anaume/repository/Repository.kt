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
    var userScore: Int = 0

    var opponentName: String = "Opponent"
    var opponentScore: Int = 0
}