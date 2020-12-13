package com.baram.lotto;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

        //getData();

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

                            Elements eRound = doc.select("#dwrNoList");                                        //
                            String TotalRound = eRound.text();                                                          //드롭박스의 모든 Text를 가져옴(내림차순)
                            String Rounds[] = TotalRound.split(" ");                                             // " "으로 Split
                            int MaxRound = Integer.parseInt(Rounds[0]);                                                 //내림차순이기때문에 가장 처음 값이 최대값

                            String WinNumbers = "";

                            for(int i = 1; i <= 3/*MaxRound*/; i++)
                            {
                                Round = i;                                                                              //URL 뒤에 회차 숫자를 붙이는 걸로 페이지 이동
                                LottoRoundURL = LottoBaseURL + Integer.toString(Round);
                                doc = Jsoup.connect(LottoRoundURL).get();

                                eRound = doc.select(".nums");                                        //
                                WinNumbers = eRound.text();
                                WinNumbers = i + "회차 : " + WinNumbers;
                                items.add(WinNumbers);
                            }
                            //adapter.notifyDataSetChanged();           // 리스트 목록 갱신

                            Toast myToast = Toast.makeText(LottoHistoryActivity.this,WinNumbers, Toast.LENGTH_SHORT);
                            myToast.show();
                        }
                        catch (IOException e)
                        {
                            Toast myToast = Toast.makeText(LottoHistoryActivity.this,"Error", Toast.LENGTH_SHORT);
                            myToast.show();
                            e.printStackTrace();
                        }
                    }
                }.start();

                /*
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
                */
            }
        });
    }

    private void getData()
    {
        LottoJsoup jsoupAsyncTask = new LottoJsoup();
        jsoupAsyncTask.execute();
    }

    private class LottoJsoup extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids)
        {
            try
            {
                Document doc = null;
                String LottoBaseURL = "https://www.dhlottery.co.kr/gameResult.do?method=byWin&drwNo=";      //회차별 당첨번호 조회 URL
                int Round = 1;                                                                              //URL 뒤에 회차 숫자를 붙이는 걸로 페이지 이동
                String LottoRoundURL = LottoBaseURL + Integer.toString(Round);                              //URL 생성
                doc = Jsoup.connect(LottoRoundURL).get();

                //리스트 만들기
                String WinNumbers;

                Document RunDoc = null;
                //현재 회차정보 가져오기
                Elements eRound = doc.select("#dwrNoList");                                        //
                String TotalRound = eRound.text();                                                          //드롭박스의 모든 Text를 가져옴(내림차순)
                String Rounds[] = TotalRound.split(" ");                                             // " "으로 Split
                int MaxRound = Integer.parseInt(Rounds[0]);                                                 //내림차순이기때문에 가장 처음 값이 최대값

                for(int i = MaxRound; i >= MaxRound - 10/*MaxRound*/; --i) {
                    Round = i;                                                                              //URL 뒤에 회차 숫자를 붙이는 걸로 페이지 이동
                    LottoRoundURL = LottoBaseURL + Integer.toString(Round);
                    doc = Jsoup.connect(LottoRoundURL).get();

                    eRound = doc.select(".nums");                                        //
                    WinNumbers = eRound.text();
                    WinNumbers = i + "회차 : " + WinNumbers;
                    items.add(WinNumbers);
                }

                adapter.notifyDataSetChanged();           // 리스트 목록 갱신
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return null;
        }
    }
}