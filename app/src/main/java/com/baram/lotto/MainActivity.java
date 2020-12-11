package com.baram.lotto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends AppCompatActivity {
    Button btnLotto, btnMapView, btnLottoHistory;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 로또번호추첨
        btnLotto = findViewById(R.id.btnLotto);
        btnLotto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rIntent = new Intent(getApplicationContext(), RandomLottoActivity.class);
                startActivity(rIntent);
            }
        });

        // 주변 판매점 버튼
        btnMapView = findViewById(R.id.btnMapView);
        btnMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), MapViewActivity.class);
                // 주변 지도보기 화면으로 전환
                startActivity(mIntent);
            }
        });

        // 역대 로또 정보 버튼
        btnLottoHistory = findViewById(R.id.btnLottoHistory);
        btnLottoHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), LottoHistoryActivity.class);
                // 주변 지도보기 화면으로 전환
                startActivity(mIntent);
            }
        });

        // 광고배너
        AdView adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}