package com.baram.lotto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 로또정보 동기화
        new Thread(()-> {
            PreferenceLottoData.getPreferenceLottoData(SplashActivity.this).updateLottoData(getCurrentRound());
        }).start();

        // 2초 뒤 메인화면 이동
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplication(), MainActivity.class));    // 메인 Activity로 이동
                SplashActivity.this.finish();   // 현재 Activity 종료
            }
        }, 2000);    // 2초 뒤 실행
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }

    // 현재 회차를 구함
    private int getCurrentRound() {
        // 복권 1주차 2002.12.07
        Calendar c1 = new GregorianCalendar(2002, 12 - 1, 7);
        // 현재일자
        Calendar c2 = Calendar.getInstance();

        // 두 날짜를 milliseconds로 변환 후 차이를 구함
        long ms1 = c1.getTimeInMillis() + (1000 * 60 * 60 * 21); // 자정 + 21시간 = 토요일 오후 9시
        long ms2 = c2.getTimeInMillis();
        long milliDiff = ms2 - ms1;

        // 차이 milliseconds를 1주일로 나눔
        int weekDiff = (int)(milliDiff / (1000 * 60 * 60 * 24 * 7));

        return weekDiff + 1;
    }

}