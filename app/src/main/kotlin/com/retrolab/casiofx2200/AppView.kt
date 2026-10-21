package com.retrolab.casiofx2200

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.Color
import android.media.AudioManager
import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.WindowInsets
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import org.json.JSONObject
import java.io.ByteArrayInputStream

/** Sole visual surface: HTML/CSS/WebGL. All native application code is Kotlin. */
@SuppressLint("SetJavaScriptEnabled", "ViewConstructor")
class AppView(private val host: Activity) : WebView(host) {
    private val engine = CalculatorEngine()
    private val buttonSound = ButtonSound(host)
    private val preferences = host.getSharedPreferences("casiofx2200", Context.MODE_PRIVATE)
    private var fullscreen = false
    private var sound = preferences.getBoolean("sound", true)
    private var haptics = preferences.getBoolean("haptics", true)
    init {
        host.volumeControlStream = AudioManager.STREAM_MUSIC
        setBackgroundColor(Color.rgb(232,223,205))
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.allowFileAccess = false
        settings.allowContentAccess = false
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        settings.setSupportMultipleWindows(false)
        isHapticFeedbackEnabled = true
        isSoundEffectsEnabled = false
        engine.setMode(preferences.getString("mode", "RAD") ?: "RAD")
        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
        overScrollMode = View.OVER_SCROLL_NEVER
        WebView.setWebContentsDebuggingEnabled(host.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0)
        val loader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(host)).build()
        webViewClient = object : WebViewClientCompat() {
            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse {
                // Missing resources never fall through to the network. The APK needs no INTERNET permission.
                return loader.shouldInterceptRequest(request.url) ?: WebResourceResponse(
                    "text/plain", "UTF-8", 404, "Not Found", emptyMap(), ByteArrayInputStream("Recurso local no encontrado".toByteArray()))
            }
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url=request.url
                return !(url.scheme=="https" && url.host=="appassets.androidplatform.net" && url.path=="/assets/index.html")
            }
        }
        if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_LISTENER)) {
            WebViewCompat.addWebMessageListener(this,"NativeApp",setOf(ORIGIN)) { _, message, origin, mainFrame, reply ->
                if (!mainFrame || origin.toString()!=ORIGIN) return@addWebMessageListener
                try {
                    val request=JSONObject(message.data ?: "{}")
                    val id=request.optInt("id")
                    val response=handle(request).put("id",id).put("ok",true)
                    reply.postMessage(response.toString())
                } catch (exception: Exception) {
                    val id=runCatching { JSONObject(message.data ?: "{}").optInt("id") }.getOrDefault(0)
                    reply.postMessage(JSONObject().put("id",id).put("ok",false).put("error",exception.message ?: "Error nativo").toString())
                }
            }
        }
        // Keep the whole document inside system bars on Android 15/16 as well.
        setOnApplyWindowInsetsListener { _, insets ->
            if (Build.VERSION.SDK_INT >= 30) {
                val bars=insets.getInsets(WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout())
                setPadding(bars.left,bars.top,bars.right,bars.bottom)
            } else {
                @Suppress("DEPRECATION")
                setPadding(insets.systemWindowInsetLeft,insets.systemWindowInsetTop,insets.systemWindowInsetRight,insets.systemWindowInsetBottom)
            }
            insets
        }
        loadUrl("$ORIGIN/assets/index.html")
    }
    private fun state(): JSONObject = JSONObject().put("display", engine.display()).put("value", engine.value)
        .put("memory", engine.format(engine.memory)).put("mode", engine.mode).put("inverse", engine.inverse)
        .put("hyper", engine.hyper).put("powered", engine.powered).put("pending", engine.pendingDisplay())
        .put("count", engine.count).put("error", engine.error).put("notice", engine.notice)
        .put("haptics", haptics).put("sound", sound)
    private fun handle(request: JSONObject): JSONObject {
        when(request.optString("action")) {
            "feedback" -> {
                if(sound) buttonSound.play()
                return JSONObject()
            }
            "key" -> {
                val key=request.optString("key")
                require(key.length in 1..24)
                engine.press(key)
                if(key=="MODE") preferences.edit().putString("mode",engine.mode).apply()
                if(haptics) performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            }
            "settings" -> {
                if (request.has("mode")) {
                    engine.setMode(request.getString("mode"))
                    preferences.edit().putString("mode",engine.mode).apply()
                }
                haptics=request.optBoolean("haptics",haptics)
                sound=request.optBoolean("sound",sound)
                preferences.edit().putBoolean("haptics",haptics).putBoolean("sound",sound).apply()
            }
            "copy" -> (host.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                .setPrimaryClip(ClipData.newPlainText("Casio fx-2200", engine.display()))
            "share" -> host.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type="text/plain"; putExtra(Intent.EXTRA_TEXT,"Casio fx-2200 · ${engine.mode}\n${engine.display()}")
            }, "Compartir cálculo"))
            "fullscreen" -> {
                fullscreen=!fullscreen
                if(Build.VERSION.SDK_INT>=30) {
                    if(fullscreen) host.window.insetsController?.hide(WindowInsets.Type.systemBars())
                    else host.window.insetsController?.show(WindowInsets.Type.systemBars())
                } else {
                    @Suppress("DEPRECATION")
                    host.window.decorView.systemUiVisibility=if(fullscreen) View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY else View.SYSTEM_UI_FLAG_VISIBLE
                }
                requestApplyInsets()
            }
            "source" -> {
                val url = request.optString("url")
                require(url in setOf(
                    "https://www.worldradiohistory.com/Archive-Practical-Electronics/70s/Practical-Electronics-1978-06.pdf",
                    "https://casio.ledudu.com/pockets.asp?type=1345",
                    "https://casio.ledudu.com/pockets.asp?type=1029",
                    "https://casio.ledudu.com/images/calculs/casio/manuels/fx911.pdf"
                )) { "Fuente no permitida." }
                host.startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url)))
            }
            "state" -> Unit
            else -> throw IllegalArgumentException("Acción desconocida")
        }
        return JSONObject().put("state",state()).put("platform","Android ${Build.VERSION.RELEASE} · Kotlin")
    }
    override fun onPause() {
        buttonSound.stop()
        super.onPause()
    }
    override fun destroy() {
        buttonSound.release()
        super.destroy()
    }
    companion object { private const val ORIGIN="https://appassets.androidplatform.net" }
}
