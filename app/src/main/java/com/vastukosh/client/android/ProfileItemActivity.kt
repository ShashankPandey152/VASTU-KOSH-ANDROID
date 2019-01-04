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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_profile_item.*
import kotlinx.android.synthetic.main.app_bar_profile.*
import kotlinx.android.synthetic.main.content_profile_item.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.side_menu.*
import org.json.JSONException

class ProfileItemActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_item)
        setSupportActionBar(toolbar)

        val uid = intent.getStringExtra("uid")
        val uname = intent.getStringExtra("uname")
        val ufirst = intent.getStringExtra("ufirst")

        sp = getSharedPreferences("login", Context.MODE_PRIVATE)

        userNameNavHeader.text = ufirst
        userNameNavHeader?.paintFlags = userNameNavHeader?.paintFlags!! or Paint.UNDERLINE_TEXT_FLAG

        val pageType = intent.getStringExtra("pageType")
        val iid = intent.getStringExtra("iid")
        val name = intent.getStringExtra("name")

        var price = ""

        if(pageType == "2" || pageType == "3") {
            price = intent.getStringExtra("price")
        }

        profileItemName.text = name

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        if(pageType == "1") {
            profileReturnBtn.visibility = View.VISIBLE
            profileItemCountSpinner.visibility = View.VISIBLE
            profileEmailText.visibility = View.GONE
            profileDealDoneBtn.visibility = View.GONE
        }
        if(pageType == "2" || pageType == "3") {
            profileEmailText.visibility = View.VISIBLE
            profileDealDoneBtn.visibility = View.VISIBLE
            profileReturnBtn.visibility = View.GONE
            profileItemCountSpinner.visibility = View.GONE
        }
        if (pageType == "4") {
            profileReturnBtn.visibility = View.GONE
            profileItemCountSpinner.visibility = View.GONE
            profileEmailText.visibility = View.GONE
            profileDealDoneBtn.visibility = View.GONE
        }

        ParseJSONInfo(pageType, iid, price)

        if(pageType != "4") {
            val url1 = "http://<website-link>/json/?countReturn=1"
            val url2 = "&iid=" + iid
            val url = url1+url2
            val loginRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
                try {
                    val count = response.getString("counts")
                    val high: Long = java.lang.Long.parseLong(count.toString())
                    var i = 1
                    val countList = arrayListOf("--Select Count--")
                    while(i<=high) {
                        countList.add(i.toString())
                        i++
                    }
                    val arrayAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, countList)
                    profileItemCountSpinner.adapter = arrayAdapter

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
        }

        var count = ""

        profileItemCountSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView:View, position:Int, id:Long) {
                count = parentView.selectedItem.toString()
            }
            override fun onNothingSelected(parentView: AdapterView<*>) {

            }
        }

        if(pageType == "1") {

            profileReturnBtn.setOnClickListener {
                try {
                    if(profileItemCountSpinner.selectedItem.toString() != "--Select Count--") {
                        Toast.makeText(this, "Sending mail...", Toast.LENGTH_SHORT).show()
                        val url1 = "http://<website-link>/json/?return=1"
                        val url2 = "&iid=$iid&count=$count"
                        val url = url1+url2
                        val loginRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
                            try {
                                val sent = response.getString("sent")

                                if(sent == "1") {
                                    Toast.makeText(this, "Mail sent, we will get back to you shortly!", Toast.LENGTH_SHORT).show()
                                    val gotoProfile = Intent(this, ProfileActivity::class.java)
                                    gotoProfile.putExtra("id", uid)
                                    gotoProfile.putExtra("first", ufirst)
                                    gotoProfile.putExtra("name", uname)
                                    startActivity(gotoProfile)
                                } else if(sent == "-1") {
                                    Toast.makeText(this, "Oops, there was some error. Please come back later!", Toast.LENGTH_SHORT).show()
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
                    } else {
                        Toast.makeText(this, "Enter number of items!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: NullPointerException) {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }

            }
        }
        if(pageType == "2" || pageType == "3") {

            profileDealDoneBtn.setOnClickListener {
                if(profileEmailText.text.toString() != "") {
                    Toast.makeText(this, "Sending mail...", Toast.LENGTH_SHORT).show()
                    val url1 = "http://<website-link>/json/?deal=1"
                    val url2 = "&iid=$iid&email=" + profileEmailText.text + "&price=" + price
                    val url = url1+url2
                    val loginRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
                        try {
                            val sent = response.getString("sent")

                            if(sent == "1") {
                                Toast.makeText(this, "Mail sent, we will deliver the item to its destination address!", Toast.LENGTH_SHORT).show()
                                val gotoProfile = Intent(this, ProfileActivity::class.java)
                                gotoProfile.putExtra("id", uid)
                                gotoProfile.putExtra("first", ufirst)
                                gotoProfile.putExtra("name", uname)
                                startActivity(gotoProfile)
                            } else if(sent == "-1") {
                                Toast.makeText(this, "Oops, there was some error. Please come back later!", Toast.LENGTH_SHORT).show()
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
                } else {
                    Toast.makeText(this, "Enter email address of the interested individual!", Toast.LENGTH_SHORT).show()
                }
            }

        }

        rentItBtn.setOnClickListener {
            val rentItIntent = Intent(this, RentSellActivity::class.java)
            rentItIntent.putExtra("id", uid)
            rentItIntent.putExtra("first", ufirst)
            rentItIntent.putExtra("name", uname)
            rentItIntent.putExtra("pageType", "1")
            startActivity(rentItIntent)
        }

        sellBtn.setOnClickListener {
            val sellIntent = Intent(this, RentSellActivity::class.java)
            sellIntent.putExtra("id", uid)
            sellIntent.putExtra("first", ufirst)
            sellIntent.putExtra("name", uname)
            sellIntent.putExtra("pageType", "2")
            startActivity(sellIntent)
        }

        buyBtn.setOnClickListener {
            val buyIntent = Intent(this, BuyRentActivity::class.java)
            buyIntent.putExtra("id", uid)
            buyIntent.putExtra("first", ufirst)
            buyIntent.putExtra("name", uname)
            buyIntent.putExtra("pageType", "1")
            startActivity(buyIntent)
        }

        rentBtn.setOnClickListener {
            val rentIntent = Intent(this, BuyRentActivity::class.java)
            rentIntent.putExtra("id", uid)
            rentIntent.putExtra("first", ufirst)
            rentIntent.putExtra("name", uname)
            rentIntent.putExtra("pageType", "2")
            startActivity(rentIntent)
        }

        contactBtn.setOnClickListener {
            val contactIntent = Intent(this, ContactActivity::class.java)
            contactIntent.putExtra("id", uid)
            contactIntent.putExtra("first", ufirst)
            contactIntent.putExtra("name", uname)
            startActivity(contactIntent)
        }

        giveBtn.setOnClickListener {
            val giveIntent = Intent(this, GiveActivity::class.java)
            giveIntent.putExtra("id", uid)
            giveIntent.putExtra("first", ufirst)
            giveIntent.putExtra("name", uname)
            startActivity(giveIntent)
        }

        homeBtn.setOnClickListener {
            val homeIntent = Intent(this, HomeActivity::class.java)
            homeIntent.putExtra("id", uid)
            homeIntent.putExtra("first", ufirst)
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
            profile.putExtra("first", ufirst)
            profile.putExtra("name", uname)
            startActivity(profile)
        }

        lockerBtn.setOnClickListener {
            val locker = Intent(this, LockerActivity::class.java)
            locker.putExtra("id", uid)
            locker.putExtra("first", ufirst)
            locker.putExtra("name", uname)
            startActivity(locker)
        }

        aboutBtn.setOnClickListener {
            val about = Intent(this, AboutActivity::class.java)
            about.putExtra("id", uid)
            about.putExtra("first", ufirst)
            about.putExtra("name", uname)
            startActivity(about)
        }

    }

    fun ParseJSONInfo(pageType: String, iid: String, Price: String) {
        try {
            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
            var  url1 = "http://<website-link>/json/?profile=2&price=" + Price
            if(pageType == "4") {
                url1 = "http://<website-link>/json/?profile=3"
            } else if(pageType == "1") {
                url1 = "http://<website-link>/json/?profile=4"
            }
            val url2 = "&iid=" + iid
            val url = url1+url2
            val loginRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
                try {
                    var image = response.getString("image")
                    image = "http://<website-link>/img/items/" + image

                    Glide.with(this).load(image).into(profileItemImage)

                    val oname = response.getString("oname")
                    val itype = response.getString("itype")
                    val isubtype = response.getString("isubtype")
                    val count = response.getString("counts")

                    profileOwnerNameText.text = "Owner name: $oname"
                    profileItemTypeText.text = "Item type: $itype"
                    profileItemSubtypeText.text = "Item subtype: $isubtype"
                    profileItemCountText.text = "Count: $count"

                    var status = "Status: In locker"
                    when (pageType) {
                        "2" -> status = "Status: On Rent"
                        "3" -> status = "Status: Sold"
                        "4" -> status = "Status: Donated"
                    }
                    profileItemStatusText.text = status

                    if(pageType != "4") {
                        val description = response.getString("descr")
                        profileDescriptionCharityText.text = "Description: \r\n $description"
                    } else {
                        val charity = response.getString("charity")
                        profileDescriptionCharityText.text = "Charity: $charity"
                    }

                    if(pageType == "2") {
                        val duration = response.getString("duration")
                        profileDurationPriceText.text = "Duration (in months): $duration"
                        profilePriceText.text = "Price (per month): $Price"
                    } else if(pageType == "3") {
                        profileDurationPriceText.text = "Price: $Price"
                    }

                } catch (e: JSONException) {
                    Log.d("JSON", "EXC: " + e.localizedMessage)
                    Toast.makeText(this, e.localizedMessage.toString(), Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener { error ->
                Log.d("ERROR", "Could not send mail: $error")
            }) {

                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }
            }
            Volley.newRequestQueue(this).add(loginRequest)
        } catch (e: Exception) {
            Toast.makeText(this, e.localizedMessage.toString(), Toast.LENGTH_SHORT).show()
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
        val id = intent.getStringExtra("uid")
        val first = intent.getStringExtra("ufirst")
        val name = intent.getStringExtra("uname")
        when (item.itemId) {
            R.id.action_back -> {
                val gotoProfile = Intent(this, ProfileActivity::class.java)
                gotoProfile.putExtra("id", id)
                gotoProfile.putExtra("first", first)
                gotoProfile.putExtra("name", name)
                startActivity(gotoProfile)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
       return true
    }
}
