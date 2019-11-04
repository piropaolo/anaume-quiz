package com.piropaolo.anaume

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient

suspend fun main() {

    val client = OkHttpClient()
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    val kanjiApi = KanjiApi(client, moshi)

    val randomKanji = kanjiApi.getRandomKanji("1")
    println("randomKanji = ${randomKanji}")
}