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
import kotlinx.android.synthetic.main.activity_locker.*
import kotlinx.android.synthetic.main.app_bar_locker.*
import kotlinx.android.synthetic.main.content_locker.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.side_menu.*
import org.json.JSONException

class LockerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locker)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        sp = getSharedPreferences("login", Context.MODE_PRIVATE)

        val uid = intent.getStringExtra("id")
        val first = intent.getStringExtra("first")
        val uname = intent.getStringExtra("name")

        lockerBtn.setTextColor(Color.parseColor("#204999"))
        lockerBtn.textSize = 16F
        lockerBtn.setTypeface(null, Typeface.BOLD)

        userNameNavHeader.text = first
        userNameNavHeader?.paintFlags = userNameNavHeader?.paintFlags!! or Paint.UNDERLINE_TEXT_FLAG

        val type = arrayOf("--Select Type--","Electronic Gadgets", "Clothes and Accessories", "Home Appliances", "Kitchenware", "Luggage",
                "Sports and Fitness", "Books", "Musical Instruments", "Gaming")
        var arrayAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, type)
        profileItemTypeSpinner.adapter = arrayAdapter

        var subtype = arrayOf("--Select Subtype--")
        var subtype_ini = subtype
        arrayAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, subtype)
        profileItemSubtypeSpinner.adapter = arrayAdapter

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

        profileItemTypeSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position:Int, id:Long) {
                when {
                    profileItemTypeSpinner.selectedItem.toString() == "Electronic Gadgets" -> subtype = electronicGadgets
                    profileItemTypeSpinner.selectedItem.toString() == "Clothes and Accessories" -> subtype = clothesAccessories
                    profileItemTypeSpinner.selectedItem.toString() == "Home Appliances" -> subtype = homeAppliances
                    profileItemTypeSpinner.selectedItem.toString() == "Kitchenware" -> subtype = kitchenware
                    profileItemTypeSpinner.selectedItem.toString() == "Luggage" -> subtype = luggage
                    profileItemTypeSpinner.selectedItem.toString() == "Sports and Fitness" -> subtype = sportsFitness
                    profileItemTypeSpinner.selectedItem.toString() == "Books" -> subtype = books
                    profileItemTypeSpinner.selectedItem.toString() == "Musical Instruments" -> subtype = musicalInstruments
                    profileItemTypeSpinner.selectedItem.toString() == "Gaming" -> subtype = gaming
                    profileItemTypeSpinner.selectedItem.toString() == "--Select Type--" -> subtype = subtype_ini
                }
                arrayAdapter = ArrayAdapter(this@LockerActivity, R.layout.support_simple_spinner_dropdown_item, subtype)
                profileItemSubtypeSpinner.adapter = arrayAdapter
            }
            override fun onNothingSelected(parentView: AdapterView<*>) {

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

        aboutBtn.setOnClickListener {
            val about = Intent(this, AboutActivity::class.java)
            about.putExtra("id", uid)
            about.putExtra("first", first)
            about.putExtra("name", uname)
            startActivity(about)
        }

    }

    fun profileSubmitBtnClicked(view: View) {
        val uid = intent.getStringExtra("id")
        val first = intent.getStringExtra("first")
        val uname = intent.getStringExtra("name")

        val id = intent.getStringExtra("id")
        val name = intent.getStringExtra("name")
        if(profileItemNameText.text.toString() != "" && profileItemTypeSpinner.selectedItem.toString() != "--Select Type--"
                && profileItemSubtypeSpinner.selectedItem.toString() != "--Select Subtype--" &&
                profileItemCountText.text.toString() != "" && profileItemDescriptionText.text.toString() != "") {
            Toast.makeText(this, "Putting into database...", Toast.LENGTH_SHORT).show()
            val count: Long = java.lang.Long.parseLong(profileItemCountText.text.toString())
            if(count in 1..25) {
                val url1 = "http://<website-link>/json/?locker=1"
                val url2 = "&id=$id" + "&name=" + devoidOfSpace(name)
                val url3 = "&iname=" + devoidOfSpace(profileItemNameText.text.toString()) + "&type=" +
                        profileItemTypeSpinner.selectedItem
                val url4 = "&subtype=" + profileItemSubtypeSpinner.selectedItem + "&count=" + profileItemCountText.text
                val url5 = "&descr=" + devoidOfSpace(profileItemDescriptionText.text.toString())
                val url = url1 + url2 + url3 + url4 + url5
                val loginRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
                    try {
                        val put = response.getString("sent")

                        if(put == "1") {
                            Toast.makeText(this, "Your item is put to locker database, we will pick it up soon!",
                                    Toast.LENGTH_SHORT).show()
                            val gotoHome = Intent(this, HomeActivity::class.java)
                            gotoHome.putExtra("id", uid)
                            gotoHome.putExtra("first", first)
                            gotoHome.putExtra("name", uname)
                            startActivity(gotoHome)
                        } else if(put == "-1") {
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
                Toast.makeText(this, "Invalid count!", Toast.LENGTH_SHORT).show()
            }

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

    fun devoidOfSpace(str: String): String {
        return str.replace("\\s".toRegex(), "%20")
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }
}
