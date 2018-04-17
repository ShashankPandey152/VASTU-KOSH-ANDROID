package com.vastukosh.client.android

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.app_bar_change_password.*
import kotlinx.android.synthetic.main.content_change_password.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.side_menu.*
import org.json.JSONException

class ChangePasswordActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private var submitPassBtn: Button? = null


    override fun onClick(v: View) {
        if (v === submitPassBtn) {
            submit()
        }
    }

    lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        setSupportActionBar(toolbar)

        submitPassBtn = findViewById(R.id.submitPassBtn)
        submitPassBtn?.setOnClickListener(this)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        sp = getSharedPreferences("login", Context.MODE_PRIVATE)
        
        val id = intent.getStringExtra("id")
        val first = intent.getStringExtra("first")
        val name = intent.getStringExtra("name")

        userNameNavHeader.text = first
        userNameNavHeader?.paintFlags = userNameNavHeader?.paintFlags!! or Paint.UNDERLINE_TEXT_FLAG

        rentItBtn.setOnClickListener {
            val rentItIntent = Intent(this, RentSellActivity::class.java)
            rentItIntent.putExtra("id", id)
            rentItIntent.putExtra("first", first)
            rentItIntent.putExtra("name", name)
            rentItIntent.putExtra("pageType", "1")
            startActivity(rentItIntent)
        }

        sellBtn.setOnClickListener {
            val sellIntent = Intent(this, RentSellActivity::class.java)
            sellIntent.putExtra("id", id)
            sellIntent.putExtra("first", first)
            sellIntent.putExtra("name", name)
            sellIntent.putExtra("pageType", "2")
            startActivity(sellIntent)
        }

        buyBtn.setOnClickListener {
            val buyIntent = Intent(this, BuyRentActivity::class.java)
            buyIntent.putExtra("id", id)
            buyIntent.putExtra("first", first)
            buyIntent.putExtra("name", name)
            buyIntent.putExtra("pageType", "1")
            startActivity(buyIntent)
        }

        rentBtn.setOnClickListener {
            val rentIntent = Intent(this, BuyRentActivity::class.java)
            rentIntent.putExtra("id", id)
            rentIntent.putExtra("first", first)
            rentIntent.putExtra("name", name)
            rentIntent.putExtra("pageType", "2")
            startActivity(rentIntent)
        }

        contactBtn.setOnClickListener {
            val contactIntent = Intent(this, ContactActivity::class.java)
            contactIntent.putExtra("id", id)
            contactIntent.putExtra("first", first)
            contactIntent.putExtra("name", name)
            startActivity(contactIntent)
        }

        giveBtn.setOnClickListener {
            val giveIntent = Intent(this, GiveActivity::class.java)
            giveIntent.putExtra("id", id)
            giveIntent.putExtra("first", first)
            giveIntent.putExtra("name", name)
            startActivity(giveIntent)
        }

        homeBtn.setOnClickListener {
            val homeIntent = Intent(this, HomeActivity::class.java)
            homeIntent.putExtra("id", id)
            homeIntent.putExtra("first", first)
            homeIntent.putExtra("name", name)
            startActivity(homeIntent)
        }

        loginBtnNavHeader.setOnClickListener {
            val logout = Intent(this, MainActivity::class.java)
            startActivity(logout)
            sp.edit().putBoolean("logged", false).apply()
        }

        userNameNavHeader.setOnClickListener {
            val profile = Intent(this, ProfileActivity::class.java)
            profile.putExtra("id", id)
            profile.putExtra("first", first)
            profile.putExtra("name", name)
            startActivity(profile)
        }

        lockerBtn.setOnClickListener {
            val locker = Intent(this, LockerActivity::class.java)
            locker.putExtra("id", id)
            locker.putExtra("first", first)
            locker.putExtra("name", name)
            startActivity(locker)
        }

        aboutBtn.setOnClickListener {
            val about = Intent(this, AboutActivity::class.java)
            about.putExtra("id", id)
            about.putExtra("first", first)
            about.putExtra("name", name)
            startActivity(about)
        }
        
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun submit() {
        val id = intent.getStringExtra("id")
        val first = intent.getStringExtra("first")
        val name = intent.getStringExtra("name")
        
        if(oldPass.text.toString() != "" && newPass.text.toString() != "" && confirmPass.text.toString() != "") {
            if (newPass.text.toString() == confirmPass.text.toString()) {
                if(newPass.text.toString().length < 8) {
                    val url1 = "http://vastukosh-com.stackstaging.com/json/?pass=1"
                    val url2 = "&id=" + id + "&opassword=" + oldPass.text.toString()
                    val url3 = "&password=" + newPass.text.toString()
                    val url = url1 + url2 + url3
                    val loginRequest = object : JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
                        try {
                            val get = response.getString("sent")

                            when (get) {
                                "1" ->{
                                    Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                                    val gotoProfile = Intent(this, ProfileActivity::class.java)
                                    gotoProfile.putExtra("id", id)
                                    gotoProfile.putExtra("first", first)
                                    gotoProfile.putExtra("name", name)
                                    startActivity(gotoProfile)
                                } 
                                "2" -> Toast.makeText(this, "Old password is incorrect", Toast.LENGTH_SHORT).show()
                                "0" -> Toast.makeText(this, "Oops, there was some error. Please come back later!", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: JSONException) {
                            Log.d("JSON", "EXC: " + e.localizedMessage)
                            Toast.makeText(this, e.localizedMessage.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }, Response.ErrorListener { error ->
                        Log.d("ERROR", "Could not send mail: $error")
                        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
                    }) {

                        override fun getBodyContentType(): String {
                            return "application/json; charset=utf-8"
                        }
                    }
                    Volley.newRequestQueue(this).add(loginRequest)
                } else {
                    Toast.makeText(this, "Password should contain atleast 8 characters!", Toast.LENGTH_SHORT).show()
                }
                
            } else {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "Complete the form!", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.back_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_back -> {
                val id = intent.getStringExtra("id")
                val first = intent.getStringExtra("first")
                val name = intent.getStringExtra("name")
                val gotoEditProfile = Intent(this, EditProfileActivity::class.java)
                gotoEditProfile.putExtra("id", id)
                gotoEditProfile.putExtra("first", first)
                gotoEditProfile.putExtra("name", name)
                startActivity(gotoEditProfile)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        return true
    }
}
