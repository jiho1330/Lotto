package com.baram.lotto;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baram.lotto.model.LottoData;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Calendar;
import java.util.GregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    Button btnLotto, btnMapView, btnLottoHistory;
    ImageButton btnQRCode;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private TextView tvTime, tvDate;
    private Button[] balls = new Button[7];

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

                mIntent.putExtra("currentRound", getCurrentRound());
                // 역대 로또 정보 화면으로 전환
                startActivity(mIntent);
            }
        });

        // QR코드 스캔 버튼
        btnQRCode = findViewById(R.id.btnQRCode);
        btnQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), QRScanActivity.class);
                // QR Code Scan 화면으로 전환
                startActivity(mIntent);
            }
        });

        // 광고배너
        AdView adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

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
    }

    private void getLastLottoNumber(int drwNo) {
        // UI 작업을 위해 runOnUiThread 사용
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RetrofitRepository.getINSTANCE().getLottoRoundData(Integer.toString(drwNo), new RetrofitRepository.ResponseListener<LottoData>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                            balls[0].setText(lottoData.getDrwtNo1());
                            balls[1].setText(lottoData.getDrwtNo2());
                            balls[2].setText(lottoData.getDrwtNo3());
                            balls[3].setText(lottoData.getDrwtNo4());
                            balls[4].setText(lottoData.getDrwtNo5());
                            balls[5].setText(lottoData.getDrwtNo6());
                            balls[6].setText(lottoData.getBnusNo());

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
                    public void onFailResponse() {
                        //Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.fail_result), Toast.LENGTH_SHORT).show();
                    }
                });
            }
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
}