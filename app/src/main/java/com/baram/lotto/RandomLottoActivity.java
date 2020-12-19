package com.baram.lotto;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baram.lotto.model.LottoData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TreeSet;

public class RandomLottoActivity extends AppCompatActivity {
    Button btnRandom, btnExBtn;
    LinearLayout ll, ll3;
    ArrayList<Bitmap> lottoBalls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_lotto);

        //로또볼 이미지들을 bitmap객체로 담을 리스트 생성
        lottoBalls = new ArrayList<>();

        //drawable 안에 이미지를 가져오기 위해서 resource 객체 가져오기
        Resources res = getResources();

        for(int i = 0; i < 45; i++){

            //파일명으로 로또볼 이미지를 찾아서 resourceId 값으로 변환
            int tmpId = getResources().getIdentifier(
                    "lottoball" + (i+1) , "drawable", this.getPackageName());

            //Bitmap 객체 생성
            Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, tmpId)
                    ,130,130, false);

            //리스트에 Bitmap 객체 추가
            lottoBalls.add(bitmap);
        }

        ll = findViewById(R.id.ll);
        ll3 = findViewById(R.id.ll3);


        final LinearLayout.LayoutParams param = new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , LinearLayout.LayoutParams.WRAP_CONTENT);
        final LinearLayout.LayoutParams param1 = new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , LinearLayout.LayoutParams.WRAP_CONTENT);

        ll.setLayoutParams(param);
        ll3.setLayoutParams(param1);

        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll3.setOrientation(LinearLayout.HORIZONTAL);

        btnRandom = findViewById(R.id.btnRandom);
        btnExBtn = findViewById(R.id.btnExceptRandom);

        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //addView로 뷰를 추가하기 때문에 이전에 있던 뷰들을 모두 삭제 (재실행시)
                if(ll.getChildCount() > 0) ll.removeAllViews();

                //중복 제거, 오름차순 정렬을 위해서 TreeSet 사용
                TreeSet<Integer> set = new TreeSet();

                //TreeSet의 사이즈가 6이 될때까지 실행
                while(set.size() < 6){
                    //0~44의 숫자를 생성(리스트의 순서도 0부터 시작하기 때문에 0~44로 설정 했습니다.)
                    int random = new Random().nextInt(45);

                    //TreeSet에 랜덤으로 만들어진 숫자 추가
                    set.add(random);
                }


                //TreeSet의 사이즈 만큼 TreeSet안의 값을 불러옵니다.
                for(Integer i:set){

                    //ImageView를 생성
                    ImageView lottoBallView = new ImageView(ll.getContext());

                    //ImageView의 Bitmap 세팅 (TreeSet에 추가 됐던 랜덤 숫자)
                    lottoBallView.setImageBitmap(lottoBalls.get(i));

                    //레이아웃에 ImageView 추가
                    ll.addView(lottoBallView);
                }

            }
        });

        btnExBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //addView로 뷰를 추가하기 때문에 이전에 있던 뷰들을 모두 삭제 (재실행시)
                if(ll3.getChildCount() > 0) ll3.removeAllViews();

                getLastLottoNumber(getCurrentRound());

            }
        });

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

                            //중복 제거, 오름차순 정렬을 위해서 TreeSet 사용
                            TreeSet<Integer> set = new TreeSet();

                            //TreeSet의 사이즈가 6이 될때까지 실행
                            while(set.size() < 6){
                                //0~44의 숫자를 생성(리스트의 순서도 0부터 시작하기 때문에 0~44로 설정 했습니다.)
                                int random = new Random().nextInt(45);

                                if (    random != Integer.parseInt(lottoData.getDrwtNo1()) ||
                                        random != Integer.parseInt(lottoData.getDrwtNo2()) ||
                                        random != Integer.parseInt(lottoData.getDrwtNo3()) ||
                                        random != Integer.parseInt(lottoData.getDrwtNo4()) ||
                                        random !=  Integer.parseInt(lottoData.getDrwtNo5()) ||
                                        random !=  Integer.parseInt(lottoData.getDrwtNo6()) ||
                                        random !=  Integer.parseInt(lottoData.getBnusNo())){
                                    set.add(random);
                                }

                                //TreeSet에 랜덤으로 만들어진 숫자 추가
                                //set.add(random);
                            }

                            //TreeSet의 사이즈 만큼 TreeSet안의 값을 불러옵니다.
                            for(Integer i:set){

                                //ImageView를 생성
                                ImageView lottoBallView = new ImageView(ll3.getContext());

                                //ImageView의 Bitmap 세팅 (TreeSet에 추가 됐던 랜덤 숫자)
                                lottoBallView.setImageBitmap(lottoBalls.get(i));

                                //레이아웃에 ImageView 추가
                                ll3.addView(lottoBallView);
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