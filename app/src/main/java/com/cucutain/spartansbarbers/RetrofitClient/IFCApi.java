package com.cucutain.spartansbarbers.RetrofitClient;

import com.cucutain.spartansbarbers.Models.FCMResponse;
import com.cucutain.spartansbarbers.Models.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


//put key firebase
public interface IFCApi {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=keyfirebase"

            })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);
}
