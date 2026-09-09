package com.kabilahtour.digitalbook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String HOME_URL =
            "https://digital-book.kabilahtour.com/";

    private WebView webView;
    private FrameLayout root;
    private ProgressBar progress;
    private LinearLayout offlineView;
    private boolean hasMainFrameError = false;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Warna area sistem Android.
         */
        getWindow().setStatusBarColor(
                Color.rgb(16, 18, 23)
        );

        getWindow().setNavigationBarColor(
                Color.rgb(16, 18, 23)
        );

        buildUi();

        /*
         * Menangani edge-to-edge Android 15/16 dengan benar.
         * Konten WebView diberi ruang sesuai system bars,
         * sehingga tidak tertutup status bar / navigation bar.
         */
        applySystemBarInsets();

        configureWebView();

        loadHome();
    }

    private void buildUi() {

        root = new FrameLayout(this);
        root.setBackgroundColor(
                Color.rgb(16, 18, 23)
        );

        /*
         * WebView utama.
         */
        webView = new WebView(this);
        webView.setBackgroundColor(
                Color.rgb(16, 18, 23)
        );

        FrameLayout.LayoutParams webViewLp =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );

        root.addView(
                webView,
                webViewLp
        );

        /*
         * Loading indicator.
         */
        progress = new ProgressBar(this);
        progress.setIndeterminate(true);

        FrameLayout.LayoutParams progressLp =
                new FrameLayout.LayoutParams(
                        56,
                        56
                );

        progressLp.gravity = Gravity.CENTER;

        root.addView(
                progress,
                progressLp
        );

        /*
         * Tampilan ketika koneksi gagal.
         */
        offlineView = new LinearLayout(this);
        offlineView.setOrientation(
                LinearLayout.VERTICAL
        );

        offlineView.setGravity(
                Gravity.CENTER
        );

        offlineView.setPadding(
                48,
                48,
                48,
                48
        );

        offlineView.setBackgroundColor(
                Color.rgb(16, 18, 23)
        );

        offlineView.setVisibility(
                View.GONE
        );

        /*
         * Judul.
         */
        TextView title = new TextView(this);

        title.setText(
                "Koneksi internet diperlukan"
        );

        title.setTextColor(
                Color.WHITE
        );

        title.setTextSize(20);

        title.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        title.setGravity(
                Gravity.CENTER
        );

        /*
         * Pesan.
         */
        TextView message = new TextView(this);

        message.setText(
                "Digital Book Kabilah mengambil konten terbaru langsung dari website."
        );

        message.setTextColor(
                Color.LTGRAY
        );

        message.setTextSize(14);

        message.setGravity(
                Gravity.CENTER
        );

        message.setPadding(
                0,
                16,
                0,
                24
        );

        /*
         * Tombol coba lagi.
         */
        Button retry = new Button(this);

        retry.setText(
                "Coba Lagi"
        );

        retry.setTextColor(
                Color.rgb(16, 18, 23)
        );

        retry.setBackgroundResource(
                com.kabilahtour.digitalbook.R.drawable.bg_retry
        );

        retry.setOnClickListener(
                v -> loadHome()
        );

        /*
         * Masukkan komponen.
         */
        offlineView.addView(
                title,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        offlineView.addView(
                message,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        LinearLayout.LayoutParams retryLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        offlineView.addView(
                retry,
                retryLp
        );

        root.addView(
                offlineView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );

        setContentView(root);
    }

    /**
     * Menghindari konten WebView tertutup status bar dan
     * navigation bar pada Android yang menggunakan edge-to-edge.
     *
     * Tidak mengubah ukuran website dan tidak mengubah source
     * digital-book.kabilahtour.com.
     */
    private void applySystemBarInsets() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }

        root.setOnApplyWindowInsetsListener((v, insets) -> {
            int top;
            int bottom;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                android.graphics.Insets systemBars =
                        insets.getInsets(WindowInsets.Type.systemBars());

                top = systemBars.top;
                bottom = systemBars.bottom;
            } else {
                top = insets.getSystemWindowInsetTop();
                bottom = insets.getSystemWindowInsetBottom();
            }

            v.setPadding(
                    0,
                    top,
                    0,
                    bottom
            );

            return insets;
        });

        root.post(() -> root.requestApplyInsets());
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configureWebView() {

        WebSettings settings =
                webView.getSettings();

        settings.setJavaScriptEnabled(true);

        settings.setDomStorageEnabled(true);

        settings.setDatabaseEnabled(true);

        /*
         * Audio mengikuti gesture pengguna.
         */
        settings.setMediaPlaybackRequiresUserGesture(true);

        settings.setLoadsImagesAutomatically(true);

        settings.setJavaScriptCanOpenWindowsAutomatically(false);

        settings.setSupportMultipleWindows(false);

        settings.setBuiltInZoomControls(false);

        settings.setDisplayZoomControls(false);

        settings.setSupportZoom(false);

        settings.setCacheMode(
                WebSettings.LOAD_DEFAULT
        );

        settings.setMixedContentMode(
                WebSettings.MIXED_CONTENT_NEVER_ALLOW
        );

        settings.setAllowFileAccess(false);

        settings.setAllowContentAccess(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.setSafeBrowsingEnabled(true);
        }

        settings.setUserAgentString(
                settings.getUserAgentString()
                        + " DigitalBookKabilah/1.0"
        );

        /*
         * Cookie.
         */
        CookieManager
                .getInstance()
                .setAcceptCookie(true);

        CookieManager
                .getInstance()
                .setAcceptThirdPartyCookies(
                        webView,
                        true
                );

        /*
         * WebViewClient.
         */
        webView.setWebViewClient(
                new WebViewClient() {

                    @Override
                    public boolean shouldOverrideUrlLoading(
                            WebView view,
                            WebResourceRequest request
                    ) {

                        return handleExternalScheme(
                                request.getUrl().toString()
                        );
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(
                            WebView view,
                            String url
                    ) {

                        return handleExternalScheme(url);
                    }

                    @Override
                    public void onPageStarted(
                            WebView view,
                            String url,
                            android.graphics.Bitmap favicon
                    ) {

                        hasMainFrameError = false;

                        progress.setVisibility(
                                View.VISIBLE
                        );

                        offlineView.setVisibility(
                                View.GONE
                        );
                    }

                    @Override
                    public void onPageFinished(
                            WebView view,
                            String url
                    ) {

                        progress.setVisibility(
                                View.GONE
                        );

                        if (!hasMainFrameError) {

                            offlineView.setVisibility(
                                    View.GONE
                            );
                        }
                    }

                    @Override
                    public void onReceivedError(
                            WebView view,
                            WebResourceRequest request,
                            WebResourceError error
                    ) {

                        if (
                                request != null
                                        && request.isForMainFrame()
                        ) {

                            hasMainFrameError = true;

                            progress.setVisibility(
                                    View.GONE
                            );

                            offlineView.setVisibility(
                                    View.VISIBLE
                            );
                        }
                    }

                    @Override
                    public void onReceivedHttpError(
                            WebView view,
                            WebResourceRequest request,
                            WebResourceResponse errorResponse
                    ) {

                        if (
                                request != null
                                        && request.isForMainFrame()
                                        && errorResponse != null
                                        && errorResponse.getStatusCode() >= 500
                        ) {

                            hasMainFrameError = true;

                            progress.setVisibility(
                                    View.GONE
                            );

                            offlineView.setVisibility(
                                    View.VISIBLE
                            );
                        }
                    }

                    @Override
                    public void onReceivedSslError(
                            WebView view,
                            SslErrorHandler handler,
                            SslError error
                    ) {

                        /*
                         * Sertifikat SSL yang bermasalah
                         * tidak diterima.
                         */
                        handler.cancel();

                        hasMainFrameError = true;

                        progress.setVisibility(
                                View.GONE
                        );

                        offlineView.setVisibility(
                                View.VISIBLE
                        );
                    }
                }
        );

        /*
         * WebChromeClient.
         */
        webView.setWebChromeClient(
                new WebChromeClient()
        );

        /*
         * Download listener.
         */
        webView.setDownloadListener(
                (
                        url,
                        userAgent,
                        contentDisposition,
                        mimeType,
                        contentLength
                ) -> {

                    try {

                        Intent intent =
                                new Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(url)
                                );

                        startActivity(intent);

                    } catch (ActivityNotFoundException ignored) {

                        Toast.makeText(
                                this,
                                "Tidak ada aplikasi yang dapat membuka file ini.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }

    private boolean handleExternalScheme(
            String url
    ) {

        Uri uri =
                Uri.parse(url);

        String scheme =
                uri.getScheme();

        if (scheme == null) {
            return false;
        }

        /*
         * HTTP / HTTPS.
         */
        if (
                scheme.equalsIgnoreCase("http")
                        || scheme.equalsIgnoreCase("https")
        ) {

            String host =
                    uri.getHost();

            /*
             * WhatsApp.
             */
            if (
                    host != null
                            && (
                            host.equalsIgnoreCase("wa.me")
                                    || host.equalsIgnoreCase(
                                    "api.whatsapp.com"
                            )
                    )
            ) {

                try {

                    startActivity(
                            new Intent(
                                    Intent.ACTION_VIEW,
                                    uri
                            )
                    );

                    return true;

                } catch (ActivityNotFoundException ignored) {

                    return false;
                }
            }

            /*
             * Google Maps.
             */
            if (
                    host != null
                            && host.equalsIgnoreCase(
                            "maps.google.com"
                    )
            ) {

                try {

                    startActivity(
                            new Intent(
                                    Intent.ACTION_VIEW,
                                    uri
                            )
                    );

                    return true;

                } catch (ActivityNotFoundException ignored) {

                    return false;
                }
            }

            /*
             * URL lainnya tetap berada
             * di dalam WebView.
             */
            return false;
        }

        /*
         * tel:, mailto:, whatsapp:, dll.
         */
        try {

            startActivity(
                    new Intent(
                            Intent.ACTION_VIEW,
                            uri
                    )
            );

        } catch (ActivityNotFoundException e) {

            Toast.makeText(
                    this,
                    "Tidak dapat membuka tautan.",
                    Toast.LENGTH_SHORT
            ).show();
        }

        return true;
    }

    private void loadHome() {

        hasMainFrameError = false;

        offlineView.setVisibility(
                View.GONE
        );

        progress.setVisibility(
                View.VISIBLE
        );

        webView.loadUrl(
                HOME_URL
        );
    }

    @Override
    public void onBackPressed() {

        if (
                webView != null
                        && webView.canGoBack()
        ) {

            webView.goBack();

        } else {

            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {

        if (webView != null) {

            webView.stopLoading();

            webView.setWebChromeClient(
                    null
            );

            webView.setWebViewClient(
                    null
            );

            webView.destroy();
        }

        super.onDestroy();
    }
}