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
    LinearLayout ll;
    ArrayList<Bitmap> lottoBalls;
    Button bt, btnMapView, btnLottoHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //로또볼 이미지들을 bitmap객체로 담을 리스트 생성
        lottoBalls = new ArrayList<>();

        //drawable 안에 이미지를 가져오기 위해서 resource 객체 가져오기
        Resources res = getResources();

        for(int i = 0; i < 19; i++){

            //파일명으로 로또볼 이미지를 찾아서 resourceId 값으로 변환
            int tmpId = getResources().getIdentifier(
                    "lottoball" + (i+1) , "drawable", this.getPackageName());

            //Bitmap 객체 생성
            Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, tmpId)
                    ,70,70, false);

            //리스트에 Bitmap 객체 추가
            lottoBalls.add(bitmap);
        }

        ll = findViewById(R.id.ll);

        final LinearLayout.LayoutParams param = new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , LinearLayout.LayoutParams.WRAP_CONTENT);

        ll.setLayoutParams(param);

        ll.setOrientation(LinearLayout.HORIZONTAL);


        bt = findViewById(R.id.bt);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //addView로 뷰를 추가하기 때문에 이전에 있던 뷰들을 모두 삭제 (재실행시)
                if(ll.getChildCount() > 0) ll.removeAllViews();

                //중복 제거, 오름차순 정렬을 위해서 TreeSet 사용
                TreeSet<Integer> set = new TreeSet();

                //TreeSet의 사이즈가 6이 될때까지 실행
                while(set.size() < 6){
                    //0~44의 숫자를 생성(리스트의 순서도 0부터 시작하기 때문에 0~44로 설정 했습니다.)
                    int random = new Random().nextInt(19);

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