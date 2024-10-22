package org.forgerock.pebblebank;

import static org.forgerock.android.auth.Action.START_AUTHENTICATE;

import android.net.Uri;

import androidx.annotation.NonNull;

import org.forgerock.android.auth.Action;
import org.forgerock.android.auth.FRRequestInterceptor;
import org.forgerock.android.auth.Request;

public class ForceAuthRequestInterceptor implements FRRequestInterceptor<Action> {

    @NonNull
    @Override
    public Request intercept(@NonNull Request request, Action tag) {
        if (tag.getType().equals(START_AUTHENTICATE)) {
            return request.newBuilder()
                    .url(Uri.parse(request.url().toString())
                            .buildUpon()
                            .appendQueryParameter("ForceAuth", "true").toString())
                    .build();
        }
        return request;
    }
}
