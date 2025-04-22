package org.epstudios.morbidmeter

/**
Copyright (C) 2025 EP Studios, Inc.
www.epstudiossoftware.com

Created by mannd on 4/21/25.

This file is part of morbidmeter-android.

morbidmeter-android is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

morbidmeter-android is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with morbidmeter-android.  If not, see <http://www.gnu.org/licenses/>.
 */
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class HelpActivity : AppCompatActivity() {

    companion object {
        private const val LOG_TAG = "HelpActivity"
    }

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help) // Your layout for the HelpActivity

        webView = findViewById<WebView>(R.id.helpWebView)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.addJavascriptInterface(WebAppInterface(this), "Android")
        val htmlContent = loadHelpHtml()

        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    class WebAppInterface(private val activity: HelpActivity) {
       @JavascriptInterface
       fun close() {
           Log.d(LOG_TAG, "close called")
           activity.finish()
       }
    }

    private fun loadHelpHtml(): String {
        Log.d(LOG_TAG, "loadHelpHtml called")
        val assetManager = assets
        val stringBuilder = StringBuilder()
        try {
            val inputStream: InputStream = assetManager.open("help.html")
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Error loading help.html", e)
        }
        return stringBuilder.toString()
    }
}
