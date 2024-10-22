package org.forgerock.pebblebank.service;

import org.forgerock.pebblebank.domain.Transaction;

import retrofit2.Callback;

public class TransactionService extends Service {

    public void getTransaction(String id, Callback<Transaction> callback) {
        getTransactionApi().getTransaction(id).enqueue(callback);
    }
}
