package com.baram.lotto;

import android.content.Context;
import android.util.Log;

import com.baram.lotto.model.LottoData;

public class PreferenceLottoData {
    private Context mContext;
    private static PreferenceLottoData INSTANCE;

    private String LastRound = "-1";

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


    public static PreferenceLottoData getPreferenceLottoData(Context context){
        if(INSTANCE == null){
            INSTANCE = new PreferenceLottoData(context);
        }
        return INSTANCE;
    }

    private PreferenceLottoData(Context context){
        mContext = context;
        LastRound = PreferenceManager.getString(mContext, LOTTO_DATA_LAST_ROUND);
    }

    public void updateLottoRoundData()
    {
        int nLastRound;
        int Round;
        if(LastRound == "")
            nLastRound = -1;
        else
            nLastRound = Integer.parseInt(LastRound);

        if(nLastRound == -1)
            Round = 1;
        else
            Round = nLastRound + 1;

        String Key = LOTTO_DATA_KEY + Round;

        RetrofitRepository.getINSTANCE().getLottoRoundData(Integer.toString(Round), new RetrofitRepository.ResponseListener<LottoData>() {
            @Override
            public void onSuccessResponse(LottoData lottoData) {
                if(!lottoData.getReturnValue().equals("fail"))
                {
                    LastRound = Integer.toString(Round);
                    PreferenceManager.setString(mContext, LOTTO_DATA_LAST_ROUND,                 LastRound);     //로또 당첨 일시
                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_DATE,            lottoData.getDrwNoDate());     //로또 당첨 일시
                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_TOTALSELLAMNT,   lottoData.getTotSellamnt());     //누적 상금
                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_FIRSTWINAMNT,    lottoData.getFirstWinamnt());     //1등 당첨금
                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_FIRSTPRZWNERCO,  lottoData.getFirstPrzwnerCo());     //1등 당첨 인원
                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_DRWTNO_1,        lottoData.getDrwtNo1());     //로또 번호 1
                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_DRWTNO_2,        lottoData.getDrwtNo2());     //로또 번호 2
                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_DRWTNO_3,        lottoData.getDrwtNo3());     //로또 번호 3
                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_DRWTNO_4,        lottoData.getDrwtNo4());     //로또 번호 4
                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_DRWTNO_5,        lottoData.getDrwtNo5());     //로또 번호 5
                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_DRWTNO_6,        lottoData.getDrwtNo6());     //로또 번호 6
                    PreferenceManager.setString(mContext, Key + LOTTO_DATA_BNUSNO,          lottoData.getBnusNo());     //보너스 번호
                    Log.i("updateLottoRoundData", "Success Round : " + Round);
                    updateLottoRoundData();
                }
                else
                    Log.i("updateLottoRoundData", "Fail Round : " + Round);
            }

            @Override
            public void onFailResponse() {
                //showToastMessage("데이터를 가져오지 못했습니다.", Toast.LENGTH_SHORT);
                //Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.fail_result), Toast.LENGTH_SHORT).show();
            }
        });
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
        LottoData lottoData = new LottoData();

        lottoData.setDrwNoDate(PreferenceManager.getString(mContext, Key + LOTTO_DATA_DATE));
        lottoData.setTotSellamnt(PreferenceManager.getString(mContext, Key + LOTTO_DATA_TOTALSELLAMNT));
        lottoData.setFirstWinamnt(PreferenceManager.getString(mContext, Key + LOTTO_DATA_FIRSTWINAMNT));
        lottoData.setFirstPrzwnerCo(PreferenceManager.getString(mContext, Key + LOTTO_DATA_FIRSTPRZWNERCO));
        lottoData.setDrwtNo1(PreferenceManager.getString(mContext, Key + LOTTO_DATA_DRWTNO_1));
        lottoData.setDrwtNo2(PreferenceManager.getString(mContext, Key + LOTTO_DATA_DRWTNO_2));
        lottoData.setDrwtNo3(PreferenceManager.getString(mContext, Key + LOTTO_DATA_DRWTNO_3));
        lottoData.setDrwtNo4(PreferenceManager.getString(mContext, Key + LOTTO_DATA_DRWTNO_4));
        lottoData.setDrwtNo5(PreferenceManager.getString(mContext, Key + LOTTO_DATA_DRWTNO_5));
        lottoData.setDrwtNo6(PreferenceManager.getString(mContext, Key + LOTTO_DATA_DRWTNO_6));
        lottoData.setBnusNo(PreferenceManager.getString(mContext, Key + LOTTO_DATA_BNUSNO));

        return lottoData;
    }

    public int getLottoLastRound()
    {
        return Integer.parseInt(LastRound);
    }
}
