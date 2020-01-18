package com.piropaolo.anaume

import com.piropaolo.anaume.api.KanjiAlive
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient

suspend fun main() {

    val client = OkHttpClient()
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    val kanjiAlive = KanjiAlive(client, moshi)

    val quiz = kanjiAlive.getQuizMap(1)
    println("quiz = ${quiz}")
}