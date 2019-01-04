package com.vastukosh.client.android

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.Response.Listener
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_forgot.*
import org.json.JSONException
import java.util.regex.Pattern

class ForgotActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
    }

    fun forgotBtnClicked(view: View) {
        var forgot: String

        if(forgotEmailText.text.toString() != "") {
            if(isEmailValid(forgotEmailText.text.toString())) {
                Toast.makeText(this, "Sending mail...", Toast.LENGTH_SHORT).show()
                val url1 = "http://<website-link>/json/?forgot=1"
                val url2 = "&email="+forgotEmailText.text
                val url = url1+url2
                val loginRequest = object: JsonObjectRequest(Method.GET, url, null, Listener { response ->
                    try {
                        forgot = response.getString("forgot")

                        if(forgot.toInt() == 1) {
                            Toast.makeText(this, "Mail sent successfully!", Toast.LENGTH_SHORT).show()
                            val gotoLogin = Intent(this, MainActivity::class.java)
                            startActivity(gotoLogin)
                        } else if(forgot.toInt() == 2) {
                            Toast.makeText(this, "Account does not exist!", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Invalid email address!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Enter email address!", Toast.LENGTH_SHORT).show()
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
}
