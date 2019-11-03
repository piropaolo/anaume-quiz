package com.piropaolo.anaume

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import java.io.IOException
import java.lang.reflect.ParameterizedType
import kotlin.random.Random

object KanjiManager {

    private val client = OkHttpClient()
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    fun getRandomKanji(grade: String): String? {
        val request = Request.Builder()
            .url("https://kanjiapi.dev/v1/kanji/grade-$grade")
            .build()
        val type: ParameterizedType =
            Types.newParameterizedType(List::class.java, String::class.java)
        val adapter: JsonAdapter<List<String>> = moshi.adapter(type)
        var result: String? = null

        /*client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val kanjis: List<String>? = adapter.fromJson(response.body!!.source())
            return kanjis?.get(Random.nextInt(kanjis.size))
        }*/

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    val kanjis: List<String>? = adapter.fromJson(response.body!!.source())
                    result = kanjis?.get(Random.nextInt(kanjis.size))
                }
            }
        })

        return result
    }

    fun getWords(character: String): List<Word>? {
        val request = Request.Builder()
            .url("https://kanjiapi.dev/v1/words/$character")
            .build()
        val type: ParameterizedType = Types.newParameterizedType(List::class.java, Word::class.java)
        val adapter: JsonAdapter<List<Word>> = moshi.adapter(type)
        var result: List<Word> = emptyList()

        /*client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return adapter.fromJson(response.body!!.source())
        }*/


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    result = adapter.fromJson(response.body!!.source())!!
                }
            }
        })
        
        return result
    }

    fun List<Word>.toVariants(): List<Variant> = this.flatMap { it.variants }

    fun List<Variant>.toExamples(): List<String> = this.filter { it.written.length == 2 }
        .map { it.written }
        .distinct()

    fun List<String>.filterExamples(character: String): List<String> =
        this.filter { it.contains(character) }

    fun getQuiz(grade: String): Map<Int, String> {
        val character = getRandomKanji(grade)
        val examples =
            character?.let { getWords(it)?.toVariants()?.toExamples()?.filterExamples(it) }

        val results = HashMap<Int, String>()
        while (results.size < 2) {
            val candidate = examples?.get(Random.nextInt(examples.size))
            candidate?.let {
                if (it[1] == character[0] && !results.containsValue(it))
                    results[results.size] = it
            }
        }
        while (results.size < 4) {
            val candidate = examples?.get(Random.nextInt(examples.size))
            candidate?.let {
                if (it[0] == character[0] && !results.containsValue(it))
                    results[results.size] = it
            }
        }
        return results
    }

    @JsonClass(generateAdapter = true)
    data class Word(
        val meanings: List<Meaning>,
        val variants: List<Variant>
    )

    @JsonClass(generateAdapter = true)
    data class Meaning(
        val glosses: List<String>
    )

    @JsonClass(generateAdapter = true)
    data class Variant(
        val written: String,
        val pronounced: String,
        val priorities: List<String>
    )
}
