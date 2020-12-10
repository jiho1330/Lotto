package com.baram.lotto;

import android.util.Log;

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

    public void getAddressList(String query, int page, int size, AddressResponseListener listener) {
        if (query != null) {
            Call<Location> call = RetrofitNet.getRetrofit().getSearchAddrService().searchAddressList(query, page, size, "KakaoAK "+ "423214fe1e2a24324c4f040f69149298");
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
    }

    public interface AddressResponseListener{
        void onSuccessResponse(Location locationData);
        void onFailResponse();
    }

}
