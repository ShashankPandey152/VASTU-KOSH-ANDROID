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
import com.android.volley.Response.Listener
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_contact.*
import kotlinx.android.synthetic.main.app_bar_profile_item.*
import kotlinx.android.synthetic.main.content_rent_sell.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.side_menu.*
import org.json.JSONException

class RentSellActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var iid = ""
    lateinit var pageType: String

    lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rent_sell)
        setSupportActionBar(toolbar)

        val id = intent.getStringExtra("id")
        val first = intent.getStringExtra("first")
        val name = intent.getStringExtra("name")
        pageType = intent.getStringExtra("pageType")

        if(pageType == "1") {
            priceText.hint = "Price (per month)"
        }

        userNameNavHeader.text = first
        userNameNavHeader?.paintFlags = userNameNavHeader?.paintFlags!! or Paint.UNDERLINE_TEXT_FLAG

        sp = getSharedPreferences("login", Context.MODE_PRIVATE)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        rentItBtn.setTextColor(Color.parseColor("#204999"))
        rentItBtn.textSize = 16F
        rentItBtn.setTypeface(null, Typeface.BOLD)

        sellBtn.setTextColor(Color.parseColor("#204999"))
        sellBtn.textSize = 16F
        sellBtn.setTypeface(null, Typeface.BOLD)

        val itemName = arrayListOf("--Select Item--")

        val itemCount = arrayListOf("--Select Count--")
        val countAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, itemCount)
        itemCountSpinner.adapter = countAdapter

        val hashMap: HashMap<String, String> = hashMapOf()

        val url1 = "http://<website-link>/json/?items=1"
        val url2 = "&id=" + id
        val url = url1+url2
        val loginRequest = object: JsonObjectRequest(Method.GET, url, null, Listener { response ->
            try {
                val jsonArray = response.getJSONArray("item")
                val jsonArray1 = response.getJSONArray("itemid")
                val jsonArray2 = response.getJSONArray("count")

                (0 until jsonArray1.length()).map {
                    hashMap[jsonArray1.get(it).toString()] = jsonArray2.get(it).toString()
                }

                if(jsonArray.length() == 0) {
                    itemName.add("No items in locker!")
                    priceText.visibility = View.INVISIBLE
                    typeSelect.visibility = View.INVISIBLE
                    itemCountSpinner.visibility = View.INVISIBLE
                    descriptionText.visibility = View.INVISIBLE
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
        itemIdSpinner.adapter = arrayAdapter

        val types = arrayOf("--Rent/Sell--","Rent","Sell")
        val arrayAdapter1 = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, types)
        typeSelect.adapter = arrayAdapter1

        typeSelect.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView:AdapterView<*>, selectedItemView:View, position:Int, id:Long) {
                if(typeSelect.selectedItem.toString() == "Rent") {
                    durationText.visibility = View.VISIBLE
                } else {
                    durationText.visibility = View.INVISIBLE
                }
            }
            override fun onNothingSelected(parentView:AdapterView<*>) {

            }
        }

        itemIdSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView:AdapterView<*>, selectedItemView:View, position:Int, id:Long) {
                val iidArray = itemIdSpinner.selectedItem.toString().split(":")
               // iid = iidArray.get(1)
                if(iidArray.size > 1) {
                    iid = iidArray[1]
                    putCount(iid, hashMap)
                }
            }
            override fun onNothingSelected(parentView:AdapterView<*>) {

            }
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
        itemCountSpinner.adapter = arrayAdapter
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

    fun rentSellSubmitBtnClicked(view: View) {
        val id = intent.getStringExtra("id")
        val first = intent.getStringExtra("first")
        val name = intent.getStringExtra("name")
        Toast.makeText(this, "Putting item in respective section", Toast.LENGTH_SHORT).show()
        var result: String
        if(itemIdSpinner.selectedItem.toString() != "--Select Item--" && priceText.text.toString() != ""
                && typeSelect.selectedItem.toString() != "--Rent/Sell--"  && itemCountSpinner.selectedItem.toString() != "--Select Count--"
                && descriptionText.text.toString() != "") {
            if(typeSelect.selectedItem.toString() == "Rent") {
                if(durationText.text.toString() != "") {
                    if(durationText.text.toString() == "0") {
                        Toast.makeText(this, "Minimum duration is 1 month!", Toast.LENGTH_SHORT).show()
                    } else {
                        val url1 = "http://<website-link>/json/?put=1&type=" + pageType
                        val url2 = "&iid=$iid&duration="
                        val url3 = durationText.text.toString() + "&price=" + priceText.text.toString()
                        val url4 = "&count=" + itemCountSpinner.selectedItem + "&descr=" + descriptionText.text
                        val url = url1 + url2 + url3 + url4
                        val loginRequest = object: JsonObjectRequest(Method.GET, url, null, Listener { response ->
                            try {
                                result = response.getString("put")

                                if(result.toInt() == 2) {
                                    Toast.makeText(this, "Item put on rent successfully!", Toast.LENGTH_SHORT).show()
                                    val gotoHome = Intent(this, HomeActivity::class.java)
                                    gotoHome.putExtra("id", id)
                                    gotoHome.putExtra("first", first)
                                    gotoHome.putExtra("name", name)
                                    startActivity(gotoHome)
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
                    }
                } else {
                    Toast.makeText(this, "Complete the form!", Toast.LENGTH_SHORT).show()
                }
            } else {
                val url1 = "http://<website-link>/json/?put=1&type=2"
                val url2 = "&iid=" + iid
                val url3 = "&price=" + priceText.text.toString()
                val url4 = "&count=" + itemCountSpinner.selectedItem + "&descr=" + descriptionText.text
                val url = url1 + url2 + url3 + url4
                val loginRequest = object: JsonObjectRequest(Method.GET, url, null, Listener { response ->
                    try {
                        result = response.getString("put")

                        if(result.toInt() == 2) {
                            Toast.makeText(this, "Item put on sale successfully!", Toast.LENGTH_SHORT).show()
                            val gotoHome = Intent(this, HomeActivity::class.java)
                            gotoHome.putExtra("id", id)
                            gotoHome.putExtra("first", first)
                            gotoHome.putExtra("name", name)
                            startActivity(gotoHome)
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
            }
        } else {
            Toast.makeText(this, "Complete the form!", Toast.LENGTH_SHORT).show()
        }
    }
}
