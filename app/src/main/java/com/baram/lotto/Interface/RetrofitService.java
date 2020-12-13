package com.baram.lotto.Interface;

import com.baram.lotto.model.Location;
import com.baram.lotto.model.LottoData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface RetrofitService {

    public static final String KAKAO_BASE_URL = "https://dapi.kakao.com/";
    public static final String KAKAO_AK = "423214fe1e2a24324c4f040f69149298";

    public static final String LOTTO_BASE_URL = "https://www.dhlottery.co.kr"; //"https://www.nlotto.co.kr";

    // Kakao Map Api - search 호출
    @GET("v2/local/search/keyword.json")
    Call<Location> searchAddressList(@Query("query") String query, @Query("page") int page
            , @Query("x") double x, @Query("y") double y, @Query("radius") int radius, @Header("Authorization") String apikey);

    // Lotto Api
    @GET("/common.do")
    Call<LottoData> getLotto(@Query("method") String method, @Query("drwNo") String drwNum);
}