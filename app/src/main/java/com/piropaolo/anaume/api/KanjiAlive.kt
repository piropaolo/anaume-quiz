package com.piropaolo.anaume.api

import android.annotation.SuppressLint
import com.piropaolo.anaume.domain.GradeKanji
import com.piropaolo.anaume.domain.KanjiDetails
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.reflect.ParameterizedType
import kotlin.random.Random

class KanjiAlive(
    private val client: OkHttpClient,
    private val moshi: Moshi
) {
    companion object {
        val headers: Headers = Headers.Builder()
            .add("X-RapidAPI-Host", "kanjialive-api.p.rapidapi.com")
            .add("X-RapidAPI-Key", "509fe509d2msh2b5e520111ae506p14ffeejsn675c7a6af087")
            .build()
    }

    suspend fun getKanjiDetails(kanji: String): KanjiDetails? =
        withContext(Dispatchers.Default) {
            val request = Request.Builder()
                .url("https://kanjialive-api.p.rapidapi.com/api/public/kanji/$kanji")
                .headers(headers)
                .build()
            val adapter: JsonAdapter<KanjiDetails> = moshi.adapter(
                KanjiDetails::class.java
            )

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                adapter.fromJson(response.body!!.source())
            }
        }

    suspend fun getKanjisFromGrade(grade: Int): List<GradeKanji>? =
        withContext(Dispatchers.Default) {
            val request = Request.Builder()
                .url("https://kanjialive-api.p.rapidapi.com/api/public/search/advanced?grade=$grade")
                .headers(headers)
                .build()
            val type: ParameterizedType =
                Types.newParameterizedType(List::class.java, GradeKanji::class.java)
            val adapter: JsonAdapter<List<GradeKanji>> = moshi.adapter(type)

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                adapter.fromJson(response.body!!.source())
            }
        }

    suspend fun getRandomKanji(grade: Int): String? =
        withContext(Dispatchers.Default) {
            val kanjis = getKanjisFromGrade(grade)
            kanjis?.get(Random.nextInt(kanjis.size))?.kanji?.character
        }

    suspend fun getExamples(kanji: String): Set<String>? =
        withContext(Dispatchers.Default) {
            getKanjiDetails(kanji)?.examples
                ?.map { it.japanese.split("（").first() }?.toSet()
        }

    @SuppressLint("UseSparseArrays")
    suspend fun getQuizMap(grade: Int): Map<Int, String> =
        withContext(Dispatchers.Default) {
            val results = HashMap<Int, String>()
            var lefts: List<String>?
            var rights: List<String>?

            do {
                val kanji = getRandomKanji(grade)
                val examples = kanji?.let { getExamples(it) }
                lefts = examples?.filter { it.length == 2 && it[0] == kanji[0] }
                rights = examples?.filter { it.length == 2 && it[1] == kanji[0] }
            } while (lefts?.size ?: 0 < 2 || rights?.size ?: 0 < 2)

            rights?.shuffled()?.take(2)?.forEach { results[results.size] = it }
            lefts?.shuffled()?.take(2)?.forEach { results[results.size] = it }
            results
        }

    suspend fun checkSolution(kanji: String, words: Set<String>): Boolean =
        withContext(Dispatchers.Default) {
            getExamples(kanji)?.containsAll(words) ?: false
        }
}