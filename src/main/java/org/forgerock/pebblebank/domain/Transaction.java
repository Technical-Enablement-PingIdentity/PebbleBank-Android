package org.forgerock.pebblebank.domain;

import com.google.gson.annotations.SerializedName;

public class Transaction {

    @SerializedName("payload")
    private String payload;

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
