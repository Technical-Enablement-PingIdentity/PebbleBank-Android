/*
 * Copyright (c) 2020 ForgeRock. All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package org.forgerock.pebblebank.controller;

import androidx.annotation.NonNull;

import org.forgerock.android.auth.Action;
import org.forgerock.android.auth.FRRequestInterceptor;
import org.forgerock.android.auth.PolicyAdvice;
import org.forgerock.android.auth.Request;
import org.forgerock.android.auth.interceptor.AdviceHandler;
import org.forgerock.android.auth.ui.AdviceDialogHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Response;

public class AuthorizationPolicyInterceptor extends IdentityGatewayAdviceInterceptor implements FRRequestInterceptor<Action> {

    @NonNull
    @Override
    public Request intercept(@NonNull Request request, Action tag) {
        return request;
    }

 /*   public Response interceptResponse (@NotNull Chain chain) throws IOException {
        return super.intercept(chain);
    }*/

    @NonNull
    @Override
    public AdviceHandler getAdviceHandler(PolicyAdvice advice) {
        //Pre-build handler to handle Advice, e.g A Dialog to trigger the tree
        return new AdviceDialogHandler();
    }
}
