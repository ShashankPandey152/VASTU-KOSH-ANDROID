package com.vastukosh.client.android

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONException

class HomeActivity : AppCompatActivity() {

    lateinit var adapter: CategoryAdapter

    lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sp = getSharedPreferences("login", Context.MODE_PRIVATE)

        val id = sp.getString("id", "")
        val name = sp.getString("name", "")
        val first = sp.getString("first", "")

        checkBlock()

        adapter = CategoryAdapter(this, DataServiceHome.categories) { category ->
            val title = category.title.replace(" ", "")
            when (title) {
                "RentItOut" -> {
                    val rentItIntent = Intent(this, RentSellActivity::class.java)
                    rentItIntent.putExtra("id", id)
                    rentItIntent.putExtra("first", first)
                    rentItIntent.putExtra("name", name)
                    rentItIntent.putExtra("pageType", "1")
                    startActivity(rentItIntent)
                }
                "Sell" -> {
                    val sellIntent = Intent(this, RentSellActivity::class.java)
                    sellIntent.putExtra("id", id)
                    sellIntent.putExtra("first", first)
                    sellIntent.putExtra("name", name)
                    sellIntent.putExtra("pageType", "2")
                    startActivity(sellIntent)
                }
                "Buy" -> {
                    val buyIntent = Intent(this, BuyRentActivity::class.java)
                    buyIntent.putExtra("id", id)
                    buyIntent.putExtra("first", first)
                    buyIntent.putExtra("name", name)
                    buyIntent.putExtra("pageType", "1")
                    startActivity(buyIntent)
                }
                "Rent" -> {
                    val rentIntent = Intent(this, BuyRentActivity::class.java)
                    rentIntent.putExtra("id", id)
                    rentIntent.putExtra("first", first)
                    rentIntent.putExtra("name", name)
                    rentIntent.putExtra("pageType", "2")
                    startActivity(rentIntent)
                }
                "Contact" -> {
                    val contactIntent = Intent(this, ContactActivity::class.java)
                    contactIntent.putExtra("id", id)
                    contactIntent.putExtra("first", first)
                    contactIntent.putExtra("name", name)
                    startActivity(contactIntent)
                }
                "Give" -> {
                    val giveIntent = Intent(this, GiveActivity::class.java)
                    giveIntent.putExtra("id", id)
                    giveIntent.putExtra("first", first)
                    giveIntent.putExtra("name", name)
                    startActivity(giveIntent)
                }
                "Locker" -> {
                    val lockerIntent = Intent(this, LockerActivity::class.java)
                    lockerIntent.putExtra("id", id)
                    lockerIntent.putExtra("first", first)
                    lockerIntent.putExtra("name", name)
                    startActivity(lockerIntent)
                }
                "About" -> {
                    val aboutIntent = Intent(this, AboutActivity::class.java)
                    aboutIntent.putExtra("id", id)
                    aboutIntent.putExtra("first", first)
                    aboutIntent.putExtra("name", name)
                    startActivity(aboutIntent)
                }
                else -> Toast.makeText(this, "Yeh page nhi bana hai", Toast.LENGTH_SHORT).show()
            }
        }
        categoryList.adapter = adapter

        val layoutManager = LinearLayoutManager(this)
        categoryList.layoutManager = layoutManager
        categoryList.setHasFixedSize(true)

        logoutBtn.setOnClickListener {
            val logout = Intent(this, MainActivity::class.java)
            startActivity(logout)
            sp.edit().putBoolean("logged", false).apply()
        }
    }

    override fun onRestart() {
        super.onRestart()

        checkBlock()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }

    fun checkBlock() {
        val id = sp.getString("id", "")
        val url1 = "http://vastukosh-com.stackstaging.com/json/?blockCheck=1"
        val url2 = "&id=" + id
        val url = url1+url2
        val loginRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
            try {
                val status = response.getString("sent")

                if(status == "1") {
                    Toast.makeText(this, "Your account is blocked by admin!", Toast.LENGTH_SHORT).show()
                    val logout = Intent(this, MainActivity::class.java)
                    sp.edit().putBoolean("logged", false).apply()
                    startActivity(logout)
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
}
