package com.baram.lotto;

import android.util.Log;

import com.baram.lotto.Interface.AddrSearchService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddrSearchRepository {

    private static AddrSearchRepository INSTANCE;

    public static AddrSearchRepository getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new AddrSearchRepository();
        }
        return INSTANCE;
    }

    public void getAddressList(int page, double x, double y, AddressResponseListener listener) {
        Call<Location> call = RetrofitNet.getRetrofit().getSearchAddrService()
                .searchAddressList("복권", page, x, y, 2000, "KakaoAK " + AddrSearchService.KAKAO_AK);
        call.enqueue(new Callback<Location>() {
            @Override
            public void onResponse(Call<Location> call, Response<Location> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        for (int i = 0; i < response.body().documentsList.size(); i++) {
                            Log.i("MJ_DEBUG", "[GET] getAddressList : " + response.body().documentsList.get(i).getAddress_name());
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

    public interface AddressResponseListener{
        void onSuccessResponse(Location locationData);
        void onFailResponse();
    }

}
