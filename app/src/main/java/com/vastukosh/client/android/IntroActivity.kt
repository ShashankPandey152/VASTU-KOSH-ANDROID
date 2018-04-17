package com.vastukosh.client.android

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.Toast
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment

class IntroActivity : AppIntro() {

    lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sp = getSharedPreferences("first-entry", Context.MODE_PRIVATE)

        if(sp.getBoolean("isFirst", false)) {
            val gotoMain = Intent(this, MainActivity::class.java)
            startActivity(gotoMain)
        }

        addSlide(AppIntroFragment.newInstance("Locker", "Store your items in storage locker!",
                R.drawable.locker_icon, Color.parseColor("#f64c73")))
        addSlide(AppIntroFragment.newInstance("Buy, Rent & Sell", "Buy used items, sell your items, give them on rent!",
                R.drawable.buy_icon, Color.parseColor("#20d2bb")))
        addSlide(AppIntroFragment.newInstance("Donate", "Help the needy by donating your items!",
                R.drawable.donate_icon, Color.parseColor("#3395ff")))
        addSlide(AppIntroFragment.newInstance("Pickup and delivery", " Free pickup and delivery services included.",
                R.drawable.pickup_icon, Color.parseColor("#c873f4")))
        setDepthAnimation()

        setProgressIndicator()
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Do something when users tap on Skip button.
        Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show()
        sp.edit().putBoolean("isFirst", true).apply()
        val gotoMain = Intent(this, MainActivity::class.java)
        startActivity(gotoMain)
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Do something when users tap on Done button.
        Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show()
        sp.edit().putBoolean("isFirst", true).apply()
        val gotoMain = Intent(this, MainActivity::class.java)
        startActivity(gotoMain)
    }

    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
        super.onSlideChanged(oldFragment, newFragment)
        // Do something when the slide changes.

    }
}
