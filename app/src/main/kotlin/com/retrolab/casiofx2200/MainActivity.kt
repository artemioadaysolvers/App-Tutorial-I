package com.retrolab.casiofx2200

import android.app.Activity
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Build
import android.window.OnBackInvokedDispatcher

class MainActivity : Activity() {
    private lateinit var appView: AppView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appView = AppView(this)
        setContentView(appView)
        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT) { handleBack() }
        }
    }
    override fun onPause() { appView.onPause(); super.onPause() }
    override fun onResume() { super.onResume(); if (::appView.isInitialized) appView.onResume() }
    @Deprecated("Platform back callback for API 26 compatibility")
    // API 33+ uses OnBackInvokedDispatcher above; this override serves API 26–32.
    @SuppressLint("GestureBackNavigation")
    override fun onBackPressed() {
        handleBack()
    }
    private fun handleBack() {
        appView.evaluateJavascript("window.appBack && window.appBack()") { result ->
            if (result != "true") finish()
        }
    }
    override fun onDestroy() { appView.destroy(); super.onDestroy() }
}
