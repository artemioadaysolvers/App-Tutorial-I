package com.ejemplo.calculadorasencilla;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Insets;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** A minimal Android container. The calculator itself lives in assets/www. */
public final class MainActivity extends Activity {
    private static final String HOST = "calculadora.local";
    private static final String HOME = "https://" + HOST + "/index.html";
    private static final int BACKGROUND = Color.rgb(246, 245, 240);
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 30) {
            getWindow().setDecorFitsSystemWindows(false);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(BACKGROUND);
        root.setOnApplyWindowInsetsListener((view, insets) -> {
            if (Build.VERSION.SDK_INT >= 30) {
                Insets safe = insets.getInsets(WindowInsets.Type.systemBars()
                        | WindowInsets.Type.displayCutout() | WindowInsets.Type.ime());
                view.setPadding(safe.left, safe.top, safe.right, safe.bottom);
            } else {
                view.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            }
            return insets;
        });

        webView = new WebView(this);
        webView.setBackgroundColor(BACKGROUND);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setBlockNetworkLoads(true);
        settings.setSupportMultipleWindows(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);

        // Serve only these bundled files, using a fixed HTTPS origin, without Internet permission.
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if (!isLocal(uri) || !"GET".equals(request.getMethod())) return notFound();
                String path = uri.getPath();
                String mime;
                if ("/index.html".equals(path)) mime = "text/html";
                else if ("/styles.css".equals(path)) mime = "text/css";
                else if ("/engine.js".equals(path) || "/app.js".equals(path)) mime = "text/javascript";
                else if ("/favicon.svg".equals(path)) mime = "image/svg+xml";
                else return notFound();
                try {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Cache-Control", "no-store");
                    headers.put("X-Content-Type-Options", "nosniff");
                    return new WebResourceResponse(mime, "UTF-8", 200, "OK", headers,
                            getAssets().open("www" + path));
                } catch (IOException exception) {
                    return notFound();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return !HOME.equals(request.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return !HOME.equals(url);
            }
        });
        root.addView(webView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(root);
        root.requestApplyInsets();
        root.post(this::updateSystemBarAppearance);
        webView.loadUrl(HOME);
    }

    private void updateSystemBarAppearance() {
        if (Build.VERSION.SDK_INT >= 30) {
            // Ask the decor view only after setContentView has created the window content.
            // A controller can still be absent until the view is attached to its window.
            WindowInsetsController controller = getWindow().getDecorView().getWindowInsetsController();
            if (controller != null) {
                int appearance = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                        | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;
                controller.setSystemBarsAppearance(appearance, appearance);
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) updateSystemBarAppearance();
    }

    private static boolean isLocal(Uri uri) {
        return "https".equals(uri.getScheme()) && HOST.equals(uri.getHost())
                && uri.getPort() == -1 && uri.getUserInfo() == null;
    }

    private static WebResourceResponse notFound() {
        return new WebResourceResponse("text/plain", "UTF-8", 404, "Not Found",
                new HashMap<>(), new ByteArrayInputStream(new byte[0]));
    }

    @Override
    protected void onPause() {
        if (webView != null) webView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) webView.onResume();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            if (webView.getParent() instanceof ViewGroup) {
                ((ViewGroup) webView.getParent()).removeView(webView);
            }
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
