package com.baram.lotto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class LottoHistoryActivity extends AppCompatActivity {

    Button btnLottoHistoryView;

    ArrayList<String> items;
    ArrayAdapter<String> adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lotto_history);

        // 역대 로또 정보 버튼
        items = new ArrayList<String>();

        // 어댑터 생성
        adapter = new ArrayAdapter<String>(LottoHistoryActivity.this, android.R.layout.simple_list_item_1, items);

        // 어댑터 설정
        listView = (ListView) findViewById(R.id.LottoHistoryList);
        listView.setAdapter(adapter);
        //listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE); // 하나의 항목만 선택할 수 있도록 설정

        btnLottoHistoryView = findViewById(R.id.btnLottoHistoryView);
        btnLottoHistoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)  {   //여기서 클릭 시 행동을 결정

                new Thread(){
                    @Override
                    public void run(){
                        Document doc = null;
                        try
                        {
                            String LottoBaseURL = "https://www.dhlottery.co.kr/gameResult.do?method=byWin&drwNo=";      //회차별 당첨번호 조회 URL
                            int Round = 1;                                                                              //URL 뒤에 회차 숫자를 붙이는 걸로 페이지 이동
                            String LottoRoundURL = LottoBaseURL + Integer.toString(Round);                              //URL 생성
                            doc = Jsoup.connect(LottoRoundURL).get();

                            Elements eRound = doc.select("#dwrNoList");
                            String TotalRound = eRound.text();

                            for(int i = 0; i < Integer.parseInt(TotalRound); i++)
                            {
                                Round = i;                                                                              //URL 뒤에 회차 숫자를 붙이는 걸로 페이지 이동
                                LottoRoundURL = LottoBaseURL + Integer.toString(Round);
                                doc = Jsoup.connect(LottoRoundURL).get();
                            }
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }.start();

                String text = "test";        // EditText에 입력된 문자열값을 얻기
                items.add(text);                          // items 리스트에 입력된 문자열 추가
                items.add("Sunday");
                items.add("Monday");
                items.add("Tuesday");
                items.add("Wednesday");
                items.add("Thursday");
                items.add("Friday");
                items.add("Saturday");
                adapter.notifyDataSetChanged();           // 리스트 목록 갱신
            }
        });
    }
}