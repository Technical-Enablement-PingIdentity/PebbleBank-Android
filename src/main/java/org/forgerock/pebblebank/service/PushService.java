package org.forgerock.pebblebank.service;

import com.google.firebase.messaging.FirebaseMessaging;

import org.forgerock.pebblebank.domain.PushRegistry;

import retrofit2.Callback;

public class PushService extends Service {

    public PushService() {
    }

    public void register(Callback<PushRegistry> callback) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        PushRegistry pushRegistry = new PushRegistry();
                        pushRegistry.setRegistrationId(task.getResult());
                        getPushApi().register(pushRegistry).enqueue(callback);
                    } else {
                        callback.onFailure(null, task.getException());
                    }
                });
    }

}
