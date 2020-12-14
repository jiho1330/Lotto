package com.baram.lotto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebViewActivity extends AppCompatActivity {
    private WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent mIntent = getIntent();
        String uri = mIntent.getStringExtra("URI");

        wv = findViewById(R.id.wv);
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true); //자바 스크립트 사용을 할 수 있도록 설정
        wv.setWebViewClient(new WebViewClient(){ //페이지 로딩이 끝나면 호출
            @Override public void onPageFinished(WebView view,String url){
                //Toast.makeText(WebViewActivity.this,"불러오기 완료", Toast.LENGTH_SHORT).show();
            }
        });

        wv.loadUrl(uri);
    }
}