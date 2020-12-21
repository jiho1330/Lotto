package com.baram.lotto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.baram.lotto.model.LottoData;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LottoHistoryActivity extends AppCompatActivity {

    Button btnLottoHistoryView;
    Button btnLottoHistoryUpdate;
    Button btnLottoHistoryDelete;

    ArrayList<String> items;
    ArrayAdapter<String> adapter;
    ListView listView;
    int currentRound;   // 현재 회차
    boolean lastitemVisibleFlag = false;    // 리스트뷰의 마지막 아이템 여부

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lotto_history);

        // 이전 Activity에서 넘겨 받은 값을 가져옴 : 현재 회차
        Intent mIntent = getIntent();
        currentRound = mIntent.getIntExtra("currentRound", -1);

        // 역대로또정보 불러오기
        Toast.makeText(getApplicationContext(), "데이터 업데이트 중...", Toast.LENGTH_SHORT).show();
        new Thread(()-> updateData()).start();

        // 역대 로또 정보 버튼
        items = new ArrayList<String>();

        // 어댑터 생성
        adapter = new ArrayAdapter<String>(LottoHistoryActivity.this, android.R.layout.simple_list_item_1, items);

        // 어댑터 설정
        listView = (ListView) findViewById(R.id.LottoHistoryList);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 리스트뷰의 가장 마지막 아이템까지 스크롤을 내리면 아래조건 만족
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastitemVisibleFlag) {
                    // 데이터 로드
                    loadMoreData();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 아이템이 없으면
                if (totalItemCount == 0) {
                    loadMoreData();
                }
                // 마지막 아이템이 보이는지 여부
                lastitemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }
        });
        //listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE); // 하나의 항목만 선택할 수 있도록 설정

        //getData();

        btnLottoHistoryUpdate = findViewById(R.id.btnLottoHistoryUpdate);
        btnLottoHistoryUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)  {   //여기서 클릭 시 행동을 결정
                updateData();
            }
        });

        btnLottoHistoryDelete = findViewById(R.id.btnLottoHistoryDelete);
        btnLottoHistoryDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //여기서 클릭 시 행동을 결정
                PreferenceLottoData.getPreferenceLottoData(LottoHistoryActivity.this).deleteLottoRoundData();
                items.clear();  // 리스트를 비움
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "리스트가 초기화 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 역대로또정보 로드
        loadMoreData();
    }
    
    // 최신 데이터 업데이트
    private void updateData() {
        PreferenceLottoData.getPreferenceLottoData(LottoHistoryActivity.this).updateLottoRoundData(currentRound);
    }
    
    // 역대로또정보 로드
    private void loadMoreData() {
        Gson gson = new Gson();
        LottoData lottoData;
        int nextRound;
        String text;

        if (currentRound != -1)
        {
            // 다음 회차
            nextRound = currentRound - items.size();

            // 마지막 정보까지 추가가 되었으면
            if (nextRound <= 0) {
                Toast.makeText(getApplicationContext(), "마지막 정보입니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            for (int i = nextRound; i > nextRound - 20 && i > 0; i--) {
                String mJson = PreferenceManager.getString(getApplicationContext(), PreferenceLottoData.LOTTO_DATA_KEY + (i));
                if (mJson.equals("")) {
                    continue;
                }

                lottoData = gson.fromJson(mJson, LottoData.class);

                text = String.format("%s회 [%s] [%s] [%s] [%s] [%s] [%s] 보너스 [%s]",
                        lottoData.getDrwNo(),
                        lottoData.getDrwtNo1(),
                        lottoData.getDrwtNo2(),
                        lottoData.getDrwtNo3(),
                        lottoData.getDrwtNo4(),
                        lottoData.getDrwtNo5(),
                        lottoData.getDrwtNo6(),
                        lottoData.getBnusNo());

                items.add(text);
            }

            adapter.notifyDataSetChanged();

        }
        else
        {
            Toast.makeText(getApplicationContext(), "Data Update가 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void callLottoRoundData(int Round) {
        RetrofitRepository.getINSTANCE().getLottoRoundData(Integer.toString(Round), new RetrofitRepository.ResponseListener<LottoData>() {
            @Override
            public void onSuccessResponse(LottoData lottoData) {
                try {
                    items.add(Round + "회 당첨번호 : " + lottoData.getDrwtNo1()
                            + " " + lottoData.getDrwtNo2()
                            + " " + lottoData.getDrwtNo3()
                            + " " + lottoData.getDrwtNo4()
                            + " " + lottoData.getDrwtNo5()
                            + " " + lottoData.getDrwtNo6()
                            + " 보너스 " + lottoData.getBnusNo()
                    );
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailResponse() {
                //showToastMessage("데이터를 가져오지 못했습니다.", Toast.LENGTH_SHORT);
                //Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.fail_result), Toast.LENGTH_SHORT).show();
            }
        });
    }
}