package com.baram.lotto;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.baram.lotto.model.LottoData;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import retrofit2.converter.gson.GsonConverterFactory;

public class PreferenceLottoData {
    private Context mContext;
    private static PreferenceLottoData INSTANCE;

    private String LastRound = "-1";
    private int Progress;

    //Key 포맷 LottoData_Round숫자_항목
    public static final String LOTTO_DATA_KEY           = "LottoData_Round";
    public static final String LOTTO_DATA_LAST_ROUND    = "LottoData_Last_Round";
    public static final String LOTTO_DATA_DATE          = "_Date";
    public static final String LOTTO_DATA_TOTALSELLAMNT = "_TotalSellamnt";
    public static final String LOTTO_DATA_FIRSTWINAMNT  = "_FirstWinamnt";
    public static final String LOTTO_DATA_FIRSTPRZWNERCO= "_FirstPrzwnerCo";
    public static final String LOTTO_DATA_DRWTNO_1      = "_DRWTNO_1";
    public static final String LOTTO_DATA_DRWTNO_2      = "_DRWTNO_2";
    public static final String LOTTO_DATA_DRWTNO_3      = "_DRWTNO_3";
    public static final String LOTTO_DATA_DRWTNO_4      = "_DRWTNO_4";
    public static final String LOTTO_DATA_DRWTNO_5      = "_DRWTNO_5";
    public static final String LOTTO_DATA_DRWTNO_6      = "_DRWTNO_6";
    public static final String LOTTO_DATA_BNUSNO        = "_BnusNo";

    class Progress_Task extends AsyncTask<Integer, Integer, Void> {
        private ProgressDialog progressDialog = null;       // 원형 ProgressBar 생성
        public Progress_Task() { super(); }

        @Override
        // doInBackground 전에 실행(UI Thread) - 백그라운드 작업 전 초기화 부분
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                // ProgressDialog 생성, 레이아웃 변경
                progressDialog = new ProgressDialog(mContext, android.R.style.Theme_Material_Dialog_Alert);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);      // Style - 원 모양 설정
                progressDialog.setMessage("Now Update...");                         // Message - 표시할 텍스트
                progressDialog.setCanceledOnTouchOutside(false);                    // 터치시 Canceled 막기
                progressDialog.show();  // UI 표시
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        // 백그라운드 작업 시작, UI 조작 불가, onPreExcute() 종료후 바로 호출
        protected Void doInBackground(Integer... ints) {
            for (int i = 0; i < 4; i++) {
                try {
                    // UI Update, publishProgress() - onProgressUpdate 호출
                    //publishProgress(ints[0]);
                    progressDialog.setMessage(String.format("Now Update...[%d/%d]", ints[0], ints[1]));
                    //Thread.sleep(500);                  // 0.5초 간격 UI Update
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        // UI 조작가능 (UI Thread에서 실행)
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //progressBar.incrementProgressBy(values[0]);
            //progress_value.setText(progressBar.getProgress()+"%");
        }

        @Override
        // UI Thread에서 실행, doInBackground 종료 후 바로 호출
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (progressDialog != null)
                progressDialog.dismiss();       // ProgressDialog 지우기
            //progressBar.setProgress(20);
            //progress_value.setText(progressBar.getProgress()+"%");
        }
    }

    Progress_Task progress_task;

    public static PreferenceLottoData getPreferenceLottoData(Context context){
        if(INSTANCE == null){
            INSTANCE = new PreferenceLottoData(context);
        }
        return INSTANCE;
    }

    private PreferenceLottoData(Context context){
        mContext = context;
        LastRound = PreferenceManager.getString(mContext, LOTTO_DATA_LAST_ROUND);
        progress_task = new Progress_Task();
    }

    public void updateLottoRoundDataProgress(int currentRound)
    {
        progress_task.onPreExecute();
        updateLottoRoundData(currentRound);
    }

