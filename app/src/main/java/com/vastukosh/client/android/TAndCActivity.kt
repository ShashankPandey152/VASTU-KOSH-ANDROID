package com.vastukosh.client.android

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class TAndCActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tand_c)
    }

    fun backBtnClicked(view: View) {
        val gotoSignup = Intent(this, SignupActivity::class.java)
        startActivity(gotoSignup)
    }
}
