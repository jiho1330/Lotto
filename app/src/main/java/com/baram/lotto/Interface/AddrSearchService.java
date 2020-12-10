package com.baram.lotto.Interface;

import com.baram.lotto.Location;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface AddrSearchService {

    public static final String BASE_URL = "https://dapi.kakao.com/";

    @GET("v2/local/search/keyword.json")
    Call<Location> searchAddressList(@Query("query") String query, @Query("page") int page, @Query("size") int size, @Header("Authorization") String apikey);

}
