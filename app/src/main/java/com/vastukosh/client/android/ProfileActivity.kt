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
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.Response.Listener
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.app_bar_profile.*
import kotlinx.android.synthetic.main.content_profile.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.side_menu.*
import org.json.JSONException

class ProfileActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(toolbar)

        val id = intent.getStringExtra("id")
        val first = intent.getStringExtra("first")
        val name = intent.getStringExtra("name")

        sp = getSharedPreferences("login", Context.MODE_PRIVATE)

        userNameNavHeader.text = first
        userNameNavHeader?.paintFlags = userNameNavHeader?.paintFlags!! or Paint.UNDERLINE_TEXT_FLAG

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
        var adapter: ItemsAdapter
        val lockerList = arrayListOf<Items>()
        val rentList = arrayListOf<Items>()
        val sellList = arrayListOf<Items>()
        val giveList = arrayListOf<Items>()
        var idpic: String
        val url1 = "http://<website-link>/json/?profile=1"
        val url2 = "&id=" + id
        val url = url1+url2
        val loginRequest = object: JsonObjectRequest(Method.GET, url, null, Listener { response ->
            try {
                userName.text = "Name: " + response.getString("name")
                userLocation.text = "City: " + response.getString("location")
                userAddress.text = "Address: " + response.getString("address")
                userMobile.text = "Mobile: " + response.getString("mobile")
                userEmail.text = "Email: " + response.getString("email")
                userAdhaarNumber.text = "Adhaar: " + response.getString("idno")
                idpic = "http://<website-link>/img/id/" + response.get("idpic")
                Glide.with(this).load(idpic).into(profileAdhaarPicture)

                var Id = response.getJSONArray("iid")
                var Name = response.getJSONArray("locker")
                var i = 0
                while(i<Id.length()) {
                    lockerList.add(Items(Id.get(i).toString(), Name.get(i).toString(), ""))
                    i++
                }
                adapter = ItemsAdapter(this, lockerList) { items ->
                    val gotoLockerItem = Intent(this, ProfileItemActivity::class.java)
                    gotoLockerItem.putExtra("pageType", "1")
                    gotoLockerItem.putExtra("iid", items.iid)
                    gotoLockerItem.putExtra("name", items.iname)
                    gotoLockerItem.putExtra("uname", name)
                    gotoLockerItem.putExtra("uid", id)
                    gotoLockerItem.putExtra("ufirst", first)
                    startActivity(gotoLockerItem)
                }
                itemLocker.adapter = adapter

                itemLocker.isNestedScrollingEnabled = false

                var layoutManager = LinearLayoutManager(this)
                itemLocker.layoutManager = layoutManager
                itemLocker.setHasFixedSize(false)

                Id = response.getJSONArray("rentiid")
                Name = response.getJSONArray("rent")
                var Price = response.getJSONArray("rentprice")
                i = 0
                while(i<Id.length()) {
                    rentList.add(Items(Id.get(i).toString(), Name.get(i).toString(), Price.get(i).toString()))
                    i++
                }
                adapter = ItemsAdapter(this, rentList) { items ->
                    val gotoRentItem = Intent(this, ProfileItemActivity::class.java)
                    gotoRentItem.putExtra("pageType", "2")
                    gotoRentItem.putExtra("iid", items.iid)
                    gotoRentItem.putExtra("name", items.iname)
                    gotoRentItem.putExtra("price", items.price)
                    gotoRentItem.putExtra("uname", name)
                    gotoRentItem.putExtra("uid", id)
                    gotoRentItem.putExtra("ufirst", first)
                    startActivity(gotoRentItem)
                }
                itemRent.adapter = adapter

                itemRent.isNestedScrollingEnabled = false

                layoutManager = LinearLayoutManager(this)
                itemRent.layoutManager = layoutManager
                itemRent.setHasFixedSize(false)

                Id = response.getJSONArray("selliid")
                Name = response.getJSONArray("sell")
                Price = response.getJSONArray("sellprice")
                i = 0
                while(i<Id.length()) {
                    sellList.add(Items(Id.get(i).toString(), Name.get(i).toString(), Price.get(i).toString()))
                    i++
                }
                adapter = ItemsAdapter(this, sellList) { items ->
                    val gotoSellItem = Intent(this, ProfileItemActivity::class.java)
                    gotoSellItem.putExtra("pageType", "3")
                    gotoSellItem.putExtra("iid", items.iid)
                    gotoSellItem.putExtra("name", items.iname)
                    gotoSellItem.putExtra("price", items.price)
                    gotoSellItem.putExtra("uname", name)
                    gotoSellItem.putExtra("uid", id)
                    gotoSellItem.putExtra("ufirst", first)
                    startActivity(gotoSellItem)
                }
                itemSold.adapter = adapter

                itemSold.isNestedScrollingEnabled = false

                layoutManager = LinearLayoutManager(this)
                itemSold.layoutManager = layoutManager
                itemSold.setHasFixedSize(false)

                Id = response.getJSONArray("giveiid")
                Name = response.getJSONArray("give")
                i = 0
                while(i<Id.length()) {
                    giveList.add(Items(Id.get(i).toString(), Name.get(i).toString(), ""))
                    i++
                }
                adapter = ItemsAdapter(this, giveList) { items ->
                    val gotoGiveItem = Intent(this, ProfileItemActivity::class.java)
                    gotoGiveItem.putExtra("pageType", "4")
                    gotoGiveItem.putExtra("iid", items.iid)
                    gotoGiveItem.putExtra("name", items.iname)
                    gotoGiveItem.putExtra("uname", name)
                    gotoGiveItem.putExtra("uid", id)
                    gotoGiveItem.putExtra("ufirst", first)
                    startActivity(gotoGiveItem)
                }
                itemDonated.adapter = adapter

                itemDonated.isNestedScrollingEnabled = false

                layoutManager = LinearLayoutManager(this)
                itemDonated.layoutManager = layoutManager
                itemDonated.setHasFixedSize(false)

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
        menuInflater.inflate(R.menu.edit_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_edit -> {
                val id = intent.getStringExtra("id")
                val first = intent.getStringExtra("first")
                val name = intent.getStringExtra("name")
                val editIntent = Intent(this, EditProfileActivity::class.java)
                editIntent.putExtra("id", id)
                editIntent.putExtra("first", first)
                editIntent.putExtra("name", name)
                startActivity(editIntent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
       return true
    }
}
