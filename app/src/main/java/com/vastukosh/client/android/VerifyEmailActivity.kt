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
import org.json.JSONException

class VerifyEmailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_email)
    }

    fun resendBtnClicked(view: View) {
        Toast.makeText(this, "Sending mail...", Toast.LENGTH_SHORT).show()
        val url1 = "http://vastukosh-com.stackstaging.com/json/?resend=1"
        val url2 = "&id=90"
        val url = url1+url2
        val loginRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
            try {
                val sent = response.getString("sent")

                if(sent.toInt() == 1) {
                    Toast.makeText(this, "Mail resent successfully!\r\nVerify mail and login to conitnue!",
                            Toast.LENGTH_LONG).show()
                } else if(sent.toInt() == -1) {
                    Toast.makeText(this, "Oops, there was some error, try again later!", Toast.LENGTH_SHORT).show()
                }

                val gotoLogin = Intent(this, MainActivity::class.java)
                startActivity(gotoLogin)

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

    fun verifyBackBtnClicked(view: View) {
        val gotoMain = Intent(this, MainActivity::class.java)
        startActivity(gotoMain)
    }
}
