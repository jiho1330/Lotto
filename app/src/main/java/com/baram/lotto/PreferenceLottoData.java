package com.baram.lotto;

import android.content.Context;
import com.baram.lotto.model.LottoData;

public class PreferenceLottoData {
    private Context mContext;
    private static PreferenceLottoData INSTANCE;

    public static PreferenceLottoData getPreferenceLottoData(){
        if(INSTANCE == null){
            INSTANCE = new PreferenceLottoData();
        }
        return INSTANCE;
    }

    private PreferenceLottoData(){
    }

    public void setLottoRoundData(Context context, int Round, LottoData lottoData)
    {
        //라운드로 Key 생성
        //Key 포맷 LottoData_Round숫자_항목
    }
}
