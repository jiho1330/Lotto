package com.baram.lotto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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
    //ArrayAdapter<String> adapter;
    CustumListViewAdapterLotto CustumAdapter;
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
        //Toast.makeText(getApplicationContext(), "데이터 동기화...", Toast.LENGTH_SHORT).show();
        //new Thread(()-> updateData()).start();

        // 역대 로또 정보 버튼
        items = new ArrayList<String>();

        // 어댑터 생성
        CustumAdapter = new CustumListViewAdapterLotto();
        //adapter = new ArrayAdapter<String>(LottoHistoryActivity.this, android.R.layout.simple_list_item_1, items);

        // 어댑터 설정
        listView = (ListView) findViewById(R.id.LottoHistoryList);
        listView.setAdapter(CustumAdapter);
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
                PreferenceLottoData.getPreferenceLottoData(LottoHistoryActivity.this).updateWithProgress(LottoHistoryActivity.this, currentRound);
                //updateData();
            }
        });

        btnLottoHistoryDelete = findViewById(R.id.btnLottoHistoryDelete);
        btnLottoHistoryDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //여기서 클릭 시 행동을 결정
                //PreferenceLottoData.getPreferenceLottoData(LottoHistoryActivity.this).deleteLottoRoundData();
                items.clear();  // 리스트를 비움
                CustumAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "리스트가 초기화 되었습니다.", Toast.LENGTH_SHORT).show();

                // 데이터 로드
                loadMoreData();
                listView.smoothScrollToPosition(0);
            }
        });

        // 역대로또정보 로드
        loadMoreData();
    }
    
    // 최신 데이터 업데이트
    private void updateData() {
        PreferenceLottoData.getPreferenceLottoData(LottoHistoryActivity.this).updateLottoData(currentRound);
    }
    
    // 역대로또정보 로드
    private void loadMoreData() {
        Gson gson = new Gson();
        LottoData lottoData;
        int nextRound;
        String text;

        // 다음 회차
        nextRound = currentRound - items.size();

        // 마지막 정보까지 추가가 되었으면
        if (nextRound <= 0) {
            Toast.makeText(getApplicationContext(), "마지막 정보입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = nextRound; i > nextRound - 20 && i > 0; i--) {

            lottoData = PreferenceLottoData.getPreferenceLottoData(LottoHistoryActivity.this).getLottoRoundData(i);
            /*
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
            */
            CustumAdapter.addItem(
                    lottoData.getDrwNo(),
                    GetLottoBall(Integer.parseInt(lottoData.getDrwtNo1())),
                    GetLottoBall(Integer.parseInt(lottoData.getDrwtNo2())),
                    GetLottoBall(Integer.parseInt(lottoData.getDrwtNo3())),
                    GetLottoBall(Integer.parseInt(lottoData.getDrwtNo4())),
                    GetLottoBall(Integer.parseInt(lottoData.getDrwtNo5())),
                    GetLottoBall(Integer.parseInt(lottoData.getDrwtNo6())),
                    GetLottoBall(Integer.parseInt(lottoData.getBnusNo()))
            );
        }
        // UI 적용
        CustumAdapter.notifyDataSetChanged();
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
                    CustumAdapter.notifyDataSetChanged();
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

    private Drawable GetLottoBall(int Number)
    {
        Drawable NumberImg = null;

        switch (Number)
        {
            case 1: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball1); break;
            case 2: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball2); break;
            case 3: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball3); break;
            case 4: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball4); break;
            case 5: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball5); break;
            case 6: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball6); break;
            case 7: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball7); break;
            case 8: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball8); break;
            case 9: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball9); break;
            case 10: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball10); break;
            case 11: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball11); break;
            case 12: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball12); break;
            case 13: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball13); break;
            case 14: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball14); break;
            case 15: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball15); break;
            case 16: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball16); break;
            case 17: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball17); break;
            case 18: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball18); break;
            case 19: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball19); break;
            case 20: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball20); break;
            case 21: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball21); break;
            case 22: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball22); break;
            case 23: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball23); break;
            case 24: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball24); break;
            case 25: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball25); break;
            case 26: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball26); break;
            case 27: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball27); break;
            case 28: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball28); break;
            case 29: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball29); break;
            case 30: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball30); break;
            case 31: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball31); break;
            case 32: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball32); break;
            case 33: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball33); break;
            case 34: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball34); break;
            case 35: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball35); break;
            case 36: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball36); break;
            case 37: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball37); break;
            case 38: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball38); break;
            case 39: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball39); break;
            case 40: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball40); break;
            case 41: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball41); break;
            case 42: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball42); break;
            case 43: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball43); break;
            case 44: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball44); break;
            case 45: NumberImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lottoball45); break;
        }
        return NumberImg;
    }
}