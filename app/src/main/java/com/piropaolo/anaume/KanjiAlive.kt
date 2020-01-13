package com.piropaolo.anaume

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.reflect.ParameterizedType
import kotlin.random.Random

class KanjiAlive(
    private val client: OkHttpClient,
    private val moshi: Moshi
) {

    suspend fun getKanjiDetails(kanji: String) =
        withContext(Dispatchers.Default) {
            val request = Request.Builder()
                .url("https://kanjialive-api.p.rapidapi.com/api/public/kanji/$kanji")
                .header("X-RapidAPI-Host", "kanjialive-api.p.rapidapi.com")
                .header("X-RapidAPI-Key", "509fe509d2msh2b5e520111ae506p14ffeejsn675c7a6af087")
                .build()
            val adapter: JsonAdapter<KanjiDetails> = moshi.adapter(KanjiDetails::class.java)

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                adapter.fromJson(response.body!!.source())
            }
        }

    suspend fun getKanjisFromGrade(grade: Int) =
        withContext(Dispatchers.Default) {
            val request = Request.Builder()
                .url("https://kanjialive-api.p.rapidapi.com/api/public/search/advanced?grade=$grade")
                .header("X-RapidAPI-Host", "kanjialive-api.p.rapidapi.com")
                .header("X-RapidAPI-Key", "509fe509d2msh2b5e520111ae506p14ffeejsn675c7a6af087")
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

    suspend fun getExamples(kanji: String) =
        withContext(Dispatchers.Default) {
            getKanjiDetails(kanji)?.examples
                ?.map { it.japanese.split("（").first() }
        }

    suspend fun getQuizMap(grade: Int) =
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
}