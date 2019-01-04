package com.vastukosh.client.android

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_give.*
import kotlinx.android.synthetic.main.app_bar_give.*
import kotlinx.android.synthetic.main.content_give.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.side_menu.*
import org.json.JSONException

class GiveActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var iid = ""

    lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_give)
        setSupportActionBar(toolbar)

        val id = intent.getStringExtra("id")
        val first = intent.getStringExtra("first")
        val name = intent.getStringExtra("name")

        userNameNavHeader.text = first
        userNameNavHeader?.paintFlags = userNameNavHeader?.paintFlags!! or Paint.UNDERLINE_TEXT_FLAG

        sp = getSharedPreferences("login", Context.MODE_PRIVATE)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val itemName = arrayListOf("--Select ID--")

        val itemCount = arrayListOf("--Select Count--")
        val countAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, itemCount)
        countSpinner.adapter = countAdapter

        val hashMap: HashMap<String, String> = hashMapOf()

        val url1 = "http://<website-link>/json/?give=1"
        val url2 = "&id=" + id
        val url = url1+url2
        val loginRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
            try {
                val jsonArray = response.getJSONArray("iname")
                val jsonArray1 = response.getJSONArray("iid")
                val jsonArray2 = response.getJSONArray("count")

                (0 until jsonArray1.length()).map {
                    hashMap[jsonArray1.get(it).toString()] = jsonArray2.get(it).toString()
                }

                if (jsonArray.length() == 0) {
                    itemName.add("No items in locker!")
                } else {
                    (0 until jsonArray.length()).mapTo(itemName) { jsonArray.get(it).toString() + ":" + jsonArray1.get(it).toString() }
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

        val arrayAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, itemName)
        giveItemIdSpinner.adapter = arrayAdapter

        val charityName = arrayListOf("--Select Charity--", "Some Randome Charity 1", "Pandeyji Charity",
                "Vastu Kosh Charity", "Shashank Charity")

        val arrayAdapter1 = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, charityName)
        charitySpinner.adapter = arrayAdapter1

        giveItemIdSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView:View, position:Int, id:Long) {
                val iidArray = giveItemIdSpinner.selectedItem.toString().split(":")
                if(iidArray.size > 1) {
                    iid = iidArray[1]
                    putCount(iid, hashMap)
                }
            }
            override fun onNothingSelected(parentView: AdapterView<*>) {

            }
        }

        giveBtn.setTextColor(Color.parseColor("#204999"))
        giveBtn.textSize = 16F
        giveBtn.setTypeface(null, Typeface.BOLD)

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

    fun putCount(key: String, hashMap: HashMap<String, String>) {
        val stringHigh: String = hashMap[key].toString()
        val high: Long = java.lang.Long.parseLong(stringHigh)
        var i = 1
        val array = arrayListOf("--Select Count--")
        while(i <= high) {
            array.add(i.toString())
            i++
        }
        val arrayAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, array)
        countSpinner.adapter = arrayAdapter
    }

    fun giveSubmitBtnClicked(view: View) {
        val id = intent.getStringExtra("id")
        val first = intent.getStringExtra("first")
        val name = intent.getStringExtra("name")

        if(giveItemIdSpinner.selectedItem.toString() != "--Select ID--" &&
                charitySpinner.selectedItem.toString() != "--Select Charity--" &&
                countSpinner.selectedItem.toString() != "--Select Count--") {
            Toast.makeText(this, "Donating item...", Toast.LENGTH_SHORT).show()
            val url1 = "http://<website-link>/json/?give=2"
            val url2 = "&iid=" + iid + "&charity=" + charitySpinner.selectedItem
            val url3 = "&count=" + countSpinner.selectedItem
            val url = url1 + url2 + url3
            val loginRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
                try {
                    val donated = response.getString("sent")

                    if(donated.toInt() == 1) {
                        Toast.makeText(this, "Item will be sent to selected charity soon!", Toast.LENGTH_SHORT).show()
                        val gotoHome = Intent(this, HomeActivity::class.java)
                        gotoHome.putExtra("id", id)
                        gotoHome.putExtra("name", name)
                        gotoHome.putExtra("first", first)
                        startActivity(gotoHome)
                    } else if(donated.toInt() == -1) {
                        Toast.makeText(this, "Oops, there was some error, please come back later!",
                                Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Complete the form!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }
}
