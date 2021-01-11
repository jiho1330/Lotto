package com.baram.lotto;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baram.lotto.model.LottoData;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Calendar;
import java.util.GregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLotto, btnQRCode, btnMapView, btnLottoHistory, btnVersion, btnQnA, btnRate, btnMyNumber;
    private Button[] balls = new Button[7];
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private TextView tvTime, tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 로또번호추첨
        btnLotto = findViewById(R.id.btnLotto);
        btnLotto.setOnClickListener(this);

        // 주변 판매점 버튼
        btnMapView = findViewById(R.id.btnMapView);
        btnMapView.setOnClickListener(this);

        // 역대 로또 정보 버튼
        btnLottoHistory = findViewById(R.id.btnLottoHistory);
        btnLottoHistory.setOnClickListener(this);

        // QR코드 스캔 버튼
        btnQRCode = findViewById(R.id.btnQRCode);
        btnQRCode.setOnClickListener(this);

        // 버전 버튼
        btnVersion = findViewById(R.id.btnVerion);
        btnVersion.setOnClickListener(this);

        // Q&A 버튼
        btnQnA = findViewById(R.id.btnQnA);
        btnQnA.setOnClickListener(this);

        // 평가하기 버튼
        btnRate = findViewById(R.id.btnRate);
        btnRate.setOnClickListener(this);

        // 내 번호 버튼
        btnMyNumber = findViewById(R.id.btnMyNumber);
        btnMyNumber.setOnClickListener(this);

        // 현재 회차의 당첨결과
        tvTime = findViewById(R.id.tvTime);
        tvDate = findViewById(R.id.tvDate);
        balls[0] = findViewById(R.id.ball_1);
        balls[1] = findViewById(R.id.ball_2);
        balls[2] = findViewById(R.id.ball_3);
        balls[3] = findViewById(R.id.ball_4);
        balls[4] = findViewById(R.id.ball_5);
        balls[5] = findViewById(R.id.ball_6);
        balls[6] = findViewById(R.id.ball_bonus);
        getLastLottoNumber(getCurrentRound());

        // 광고배너
        AdView adView = (AdView)findViewById(R.id.adView);
        if (!BuildConfig.DEBUG) {
            adView.setAdUnitId("진짜 광고 ID");
        }
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void getLastLottoNumber(int drwNo) {
        // UI 작업을 위해 runOnUiThread 사용
        runOnUiThread(() -> {
            RetrofitRepository.getINSTANCE().getLottoRoundData(Integer.toString(drwNo), new RetrofitRepository.ResponseListener<LottoData>() {
                @Override
                public void onSuccessResponse(LottoData lottoData) {
                    try {
                        if (lottoData.getReturnValue().equals("fail")) {
                            if (drwNo > 1)
                                getLastLottoNumber(drwNo - 1);
                            return;
                        }

                        // 회차
                        tvTime.setText(drwNo + "회");
                        String[] ydm = lottoData.getDrwNoDate().split("-");
                        // 추첨일
                        tvDate.setText(String.format("(%s년 %s월 %s일 추첨)", ydm[0], ydm[1], ydm[2]));

                        // 당첨번호 + 보너스
                        for (int i = 0; i < 7; i++) {
                            balls[i].setText(lottoData.getNumber(i+1));
                        }

                        for (Button btn: balls) {
                            int num = Integer.parseInt(btn.getText().toString());
                            if (num <= 10) {
                                btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.ball_1)));
                            } else if (num <= 20) {
                                btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.ball_2)));
                            } else if (num <= 30) {
                                btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.ball_3)));
                            } else if (num <= 40) {
                                btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.ball_4)));
                            } else {
                                btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.ball_5)));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailResponse() { }
            });
        });
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

    @Override
    public void onClick(View v) {
        Intent mIntent;

        switch (v.getId()) {
            case R.id.btnLotto:
                mIntent = new Intent(getApplicationContext(), RandomLottoActivity.class);
                startActivity(mIntent);
                break;
            case R.id.btnLottoHistory:
                mIntent = new Intent(getApplicationContext(), LottoHistoryActivity.class);

                mIntent.putExtra("currentRound", getCurrentRound());
                // 역대 로또 정보 화면으로 전환
                startActivity(mIntent);
                break;
            case R.id.btnMapView:
                // DaumMapEngineApi.so는 arm 및 armv7 기기만 지원
                Boolean isAvailable = false;
                for (String abi: Build.SUPPORTED_ABIS) {
                    if (abi.contains("arm")) {
                        isAvailable = true;
                        break;
                    }
                }

                // 지원하는 기기이면
                if (isAvailable) {
                    mIntent = new Intent(getApplicationContext(), MapViewActivity.class);
                    // 주변 지도보기 화면으로 전환
                    startActivity(mIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "현재 기기에서는 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnQRCode:
                mIntent = new Intent(getApplicationContext(), QRScanActivity.class);
                // QR Code Scan 화면으로 전환
                startActivity(mIntent);
                break;
            case R.id.btnVerion:
                PackageInfo pi = null;
                try {
                    pi = getPackageManager().getPackageInfo(getPackageName(), 0);
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setTitle("현재 버전");
                    alert.setMessage("V" + pi.versionName);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
                    alert.show();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btnQnA:
            case R.id.btnRate:
                Toast.makeText(getApplicationContext(), "미구현 기능입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnMyNumber:
                mIntent = new Intent(getApplicationContext(), LottoHistoryActivity.class);
                startActivity(mIntent);
                break;
        }
    }
}