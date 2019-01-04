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
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_buy_rent.*
import kotlinx.android.synthetic.main.app_bar_buy_rent.*
import kotlinx.android.synthetic.main.content_buy_rent.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.side_menu.*
import org.json.JSONException
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter


class BuyRentActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_rent)
        setSupportActionBar(toolbar)

        val id = intent.getStringExtra("id")
        val first = intent.getStringExtra("first")
        val name = intent.getStringExtra("name")
        val pageType = intent.getStringExtra("pageType")

        userNameNavHeader.text = first
        userNameNavHeader?.paintFlags = userNameNavHeader?.paintFlags!! or Paint.UNDERLINE_TEXT_FLAG

        sp = getSharedPreferences("login", Context.MODE_PRIVATE)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        parseJSONAds(first, pageType, id, name)

        filterClear.setOnClickListener {
            filterList.visibility = View.GONE
            parseJSONAds(first, pageType, id, name)
        }

        val type = arrayOf("--Select Type--","Electronic Gadgets", "Clothes and Accessories", "Home Appliances", "Kitchenware", "Luggage",
                "Sports and Fitness", "Books", "Musical Instruments", "Gaming")
        var arrayAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, type)
        type_select.adapter = arrayAdapter

        var subtype = arrayOf("--Select Subtype--")
        val subtype_ini = subtype
        arrayAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, subtype)
        subtype_select.adapter = arrayAdapter

        val electronicGadgets = arrayOf("--Select Subtype--", "Laptop", "Mobile", "Tablet", "Speakers", "Camera and accessories",
                "Computer and accessories", "Printer", "Scanner")
        val clothesAccessories = arrayOf("--Select Subtype--", "Shoes", "Sportswear", "Handbags and clutches", "Shorts", "Shirt",
                "T-Shirt", "Jeans", "Suits and blazers", "Trousers", "Track pants", "Sweatshirts and hoodies", "Sweater", "Jackets",
                "Other winterwear", "Salwar suits", "Saree", "Tops and tees", "Nightwear")
        val homeAppliances = arrayOf("--Select Subtype--", "Microwave", "Electric kettle", "Vacuum cleaner",
                "Sewing machine", "Oven", "Heaters")
        val kitchenware = arrayOf("--Select Subtype--", "Mixer grinder", "Toaster", "Sandwich maker", "Gas stove", "Induction juicer",
                "Air fryer", "Barbeque and grill", "Electric cooker", "Dining set", "Cookware")
        val luggage = arrayOf("--Select Subtype--", "Backpacks", "Rucksacks", "Suitcases and trolley bags")
        val sportsFitness = arrayOf("--Select Subtype--", "Cricket", "Badminton", "Football", "Hockey", "Other sports",
                "Exercise and fitness materials")
        val books = arrayOf("--Select Subtype--", "Fiction", "School","Children's", "Exam", "Textbook", "Language", "Non-fiction",
                "Miscellaneous")
        val musicalInstruments = arrayOf("--Select Subtype--", "Guitar", "Keyboard", "Drumset", "Sitar", "Harmonium", "Miscellaneous")
        val gaming = arrayOf("--Select Subtype--", "Consoles", "Game CDs", "Accessories")

        type_select.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView:View, position:Int, id:Long) {
                when {
                    type_select.selectedItem.toString() == "Electronic Gadgets" -> subtype = electronicGadgets
                    type_select.selectedItem.toString() == "Clothes and Accessories" -> subtype = clothesAccessories
                    type_select.selectedItem.toString() == "Home Appliances" -> subtype = homeAppliances
                    type_select.selectedItem.toString() == "Kitchenware" -> subtype = kitchenware
                    type_select.selectedItem.toString() == "Luggage" -> subtype = luggage
                    type_select.selectedItem.toString() == "Sports and Fitness" -> subtype = sportsFitness
                    type_select.selectedItem.toString() == "Books" -> subtype = books
                    type_select.selectedItem.toString() == "Musical Instruments" -> subtype = musicalInstruments
                    type_select.selectedItem.toString() == "Gaming" -> subtype = gaming
                    type_select.selectedItem.toString() == "--Select Type--" -> subtype = subtype_ini
                }
                arrayAdapter = ArrayAdapter(this@BuyRentActivity, R.layout.support_simple_spinner_dropdown_item, subtype)
                subtype_select.adapter = arrayAdapter
            }
            override fun onNothingSelected(parentView: AdapterView<*>) {

            }
        }

        filterSubmit.setOnClickListener {
            var t = "0"
            if(type_select.selectedItem.toString() != "--Select Type--") {
                t = "1"
            }
            if(type_select.selectedItem.toString() != "--Select Type--" && subtype_select.selectedItem.toString()
                    != "--Select Subtype--") {
                t = "2"
            }
            if(type_select.selectedItem.toString() != "--Select Type--" && searchItem.text.toString() != "") {
                t = "3"
            }
            if(type_select.selectedItem.toString() != "--Select Type--" &&
                    subtype_select.selectedItem.toString() != "--Select Subtype--" && searchItem.text.toString() != "") {
                t = "4"
            }
            filterList.visibility = View.GONE
            parseFilteredJSONAds(first, pageType, id, name, t, type_select.selectedItem.toString(), subtype_select.selectedItem.toString(),
                    searchItem.text.toString())
        }

        searchItem.setOnEditorActionListener { _, actionId, _ ->
            val listAd = arrayListOf<Ads>()
            var adapter : AdsAdapter
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if(searchItem.text.toString() != "") {
                    val url1 = "http://<website-link>/json/?search=" + pageType
                    val url2 = "&sname=" + searchItem.text.toString()
                    val url = url1 + url2
                    val loginRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
                        try {
                            val iname = response.getJSONArray("iname")
                            val iprice = response.getJSONArray("prices")
                            val iid = response.getJSONArray("iid")
                            val iimage = response.getJSONArray("images")

                            var i = 0
                            while(i<iname.length()) {
                                listAd.add(Ads(iname.get(i).toString(), "Rs." + iprice.get(i).toString(), iimage.get(i).toString(),
                                        iid.get(i).toString()))
                                i++
                            }

                            if(iname.length() == 0) {
                                Toast.makeText(this, "No ads to show!", Toast.LENGTH_SHORT).show()
                                val homeIntent = Intent(this, HomeActivity::class.java)
                                homeIntent.putExtra("id", id)
                                homeIntent.putExtra("first", first)
                                homeIntent.putExtra("name", name)
                                startActivity(homeIntent)
                            }
                            adapter = AdsAdapter(this, listAd) { ads ->
                                val itemIntent = Intent(this, BuyRentViewItemActivity::class.java)
                                itemIntent.putExtra("name", ads.name)
                                itemIntent.putExtra("id", ads.id)
                                itemIntent.putExtra("price", ads.price)
                                itemIntent.putExtra("pageType", pageType)
                                itemIntent.putExtra("image", ads.image)
                                itemIntent.putExtra("first", first)
                                itemIntent.putExtra("uid", id)
                                itemIntent.putExtra("uname", name)
                                startActivity(itemIntent)
                            }
                            itemAds.adapter = adapter

                            val layoutManager = LinearLayoutManager(this)
                            itemAds.layoutManager = layoutManager
                            itemAds.setHasFixedSize(true)
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
                }
            }
            false
        }

        if(pageType == "1") {
            buyBtn.setTextColor(Color.parseColor("#204999"))
            buyBtn.textSize = 16F
            buyBtn.setTypeface(null, Typeface.BOLD)
            buyBtn.isEnabled = false

            rentBtn.setOnClickListener {
                val rentIntent = Intent(this, BuyRentActivity::class.java)
                rentIntent.putExtra("id", id)
                rentIntent.putExtra("first", first)
                rentIntent.putExtra("name", name)
                rentIntent.putExtra("pageType", "2")
                startActivity(rentIntent)
            }
        } else if(pageType == "2") {
            rentBtn.setTextColor(Color.parseColor("#204999"))
            rentBtn.textSize = 16F
            rentBtn.setTypeface(null, Typeface.BOLD)
            rentBtn.isEnabled = false

            buyBtn.setOnClickListener {
                val buyIntent = Intent(this, BuyRentActivity::class.java)
                buyIntent.putExtra("id", id)
                buyIntent.putExtra("first", first)
                buyIntent.putExtra("name", name)
                buyIntent.putExtra("pageType", "1")
                startActivity(buyIntent)
            }
        }

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.search_icon, menu)
        menuInflater.inflate(R.menu.filter_icon, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_search -> {
                if(searchItem.visibility == View.GONE) {
                    searchItem.visibility = View.VISIBLE
                } else {
                    searchItem.visibility = View.GONE
                }
                return true
            }
            R.id.action_filter -> {
                if(filterList.visibility == View.GONE) {
                    filterList.visibility = View.VISIBLE
                } else {
                    filterList.visibility = View.GONE
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }

    fun parseFilteredJSONAds(first: String, pageType: String, uid: String, uname: String, type: String, itype: String, isubtype: String,
                             sname: String) {
        Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
        var adapter: AdsAdapter
        val listAd = arrayListOf<Ads>()
        val url1 = "http://<website-link>/json/?filter=" + type
        val url2 = "&select=$pageType&itype=$itype"
        val url3 = "&isubtype=" + isubtype
        val url4 = "&sname=" + sname
        var url = url1 + url2
        if(type == "3" || type == "4") {
            url += url4
        }
        if(type == "2" || type == "4") {
            url += url3
        }
        val loginRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
            try {
                val iname = response.getJSONArray("iname")
                val iprice = response.getJSONArray("prices")
                val iid = response.getJSONArray("iid")
                val iimage = response.getJSONArray("images")

                var i = 0
                while(i<iname.length()) {
                    listAd.add(Ads(iname.get(i).toString(), "Rs." + iprice.get(i).toString(), iimage.get(i).toString(),
                            iid.get(i).toString()))
                    i++
                }

                if(iname.length() == 0) {
                    Toast.makeText(this, "No ads to show!", Toast.LENGTH_SHORT).show()
                    val homeIntent = Intent(this, HomeActivity::class.java)
                    homeIntent.putExtra("id", uid)
                    homeIntent.putExtra("first", first)
                    homeIntent.putExtra("name", uname)
                    startActivity(homeIntent)
                }
                adapter = AdsAdapter(this, listAd) { ads ->
                    val itemIntent = Intent(this, BuyRentViewItemActivity::class.java)
                    itemIntent.putExtra("name", ads.name)
                    itemIntent.putExtra("id", ads.id)
                    itemIntent.putExtra("price", ads.price)
                    itemIntent.putExtra("pageType", pageType)
                    itemIntent.putExtra("image", ads.image)
                    itemIntent.putExtra("first", first)
                    itemIntent.putExtra("uid", uid)
                    itemIntent.putExtra("uname", uname)
                    startActivity(itemIntent)
                }
                itemAds.adapter = adapter

                val layoutManager = LinearLayoutManager(this)
                itemAds.layoutManager = layoutManager
                itemAds.setHasFixedSize(true)
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
    }

    fun parseJSONAds(first: String, pageType: String, uid: String, uname: String) {
        Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
        var adapter: AdsAdapter
        val listAd = arrayListOf<Ads>()
        val url1 = "http://<website-link>/json/?images=" + pageType
        val loginRequest = object: JsonObjectRequest(Method.GET, url1, null, Response.Listener { response ->
            try {
                val iname = response.getJSONArray("iname")
                val iprice = response.getJSONArray("prices")
                val iid = response.getJSONArray("iid")
                val iimage = response.getJSONArray("images")

                var i = 0
                while(i<iname.length()) {
                    listAd.add(Ads(iname.get(i).toString(), "Rs." + iprice.get(i).toString(), iimage.get(i).toString(),
                            iid.get(i).toString()))
                    i++
                }

                if(iname.length() == 0) {
                    Toast.makeText(this, "No ads to show!", Toast.LENGTH_SHORT).show()
                    val homeIntent = Intent(this, HomeActivity::class.java)
                    homeIntent.putExtra("id", uid)
                    homeIntent.putExtra("first", first)
                    homeIntent.putExtra("name", uname)
                    startActivity(homeIntent)
                }
                adapter = AdsAdapter(this, listAd) { ads ->
                    val itemIntent = Intent(this, BuyRentViewItemActivity::class.java)
                    itemIntent.putExtra("name", ads.name)
                    itemIntent.putExtra("id", ads.id)
                    itemIntent.putExtra("price", ads.price)
                    itemIntent.putExtra("pageType", pageType)
                    itemIntent.putExtra("image", ads.image)
                    itemIntent.putExtra("first", first)
                    itemIntent.putExtra("uid", uid)
                    itemIntent.putExtra("uname", uname)
                    startActivity(itemIntent)
                }
                itemAds.adapter = adapter

                val layoutManager = LinearLayoutManager(this)
                itemAds.layoutManager = layoutManager
                itemAds.setHasFixedSize(true)
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
    }
}
