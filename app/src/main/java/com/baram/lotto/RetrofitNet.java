package com.baram.lotto;

import com.baram.lotto.Interface.AddrSearchService;

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

    public AddrSearchService getSearchAddrService(){
        Retrofit kakaoRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(AddrSearchService.BASE_URL)
                .build();

        return kakaoRetrofit.create(AddrSearchService.class);
    }

}
