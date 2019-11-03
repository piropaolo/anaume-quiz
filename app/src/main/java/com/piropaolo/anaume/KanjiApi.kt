package com.piropaolo.anaume

import android.widget.TextView
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

class KanjiApi(
    private val client: OkHttpClient,
    private val moshi: Moshi,
    private val mainActivity: MainActivity
) {

    companion object {

        private fun List<Word>.toVariants(): List<Variant> = this.flatMap { it.variants }

        private fun List<Variant>.toExamples(): List<String> =
            this.filter { it.written.length == 2 }
                .map { it.written }
                .distinct()

        private fun List<String>.filterExamples(character: String): List<String> =
            this.filter { it.contains(character) }
    }

    suspend fun getRandomKanji(grade: String): String? =
        withContext(Dispatchers.Default) {
            val request = Request.Builder()
                .url("https://kanjiapi.dev/v1/kanji/grade-$grade")
                .build()
            val type: ParameterizedType =
                Types.newParameterizedType(List::class.java, String::class.java)
            val adapter: JsonAdapter<List<String>> = moshi.adapter(type)

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val kanjis: List<String>? = adapter.fromJson(response.body!!.source())
                return@withContext kanjis?.get(Random.nextInt(kanjis.size))
            }
        }

    suspend fun getWords(character: String): List<Word>? =
        withContext(Dispatchers.Default) {
            val request = Request.Builder()
                .url("https://kanjiapi.dev/v1/words/$character")
                .build()
            val type: ParameterizedType =
                Types.newParameterizedType(List::class.java, Word::class.java)
            val adapter: JsonAdapter<List<Word>> = moshi.adapter(type)

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                return@withContext adapter.fromJson(response.body!!.source())
            }
        }

    suspend fun getQuizMap(grade: String): Map<Int, String> =
        withContext(Dispatchers.Default) {
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
            return@withContext results
        }

    suspend fun setQuizMap(grade: String): Unit =
        withContext(Dispatchers.Main) {
            val map = getQuizMap(grade)
            mainActivity.runOnUiThread {
                mainActivity.findViewById<TextView>(R.id.textView0).apply {
                    text = map[0]?.get(0).toString()
                }
                mainActivity.findViewById<TextView>(R.id.textView1).apply {
                    text = map[1]?.get(0).toString()
                }
                mainActivity.findViewById<TextView>(R.id.textView2).apply {
                    text = map[2]?.get(1).toString()
                }
                mainActivity.findViewById<TextView>(R.id.textView3).apply {
                    text = map[3]?.get(1).toString()
                }
            }
        }
}
