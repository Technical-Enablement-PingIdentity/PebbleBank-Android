package org.forgerock.pebblebank.api;

import org.forgerock.pebblebank.domain.Transaction;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TransactionApi {

    @GET("transaction/{id}")
    Call<Transaction> getTransaction(@Path("id") String id);

}
