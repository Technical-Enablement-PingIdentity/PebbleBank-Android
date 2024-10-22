package org.forgerock.pebblebank.api;

import org.forgerock.pebblebank.domain.PushRegistry;
import org.forgerock.pebblebank.domain.Transaction;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PushApi {

    @POST("push")
    Call<PushRegistry> register(@Body PushRegistry registrationId);

}
