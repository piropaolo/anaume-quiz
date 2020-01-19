package com.piropaolo.anaume.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.piropaolo.anaume.R
import com.piropaolo.anaume.repository.Repository
import kotlinx.android.synthetic.main.activity_nickname.*

class NicknameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nickname)
    }

    fun setNickname(view: View) {
        if (nickname_edit.text.toString() != getString(R.string.nickname_edit))
            Repository.userName = nickname_edit.text.toString()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
