package com.baram.lotto;

import android.util.Log;

import com.baram.lotto.Interface.RetrofitService;
import com.baram.lotto.model.Location;
import com.baram.lotto.model.LottoData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RetrofitRepository {

    private static RetrofitRepository INSTANCE;

    public static RetrofitRepository getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new RetrofitRepository();
        }
        return INSTANCE;
    }

    // 응답 리스너 (T: 데이터 타입)
    public interface ResponseListener<T> {
        void onSuccessResponse(T data);
        void onFailResponse();
    }

    // Kakao Map Api 주변 판매점 조회
    public void getAddressList(int page, double x, double y, int radius, ResponseListener<Location> listener) {
        Call<Location> call = RetrofitNet.getRetrofit().getSearchAddrService()
                .searchAddressList("복권", page, x, y, radius, "KakaoAK " + RetrofitService.KAKAO_AK);
        call.enqueue(new Callback<Location>() {
            @Override
            public void onResponse(Call<Location> call, Response<Location> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        for (int i = 0; i < response.body().documentsList.size(); i++) {
                            Log.i("AddrSearchRepository", "[GET] getAddressList : " + response.body().documentsList.get(i).getAddress_name());
                        }
                        listener.onSuccessResponse(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<Location> call, Throwable t) {
                listener.onFailResponse();
            }
        });
    }

    // Lotto Api 조회
    public void getLottoRoundData(String drwNum, ResponseListener<LottoData> listener) {
        Call<LottoData> call = RetrofitNet.getRetrofit().getLottoData().getLotto("getLottoNumber", drwNum);
        call.enqueue(new Callback<LottoData>() {
            @Override
            public void onResponse(Call<LottoData> call, Response<LottoData> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        LottoData body = response.body();
                        if (body != null) {
                            listener.onSuccessResponse(response.body());
                        }
                    }
                }
                response = null;
            }

            @Override
            public void onFailure(Call<LottoData> call, Throwable t) {
                listener.onFailResponse();
            }
        });
    }

}
