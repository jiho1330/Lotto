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
    private static Progress_Task progress_task;
    private JSONArray jsonArray;

    private int lastRound;
    private int Progress;
    private Gson gson = new Gson();

    //Key 포맷 LottoData_Round숫자_항목
    public static final String LOTTO_DATA_KEY           = "LottoData_Round";

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
                //progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);      // Style - 원 모양 설정
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);      // Style - 수평
                progressDialog.setMax(lastRound);
                progressDialog.setMessage("데이터 동기화...");                         // Message - 표시할 텍스트
                progressDialog.setCanceledOnTouchOutside(false);                    // 터치시 Canceled 막기
                progressDialog.show();  // UI 표시
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

        }

        @Override
        // 백그라운드 작업 시작, UI 조작 불가, onPreExcute() 종료후 바로 호출
        protected Void doInBackground(Integer... ints) {
            try {
                // UI Update, publishProgress() - onProgressUpdate 호출
                for (int i = 1; i <= jsonArray.length(); i++) {
                    publishProgress(i);
                    updateFromJson(i);
                }

                for (int i = jsonArray.length() + 1; i <= lastRound; i++) {
                    publishProgress(i);
                    updateFromApi(i);
                    Thread.sleep(70);
                }

                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        // UI 조작가능 (UI Thread에서 실행)
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage(String.format("데이터 동기화...[%d/%d]", values[0], lastRound));
            progressDialog.setProgress(values[0]);
        }

        @Override
        // UI Thread에서 실행, doInBackground 종료 후 바로 호출
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (progressDialog != null) {
                progressDialog.dismiss();       // ProgressDialog 지우기
                Toast.makeText(mContext, "동기화 완료", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static PreferenceLottoData getPreferenceLottoData(Context context) {
        if(INSTANCE == null){
            INSTANCE = new PreferenceLottoData(context);
        }
        return INSTANCE;
    }

    private PreferenceLottoData(Context context) {
        try {
            mContext = context;
            if (progress_task == null) {
                progress_task = new Progress_Task();
            }

            InputStream is = mContext.getAssets().open("lottodata.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            // 읽어온 json file의 buffer 값을 String 형식으로 바꿈
            String json = new String(buffer, "UTF-8");

            // json 문자열을 JsonObject로 변환
            JSONObject jsonObject = new JSONObject(json);

            // JsonObject의 "data" 값을 JsonArray 형식으로 가져옴
            jsonArray = jsonObject.getJSONArray("data");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }

    public void updateWithProgress(Context context, int currentRound)
    {
        mContext = context;
        this.lastRound = currentRound;
        progress_task = new Progress_Task();
        progress_task.execute();
    }

    // Json 파일로 데이터 업데이트
    public void updateFromJson(int round) {

        try {
            String text = jsonArray.getString(round - 1);

            if (!text.equals(""))
                PreferenceManager.setString(mContext, LOTTO_DATA_KEY + round, text);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // Api 호출로 데이터 업데이트
    public void updateFromApi(int round) {
        try {
            // 회차정보가 존재하면 종료
            if (!PreferenceManager.getString(mContext, LOTTO_DATA_KEY + round).equals("")) {
                return;
            }

            RetrofitRepository.getINSTANCE().getLottoRoundData(Integer.toString(round), new RetrofitRepository.ResponseListener<LottoData>() {
                @Override
                public void onSuccessResponse(LottoData lottoData) {
                    if (lottoData.getReturnValue().equals("success")) {
                        String key = LOTTO_DATA_KEY + round;
                        // Json String 형식으로 저장
                        PreferenceManager.setString(mContext, key, gson.toJson(lottoData));
                    }
                }

                @Override
                public void onFailResponse() {
                    //Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.fail_result), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateLottoData(int currentRound)
    {
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
            jsonArray = jsonObject.getJSONArray("data");

            String text;
            Progress = 0;
            for (int i = 0; i < jsonArray.length(); i++) {
                Progress++;
                text = jsonArray.getString(i);

                if (!text.equals(""))
                    PreferenceManager.setString(mContext, LOTTO_DATA_KEY + (i + 1), text);
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

    }

    public LottoData getLottoRoundData(int Round)
    {
        // 라운드로 Key 생성
        String Key = LOTTO_DATA_KEY + Round;

        // Preference에서 로또정보 가져오기
        String mJson = PreferenceManager.getString(mContext, Key);
        LottoData lottoData = gson.fromJson(mJson, LottoData.class);

        return lottoData;
    }
}
