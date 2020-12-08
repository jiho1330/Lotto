package com.baram.lotto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.naver.maps.map.MapView;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {
    Button btnLotto, btnMapView, btnLottoHistory;

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
    }
}