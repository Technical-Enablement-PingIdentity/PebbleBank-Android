/*
 * Copyright (c) 2019 ForgeRock. All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 *
 */

package org.forgerock.pebblebank.ui.overview;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.forgerock.android.auth.AccessToken;
import org.forgerock.android.auth.FRListener;
import org.forgerock.android.auth.FRUser;
import org.forgerock.android.auth.UserInfo;
import org.forgerock.android.auth.exception.AuthenticationRequiredException;
import org.json.JSONException;
import org.json.JSONObject;

public class OverviewViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    private String accessToken = "";

    public OverviewViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void getUserInfo() {
        try {
            AccessToken accessTokenObj = FRUser.getCurrentUser().getAccessToken();
            accessToken = accessTokenObj.getValue();
        } catch (AuthenticationRequiredException e) {
        }
        FRUser.getCurrentUser().getUserInfo(new FRListener<UserInfo>() {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void onSuccess(UserInfo result) {
                try {
                    JSONObject obj = result.getRaw().append("accessToken", accessToken);
                    mText.postValue(obj.toString(4));
                } catch (JSONException e) {
                    //ignore
                }
            }

            @Override
            public void onException(Exception e) {
                System.out.println(e);
            }
        });
    }
}