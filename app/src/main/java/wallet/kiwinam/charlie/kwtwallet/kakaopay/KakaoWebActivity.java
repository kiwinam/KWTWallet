package wallet.kiwinam.charlie.kwtwallet.kakaopay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import wallet.kiwinam.charlie.kwtwallet.R;

public class KakaoWebActivity extends AppCompatActivity{
    private WebView webView;
    private int value = 0;
    @SuppressLint({"SetJavaScriptEnabled"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao);
        value = getIntent().getIntExtra("value",0);

        webView = findViewById(R.id.kakaoWebView);
        Intent resultIntent = new Intent();
        webView.addJavascriptInterface(new KakaoInterface() {
            @Override
            @JavascriptInterface
            public void success() {
                Log.d("js","success");
                setResult(RESULT_OK, resultIntent);
                finish();
            }

            @Override
            @JavascriptInterface
            public void failed() {
                Log.d("js","failed");
                setResult(RESULT_CANCELED, resultIntent);
                finish();
            }
        }, "interface");
        webView.setWebViewClient(new KakaoWebViewClient(this));
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        webView.loadUrl("http://13.125.64.135/iamport/kakao.php?value="+value);
    }
}
