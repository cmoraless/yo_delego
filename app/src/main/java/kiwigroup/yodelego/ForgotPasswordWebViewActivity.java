package kiwigroup.yodelego;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ForgotPasswordWebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_web_view);

        final WebView myWebView = findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl("https://web.yodelego.com/accounts/password-reset/");
        myWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                myWebView.setVisibility(View.VISIBLE);
            }
        });
    }
}
