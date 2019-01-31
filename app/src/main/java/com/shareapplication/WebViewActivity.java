package com.shareapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.http.SslError;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class WebViewActivity extends AppCompatActivity {

    WebView webView;
    ProgressBar web_loader;

    String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = (WebView) findViewById(R.id.webView);
        web_loader = (ProgressBar) findViewById(R.id.web_loader);
        web_loader.setVisibility(View.VISIBLE);

        if (getIntent() != null) {
            Intent intent = getIntent();
            URL = intent.getStringExtra("URL");

        }

        Log.e("Nive ", "onCreate:URL " + URL);

        loadWebViewLoad(webView);
    }

    private void loadWebViewLoad(final WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new SSLTolerentWebViewClient());

        if (URL != null) {
            webView.loadUrl(URL);
        }

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

                Log.e("Nive ", "onReceivedError:description " + description);
                Log.e("Nive ", "onReceivedError:failingUrl " + failingUrl);
            }

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                web_loader.setVisibility(View.GONE);
            }
        });
    }


    private class SSLTolerentWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {

            Log.e("Nive ", "onReceivedSslError:handler " + handler);
            Log.e("Nive ", "onReceivedSslError:error " + error);
            final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setMessage(getResources().getString(R.string.txt_load_web));
            builder.setPositiveButton(getResources().getString(R.string.txt_continue), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton(getResources().getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                    onBackPressed();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
        }

    }
}
