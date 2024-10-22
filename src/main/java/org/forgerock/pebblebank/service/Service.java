package org.forgerock.pebblebank.service;

import org.forgerock.android.auth.interceptor.AccessTokenInterceptor;
import org.forgerock.pebblebank.api.PushApi;
import org.forgerock.pebblebank.api.TransactionApi;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Service {

    private static final Retrofit retrofit;

    static {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new AccessTokenInterceptor()).build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.151:9001/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    protected TransactionApi getTransactionApi() {
        return retrofit.create(TransactionApi.class);
    }

    protected PushApi getPushApi() {
        return retrofit.create(PushApi.class);
    }


}
