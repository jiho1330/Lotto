package com.baram.lotto;

import androidx.annotation.NonNull;
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

import com.baram.lotto.Interface.RetrofitService;
import com.baram.lotto.model.Location;
import com.baram.lotto.model.LottoData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.os.SystemClock.sleep;

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

                for(int i = 900; i <= 910; i++)
                {
                    callLottoRoundData(i);
                }


                /*
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(RetrofitService.LOTTO_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                RetrofitService LottoService = retrofit.create(RetrofitService.class);

                LottoService.getLotto("getLottoNumber","1").enqueue(new Callback() {
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response)
                    {
                        if(response.isSuccessful())
                        {
                            LottoData body = (LottoData) response.body();
                            if(body != null)
                            {
                                items.add("추첨일 : " + body.getDrwNoDate());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                        items.add("error");
                    }
                });

                adapter.notifyDataSetChanged();
                 */

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