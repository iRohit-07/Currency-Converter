package com.example.currencyconverter // change to your own package name

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val currencies = listOf(
        "USD", "EUR", "INR", "GBP", "JPY", "AUD", "CAD", "CHF", "CNY", "SGD"
    )

    // Frankfurter: free, no API key needed
    private val baseUrl = "https://api.frankfurter.dev/v1/latest"

    private lateinit var etAmount: EditText
    private lateinit var spFrom: Spinner
    private lateinit var spTo: Spinner
    private lateinit var btnConvert: Button
    private lateinit var tvResult: TextView
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
            androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
        )

        setContentView(R.layout.activity_main)

        etAmount = findViewById(R.id.etAmount)
        spFrom = findViewById(R.id.spFrom)
        spTo = findViewById(R.id.spTo)
        btnConvert = findViewById(R.id.btnConvert)
        tvResult = findViewById(R.id.tvResult)
        progress = findViewById(R.id.progress)

        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, currencies
        )
        spFrom.adapter = adapter
        spTo.adapter = adapter
        spFrom.setSelection(0) // USD
        spTo.setSelection(2)   // INR

        btnConvert.setOnClickListener { convert() }
    }

    private fun convert() {
        val amountText = etAmount.text.toString().trim()
        if (amountText.isEmpty()) {
            etAmount.error = "Enter an amount"
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null) {
            etAmount.error = "Invalid number"
            return
        }

        val from = spFrom.selectedItem.toString()
        val to = spTo.selectedItem.toString()

        if (from == to) {
            tvResult.text = String.format(Locale.US, "%.2f %s = %.2f %s", amount, from, amount, to)
            return
        }

        val url = "$baseUrl?amount=$amountText&from=$from&to=$to"

        progress.visibility = View.VISIBLE
        btnConvert.isEnabled = false
        tvResult.text = ""

        // Network calls must NOT run on the main (UI) thread
        thread {
            try {
                val json = httpGet(url)                     // 1. send HTTP GET request
                val obj = JSONObject(json)                  // 2. parse JSON response
                val result = obj.getJSONObject("rates").getDouble(to)
                val date = obj.getString("date")

                val text = String.format(
                    Locale.US, "%.2f %s = %.2f %s\n(Rate date: %s)",
                    amount, from, result, to, date
                )
                runOnUiThread { showResult(text) }          // 3. update UI on main thread
            } catch (e: Exception) {
                runOnUiThread { showResult("Error: ${e.message}") }
            }
        }
    }

    private fun showResult(text: String) {
        progress.visibility = View.GONE
        btnConvert.isEnabled = true
        tvResult.text = text
    }

    /** Sends an HTTP GET request and returns the response body as a String. */
    private fun httpGet(urlString: String): String {
        val conn = URL(urlString).openConnection() as HttpURLConnection
        try {
            conn.requestMethod = "GET"
            conn.connectTimeout = 10000
            conn.readTimeout = 10000

            val code = conn.responseCode
            if (code != HttpURLConnection.HTTP_OK) {
                throw IOException("Server returned HTTP $code")
            }
            return conn.inputStream.bufferedReader().use { it.readText() }
        } finally {
            conn.disconnect()
        }
    }
}