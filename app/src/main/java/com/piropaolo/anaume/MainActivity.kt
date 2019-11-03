package com.piropaolo.anaume

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun randomQuiz(view: View) {
        println(KanjiManager.getRandomKanji("1"))
    }
}