    public void updateLottoRoundData(int currentRound)
    {
        Gson gson = new Gson();

        LastRound = String.valueOf(currentRound);
        PreferenceManager.setString(mContext, LOTTO_DATA_LAST_ROUND, LastRound);     // 마지막 회차

        // lottodata.json 파일에서 최신회차 읽어오기
        try {

            InputStream is = mContext.getAssets().open("lottodata.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            // 읽어온 json file의 buffer 값을 String 형식으로 바꿈
            String json = new String(buffer, "UTF-8");

            // json 문자열을 JsonObject로 변환
            JSONObject jsonObject = new JSONObject(json);

            // JsonObject의 "data" 값을 JsonArray 형식으로 가져옴
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            String text;
            Progress = 0;
            for (int i = 0; i < jsonArray.length(); i++) {
                Progress++;
                text = jsonArray.getString(i);

                if (!text.equals(""))
                    PreferenceManager.setString(mContext, LOTTO_DATA_KEY + (i + 1), text);
            }

            // 전부 불러오면 로딩 종료
            if (Progress == currentRound) {
                Toast.makeText(mContext, "최신 데이터를 불러왔습니다.", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        // lottodata.json 파일에 없는 최신 데이터를 api 호출을 통해 가져옴
        int startIdx = Progress + 1;
        new Thread(() -> {
            for (int i = startIdx; i <= currentRound; i++) {
                Progress++;
                try {

                    // 회차정보가 존재하면 반복문 스킵
                    if (!PreferenceManager.getString(mContext, LOTTO_DATA_KEY + i).equals("")) {
                        continue;
                    }

                    RetrofitRepository.getINSTANCE().getLottoRoundData(Integer.toString(i), new RetrofitRepository.ResponseListener<LottoData>() {
                        @Override
                        public void onSuccessResponse(LottoData lottoData) {
                            if (lottoData.getReturnValue().equals("success")) {
                                String key = LOTTO_DATA_KEY + lottoData.getDrwNo();
                                // Json String 형식으로 저장
                                PreferenceManager.setString(mContext, key, gson.toJson(lottoData));
                            }

                            // 전부 불러오면 로딩 종료
                            if (Progress == currentRound) {
                                Toast.makeText(mContext, "최신 데이터를 불러왔습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailResponse() {
                            //Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.fail_result), Toast.LENGTH_SHORT).show();
                        }
                    });
                    Thread.sleep(100);   // 대기시간이 짧으면 오류 발생할 수 있음.

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

//        int nLastRound;
//        int Round;
//        if(LastRound == "")
//            nLastRound = -1;
//        else
//            nLastRound = Integer.parseInt(LastRound);
//
//        if(nLastRound == -1)
//            Round = 1;
//        else
//            Round = nLastRound + 1;
//
//        String Key = LOTTO_DATA_KEY + Round;
        //progress_task.doInBackground(Round);
//        RetrofitRepository.getINSTANCE().getLottoRoundData(Integer.toString(Round), new RetrofitRepository.ResponseListener<LottoData>() {
//            @Override
//            public void onSuccessResponse(LottoData lottoData) {
//                if(!lottoData.getReturnValue().equals("fail"))
//                {
//                    LastRound = Integer.toString(Round);
//
//                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_LAST_ROUND,                 LastRound);     //로또 당첨 일시
//                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_DATE,            lottoData.getDrwNoDate());     //로또 당첨 일시
//                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_TOTALSELLAMNT,   lottoData.getTotSellamnt());     //누적 상금
//                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_FIRSTWINAMNT,    lottoData.getFirstWinamnt());     //1등 당첨금
//                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_FIRSTPRZWNERCO,  lottoData.getFirstPrzwnerCo());     //1등 당첨 인원
//                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_DRWTNO_1,        lottoData.getDrwtNo1());     //로또 번호 1
//                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_DRWTNO_2,        lottoData.getDrwtNo2());     //로또 번호 2
//                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_DRWTNO_3,        lottoData.getDrwtNo3());     //로또 번호 3
//                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_DRWTNO_4,        lottoData.getDrwtNo4());     //로또 번호 4
//                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_DRWTNO_5,        lottoData.getDrwtNo5());     //로또 번호 5
//                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_DRWTNO_6,        lottoData.getDrwtNo6());     //로또 번호 6
//                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_BNUSNO,          lottoData.getBnusNo());     //보너스 번호
//                    Log.i("updateLottoRoundData", "Success Round : " + Round);
//                    updateLottoRoundData(-1);
//                }
//                else {
//                    Log.i("updateLottoRoundData", "Finish Round : " + Round);
//                    Void result = null;
//                    progress_task.onPostExecute(result);
//
//                }
//            }
//
//            @Override
//            public void onFailResponse() {
//                //showToastMessage("데이터를 가져오지 못했습니다.", Toast.LENGTH_SHORT);
//                //Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.fail_result), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    public void deleteLottoRoundData()
    {
        PreferenceManager.clear(mContext);
        Log.i("deleteLottoRoundData", "Delete Round Data");
        LastRound = "-1";
    }

    public LottoData getLottoRoundData(int Round)
    {
        //라운드로 Key 생성
        //Key 포맷 LottoData_Round숫자_항목
        String Key = LOTTO_DATA_KEY + Round;
        LottoData lottoData;

        Gson gson = new Gson();
        String json = PreferenceManager.getString(mContext, Key);
        lottoData = gson.fromJson(json, LottoData.class);

//        lottoData.setDrwNo(String.valueOf(Round));
//        lottoData.setDrwNoDate(PreferenceManager.getString(mContext, Key + LOTTO_DATA_DATE));
//        lottoData.setTotSellamnt(PreferenceManager.getString(mContext, Key + LOTTO_DATA_TOTALSELLAMNT));
//        lottoData.setFirstWinamnt(PreferenceManager.getString(mContext, Key + LOTTO_DATA_FIRSTWINAMNT));
//        lottoData.setFirstPrzwnerCo(PreferenceManager.getString(mContext, Key + LOTTO_DATA_FIRSTPRZWNERCO));
//        lottoData.setDrwtNo1(PreferenceManager.getString(mContext, Key + LOTTO_DATA_DRWTNO_1));
//        lottoData.setDrwtNo2(PreferenceManager.getString(mContext, Key + LOTTO_DATA_DRWTNO_2));
//        lottoData.setDrwtNo3(PreferenceManager.getString(mContext, Key + LOTTO_DATA_DRWTNO_3));
//        lottoData.setDrwtNo4(PreferenceManager.getString(mContext, Key + LOTTO_DATA_DRWTNO_4));
//        lottoData.setDrwtNo5(PreferenceManager.getString(mContext, Key + LOTTO_DATA_DRWTNO_5));
//        lottoData.setDrwtNo6(PreferenceManager.getString(mContext, Key + LOTTO_DATA_DRWTNO_6));
//        lottoData.setBnusNo(PreferenceManager.getString(mContext, Key + LOTTO_DATA_BNUSNO));

        return lottoData;
    }

    public int getLottoLastRound()
    {
        if(LastRound.equals(""))
            return -1;
        else
            return Integer.parseInt(LastRound);
    }
}
