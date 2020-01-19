package com.piropaolo.anaume.activity

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.piropaolo.anaume.R
import com.piropaolo.anaume.api.KanjiAlive
import com.piropaolo.anaume.repository.Repository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val client = OkHttpClient()
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val kanjiAlive: KanjiAlive =
        KanjiAlive(client, moshi)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateName()
        updateScore()
    }

    fun getRandomQuiz(view: View) = launch {
        setQuestionMark()
        val quizMap = kanjiAlive.getQuizMap(1)

        println("quizMap.get(0) = ${quizMap[0]?.get(1)}")

        text_view_0.text = quizMap[0]?.get(0).toString()
        text_view_1.text = quizMap[1]?.get(0).toString()
        text_view_2.text = quizMap[2]?.get(1).toString()
        text_view_3.text = quizMap[3]?.get(1).toString()
    }

    fun checkSolution(view: View) = launch {
        val kanji = answer_edit.text.toString()
        val words = setOf(
            text_view_0.text.toString() + kanji,
            text_view_1.text.toString() + kanji,
            kanji + text_view_2.text.toString(),
            kanji + text_view_3.text.toString()
        )

        if (kanjiAlive.checkSolution(kanji, words)) {
            Repository.userScore += 1
            updateScore()
            val toast =
                Toast.makeText(applicationContext, R.string.correct_toast, Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP or Gravity.CENTER_VERTICAL, 0, 0)
            toast.show()
            clearBoard()
        } else {
            val toast =
                Toast.makeText(applicationContext, R.string.wrong_toast, Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP or Gravity.CENTER_VERTICAL, 0, 0)
            toast.show()
            clearAnswer()
        }

    }

    private fun clearBoard() {
        answer_edit.text.clear()
        text_view_0.text = null
        text_view_1.text = null
        text_view_2.text = null
        text_view_3.text = null
    }

    private fun clearAnswer() {
        answer_edit.text.clear()
    }

    private fun setQuestionMark() {
        answer_edit.text.clear()
        text_view_0.text = "？"
        text_view_1.text = "？"
        text_view_2.text = "？"
        text_view_3.text = "？"
    }

    private fun updateName() {
        user_name_view.text = Repository.userName
    }

    private fun updateScore() {
        user_score_view.text = Repository.userScore.toString()
    }
}
