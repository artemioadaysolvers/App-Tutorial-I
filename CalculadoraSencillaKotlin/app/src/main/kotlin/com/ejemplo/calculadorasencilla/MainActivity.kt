package com.ejemplo.calculadorasencilla

import android.app.Activity
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout

/** Native Android host. HTML, CSS and JavaScript live in assets/www. */
class MainActivity : Activity() {
    private var webView: WebView? = null

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 30) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        val root = FrameLayout(this)
        root.setBackgroundColor(BACKGROUND)
        root.setOnApplyWindowInsetsListener { view, insets ->
            if (Build.VERSION.SDK_INT >= 30) {
                val safe = insets.getInsets(
                    WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout() or
                        WindowInsets.Type.ime()
                )
                view.setPadding(safe.left, safe.top, safe.right, safe.bottom)
            } else {
                view.setPadding(
                    insets.systemWindowInsetLeft, insets.systemWindowInsetTop,
                    insets.systemWindowInsetRight, insets.systemWindowInsetBottom
                )
            }
            insets
        }

        val content = WebView(this)
        webView = content
        content.setBackgroundColor(BACKGROUND)
        content.overScrollMode = View.OVER_SCROLL_NEVER
        content.settings.apply {
            javaScriptEnabled = true
            allowFileAccess = false
            allowContentAccess = false
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            blockNetworkLoads = true
            setSupportMultipleWindows(false)
            javaScriptCanOpenWindowsAutomatically = false
        }

        // Only serve bundled assets. No Internet permission or JavaScript bridge is needed.
        content.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?, request: WebResourceRequest?
            ): WebResourceResponse {
                if (request == null) return notFound()
                val uri = request.url
                if (!isLocal(uri) || request.method != "GET") return notFound()
                val path = uri.path
                val mime = when (path) {
                    "/index.html" -> "text/html"
                    "/styles.css" -> "text/css"
                    "/engine.js", "/app.js" -> "text/javascript"
                    "/favicon.svg" -> "image/svg+xml"
                    else -> return notFound()
                }
                return runCatching {
                    WebResourceResponse(
                        mime, "UTF-8", 200, "OK",
                        mapOf("Cache-Control" to "no-store", "X-Content-Type-Options" to "nosniff"),
                        assets.open("www$path")
                    )
                }.getOrElse { failure ->
                    // Convert ordinary loading failures to 404, keeping fatal errors visible.
                    if (failure !is Exception) throw failure
                    notFound()
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean =
                request?.url?.toString() != HOME

            @Deprecated("Required for devices using the legacy WebView callback")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean = url != HOME
        }
        root.addView(
            content,
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        )
        setContentView(root)
        root.requestApplyInsets()
        // Create the screen before accessing its window controller (Android 15 startup fix).
        root.post { updateSystemBarAppearance() }
        content.loadUrl(HOME)
    }

    private fun updateSystemBarAppearance() {
        if (Build.VERSION.SDK_INT >= 30) {
            val controller = window.decorView.windowInsetsController ?: return
            val appearance = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            controller.setSystemBarsAppearance(appearance, appearance)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) updateSystemBarAppearance()
    }

    private fun isLocal(uri: Uri): Boolean =
        uri.scheme == "https" && uri.host == HOST && uri.port == -1 && uri.userInfo == null

    private fun notFound(): WebResourceResponse =
        WebResourceResponse("text/plain", "UTF-8", 404, "Not Found", emptyMap(), null)

    override fun onPause() {
        webView?.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView?.onResume()
    }

    override fun onDestroy() {
        webView?.let { content ->
            (content.parent as? ViewGroup)?.removeView(content)
            content.destroy()
        }
        webView = null
        super.onDestroy()
    }

    companion object {
        private const val HOST = "calculadora.local"
        private const val HOME = "https://$HOST/index.html"
        private val BACKGROUND = Color.rgb(246, 245, 240)
    }
}
