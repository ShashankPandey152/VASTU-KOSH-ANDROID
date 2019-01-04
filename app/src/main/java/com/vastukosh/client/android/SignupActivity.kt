package com.vastukosh.client.android

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_signup.*
import org.json.JSONException
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }

    fun viewTAndCBtnClicked(view: View) {
        val gotoTerms = Intent(this, TAndCActivity::class.java)
        startActivity(gotoTerms)
    }

    fun signupBtnClicked(view: View) {
        var agreed = false
        var errors = 0
        var errorString = ""
        var jsonStatus: String

        if(agreeToTerms.isChecked) {
            agreed = !agreed
        }

        if(signNameText.text.toString() != "" && signLocationText.text.toString() != "" && signAddressText.text.toString() != ""
                && signMobileText.text.toString() != "" && signEmailText.text.toString() != ""
                && signPasswordText.text.toString() != "" && signConfirmPasswordText.text.toString() != "" && agreed) {
            if(!isEmailValid(signEmailText.text.toString())) {
                errors += 1
                errorString += "Invalid email address!\r\n"
            }
            if(signMobileText.text.toString().length != 10) {
                errors += 1
                errorString += "Invalid mobile number!\r\n"
            }
            if(signPasswordText.text.toString() != signConfirmPasswordText.text.toString()) {
                errors += 1
                errorString += "Passwords do not match!\r\n"
            } else if(signPasswordText.text.toString().length < 8) {
                errors += 1
                errorString += "Password cannot be less than 8 characters!"
            }

            if(errors > 0) {
                Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Signing you up....", Toast.LENGTH_SHORT).show()
                val url1 = "http://<website-link>/json/?log=2"
                val url2 = "&email=" + signEmailText.text + "&mobile=" + signMobileText.text
                val url3 = "&name=" + devoidOfSpace(signNameText.text.toString()) + "&location="
                val url4 = devoidOfSpace(signLocationText.text .toString())
                val url5 = "&address=" + devoidOfSpace(signAddressText.text.toString()) + "&password=" + signPasswordText.text
                val url = url1 + url2 + url3 + url4 + url5
                val loginRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
                    try {
                        jsonStatus = response.getString("signup")

                        if(jsonStatus.toInt() == 0) {
                            Toast.makeText(this, "Email address or mobile is already linked with another account!",
                                    Toast.LENGTH_SHORT).show()
                        } else if(jsonStatus.toInt() == 1) {
                            Toast.makeText(this, "Signup successfull!\r\nVerify your email address!",
                                    Toast.LENGTH_SHORT).show()
                            val gotoLogin = Intent(this, MainActivity::class.java)
                            gotoLogin.putExtra("email", signEmailText.text.toString())
                            gotoLogin.putExtra("password", signPasswordText.text.toString())
                            startActivity(gotoLogin)
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
            }
        } else {
            if(!agreed) {
                Toast.makeText(this, "Complete the form!\r\nAgree to T&C!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Complete the form!", Toast.LENGTH_SHORT).show()
            }
        }
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

    fun devoidOfSpace(str: String): String {
        return str.replace("\\s".toRegex(), "%20")
    }

}
