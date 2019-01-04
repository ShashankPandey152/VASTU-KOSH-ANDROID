package com.vastukosh.client.android

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    lateinit var sp:SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sp = getSharedPreferences("login", Context.MODE_PRIVATE)

        if(sp.getBoolean("logged", false)) {
            val gotoHome = Intent(this, HomeActivity::class.java)
            startActivity(gotoHome)
        }

        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")

        logEmailText.setText(email)
        logPasswordText.setText(password)

    }

    override fun onRestart() {
        super.onRestart()
        if(sp.getBoolean("logged", false)) {
            val gotoHome = Intent(this, HomeActivity::class.java)
            startActivity(gotoHome)
        }
    }

    fun loginBtnClicked(view: View) {
        var userName: String
        var userId: String
        var login: String
        var firstName: String
        if(logEmailText.text.toString() != "" && logPasswordText.text.toString() != "") {
            if(isEmailValid(logEmailText.text.toString())) {
                Toast.makeText(this, "Logging in....", Toast.LENGTH_SHORT).show()
                val url1 = "http://<website-link>/json/?log=1"
                val url2 = "&email="+logEmailText.text+"&pass="+logPasswordText.text
                val url = url1+url2
                val loginRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
                    try {
                        userName = response.getString("name")
                        userId = response.getString("id")
                        firstName = response.getString("first")
                        login = response.getString("login")

                        if(login.toInt() == 0) {
                            Toast.makeText(this, "Account does not exist!", Toast.LENGTH_SHORT).show()
                        } else if(login.toInt() == 1) {
                            var verified: String

                            val url3 = "http://<website-link>/json/?checkVerify=1"
                            val url4 = "&email="+logEmailText.text
                            val urL = url3+url4
                            val loginRequest = object: JsonObjectRequest(Method.GET, urL, null, Response.Listener { response ->
                                try {
                                    verified = response.getString("verified")
                                    if(verified == "1") {
                                        Toast.makeText(this, "Logged in successfully!", Toast.LENGTH_SHORT).show()
                                        sp.edit().putBoolean("logged", true).apply()
                                        val gotoHome = Intent(this, HomeActivity::class.java)
                                        sp.edit().putString("id", userId).apply()
                                        sp.edit().putString("name", userName).apply()
                                        sp.edit().putString("first", firstName).apply()
                                        startActivity(gotoHome)
                                    } else if(verified == "2") {
                                        Toast.makeText(this, "Verify email to proceed!", Toast.LENGTH_SHORT).show()
                                        val verifyIntent = Intent(this, VerifyEmailActivity::class.java)
                                        startActivity(verifyIntent)
                                    }
                                } catch (e: JSONException) {
                                    Log.d("JSON", "EXC: " + e.localizedMessage)
                                }
                            }, Response.ErrorListener { error ->
                                Log.d("ERROR", "Could not login user: $error")
                            }) {

                                override fun getBodyContentType(): String {
                                    return "application/json; charset=utf-8"
                                }
                            }
                            Volley.newRequestQueue(this).add(loginRequest)
                        } else if(login.toInt() == 2) {
                            Toast.makeText(this, "Incorrect credentials!", Toast.LENGTH_SHORT).show()
                        } else if(login.toInt() == 3) {
                            Toast.makeText(this, "Account blocked!", Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: JSONException) {
                        Log.d("JSON", "EXC: " + e.localizedMessage)
                    }
                }, Response.ErrorListener { error ->
                    Log.d("ERROR", "Could not login user: $error")
                }) {

                    override fun getBodyContentType(): String {
                        return "application/json; charset=utf-8"
                    }
                }
                Volley.newRequestQueue(this).add(loginRequest)
            } else {
                Toast.makeText(this, "Invalid email address!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Complete the form!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        val exit = Intent(Intent.ACTION_MAIN)
        exit.addCategory(Intent.CATEGORY_HOME)
        exit.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(exit)
    }

    fun logForgotBtnClicked(view: View) {
        val gotoForgot = Intent(this, ForgotActivity::class.java)
        startActivity(gotoForgot)
    }

    fun logSignupBtnClicked(view: View) {
        val gotoSignup = Intent(this, SignupActivity::class.java)
        startActivity(gotoSignup)
    }

    fun isEmailValid(email: String): Boolean {
        return Pattern.compile(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }
}
