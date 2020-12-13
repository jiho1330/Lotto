package com.baram.lotto;

import com.baram.lotto.Interface.RetrofitService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitNet {

    private static RetrofitNet INSTANCE;

    public static RetrofitNet getRetrofit() {
        if (INSTANCE == null) {
            INSTANCE = new RetrofitNet();
        }
        return INSTANCE;
    }

    private RetrofitNet() {
    }

    // Kakao Map Api 서비스
    public RetrofitService getSearchAddrService(){
        Retrofit kakaoRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(RetrofitService.KAKAO_BASE_URL)
                .build();

        return kakaoRetrofit.create(RetrofitService.class);
    }

    // Lotto Api
    public  RetrofitService getLottoData(){
        Retrofit LottoRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(RetrofitService.LOTTO_BASE_URL)
                .build();

        return LottoRetrofit.create(RetrofitService.class);
    }
}
