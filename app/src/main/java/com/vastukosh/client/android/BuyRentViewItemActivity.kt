package com.vastukosh.client.android

import android.annotation.SuppressLint
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
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_buy_rent_view_item.*
import kotlinx.android.synthetic.main.app_bar_buy_view_rent_item.*
import kotlinx.android.synthetic.main.content_buy_rent_view_item.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.side_menu.*
import org.json.JSONException

class BuyRentViewItemActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var sp: SharedPreferences

    lateinit var getUid: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_rent_view_item)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        sp = getSharedPreferences("login", Context.MODE_PRIVATE)

        val name = intent.getStringExtra("name")
        val id = intent.getStringExtra("id")
        val price = intent.getStringExtra("price")
        val typePage = intent.getStringExtra("pageType")
        val image = intent.getStringExtra("image")
        val first = intent.getStringExtra("first")
        val uname = intent.getStringExtra("uname")
        val uid = intent.getStringExtra("uid")

        userNameNavHeader.text = first
        userNameNavHeader?.paintFlags = userNameNavHeader?.paintFlags!! or Paint.UNDERLINE_TEXT_FLAG

        Glide.with(this).load(image).into(adItemImage)

        adItemName.text = name
        if(typePage.toInt() == 1) {
            adItemPrice.text = "Price : $price"
        } else if(typePage.toInt() == 2) {
            adItemPrice.text = "Price (per month): $price"
        }

        val url1 = "http://<website-link>/json/?details=" + typePage
        val url2 = "&iid=$id&price=$price"
        val url = url1+url2
        val loginRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
            try {
                val oname = response.getString("oname")
                val itype = response.getString("itype")
                val isubtype = response.getString("isubtype")
                val count = response.getString("counts")
                val description = response.getString("descr")
                getUid = response.getString("id")
                var duration = ""
                if(typePage.toInt() == 2) {
                    duration = response.getString("duration")
                }
                adItemOwner.text = "Owner: $oname"
                adItemType.text = "Item type: $itype"
                adItemSubtype.text = "Item subtype: $isubtype"
                adItemCount.text = "Item count: $count"
                adItemDescription.text = "Item Description: \r\n\r\n$description"
                adItemDuration.text = duration

                if(typePage.toInt() == 2) {
                    adItemDuration.text = "Duration (in months): $duration"
                }

            } catch (e: JSONException) {
                Log.d("JSON", "EXC: " + e.localizedMessage)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not send mail: $error")
        }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
        }
        Volley.newRequestQueue(this).add(loginRequest)

        interestedBtn.setOnClickListener {
            if(interestPrice.text.toString() != "") {
                Toast.makeText(this, "Sending mail to owner...", Toast.LENGTH_SHORT).show()
                val iPrice: Long = java.lang.Long.parseLong(interestPrice.text.toString())
                val priceString = price.split("Rs.")
                val oPrice: Long = java.lang.Long.parseLong(priceString[1])
                if(getUid == uid) {
                    Toast.makeText(this, "This item belongs to you!", Toast.LENGTH_SHORT).show()
                } else {
                    if(iPrice in 1..oPrice) {
                        val url4 = "http://<website-link>/json/?interested=1"
                        val url5 = "&iid=" + id + "&oprice=" + interestPrice.text + "&price=" + price
                        val url6 = "&name=$uname&type=$typePage&id=$uid"
                        val urL = url4 + url5 + url6
                        val loginRequest1 = object: JsonObjectRequest(Method.GET, urL, null, Response.Listener { response ->
                            try {
                                val sent = response.getString("sent")

                                if(sent.toInt() == 1) {
                                    Toast.makeText(this, "Mail sent to owner successfully!", Toast.LENGTH_SHORT).show()
                                    val gotoItemPage = Intent(this, BuyRentActivity::class.java)
                                    gotoItemPage.putExtra("id", uid)
                                    gotoItemPage.putExtra("name", uname)
                                    gotoItemPage.putExtra("first", first)
                                    gotoItemPage.putExtra("pageType", typePage)
                                    startActivity(gotoItemPage)
                                } else if(sent.toInt() == -1) {
                                    Toast.makeText(this, "Sorry! There is some error, try again later.", Toast.LENGTH_SHORT).show()
                                }

                                interestPrice.text.clear()

                            } catch (e: JSONException) {
                                Log.d("JSON", "EXC: " + e.localizedMessage)
                            }
                        }, Response.ErrorListener { error ->
                            Log.d("ERROR", "Could not send mail: $error")
                        }) {

                            override fun getBodyContentType(): String {
                                return "application/json; charset=utf-8"
                            }
                        }
                        Volley.newRequestQueue(this).add(loginRequest1)
                    } else {
                        Toast.makeText(this, "Invalid price!", Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                Toast.makeText(this, "Enter price!", Toast.LENGTH_SHORT).show()
            }
        }

        rentItBtn.setOnClickListener {
            val rentItIntent = Intent(this, RentSellActivity::class.java)
            rentItIntent.putExtra("id", uid)
            rentItIntent.putExtra("first", first)
            rentItIntent.putExtra("name", uname)
            rentItIntent.putExtra("pageType", "1")
            startActivity(rentItIntent)
        }

        sellBtn.setOnClickListener {
            val sellIntent = Intent(this, RentSellActivity::class.java)
            sellIntent.putExtra("id", uid)
            sellIntent.putExtra("first", first)
            sellIntent.putExtra("name", uname)
            sellIntent.putExtra("pageType", "2")
            startActivity(sellIntent)
        }

        buyBtn.setOnClickListener {
            val buyIntent = Intent(this, BuyRentActivity::class.java)
            buyIntent.putExtra("id", uid)
            buyIntent.putExtra("first", first)
            buyIntent.putExtra("name", uname)
            buyIntent.putExtra("pageType", "1")
            startActivity(buyIntent)
        }

        rentBtn.setOnClickListener {
            val rentIntent = Intent(this, BuyRentActivity::class.java)
            rentIntent.putExtra("id", uid)
            rentIntent.putExtra("first", first)
            rentIntent.putExtra("name", uname)
            rentIntent.putExtra("pageType", "2")
            startActivity(rentIntent)
        }

        contactBtn.setOnClickListener {
            val contactIntent = Intent(this, ContactActivity::class.java)
            contactIntent.putExtra("id", uid)
            contactIntent.putExtra("first", first)
            contactIntent.putExtra("name", uname)
            startActivity(contactIntent)
        }

        giveBtn.setOnClickListener {
            val giveIntent = Intent(this, GiveActivity::class.java)
            giveIntent.putExtra("id", uid)
            giveIntent.putExtra("first", first)
            giveIntent.putExtra("name", uname)
            startActivity(giveIntent)
        }

        homeBtn.setOnClickListener {
            val homeIntent = Intent(this, HomeActivity::class.java)
            homeIntent.putExtra("id", uid)
            homeIntent.putExtra("first", first)
            homeIntent.putExtra("name", uname)
            startActivity(homeIntent)
        }

        loginBtnNavHeader.setOnClickListener {
            val logout = Intent(this, MainActivity::class.java)
            startActivity(logout)
            sp.edit().putBoolean("logged", false).apply()
        }

        userNameNavHeader.setOnClickListener {
            val profile = Intent(this, ProfileActivity::class.java)
            profile.putExtra("id", uid)
            profile.putExtra("first", first)
            profile.putExtra("name", uname)
            startActivity(profile)
        }

        lockerBtn.setOnClickListener {
            val locker = Intent(this, LockerActivity::class.java)
            locker.putExtra("id", uid)
            locker.putExtra("first", first)
            locker.putExtra("name", uname)
            startActivity(locker)
        }

        aboutBtn.setOnClickListener {
            val about = Intent(this, AboutActivity::class.java)
            about.putExtra("id", uid)
            about.putExtra("first", first)
            about.putExtra("name", uname)
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
                val typePage = intent.getStringExtra("pageType")
                val backAdsPageIntent = Intent(this, BuyRentActivity::class.java)
                backAdsPageIntent.putExtra("id", id)
                backAdsPageIntent.putExtra("first", first)
                backAdsPageIntent.putExtra("name", name)
                backAdsPageIntent.putExtra("pageType", typePage)
                startActivity(backAdsPageIntent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }
}
