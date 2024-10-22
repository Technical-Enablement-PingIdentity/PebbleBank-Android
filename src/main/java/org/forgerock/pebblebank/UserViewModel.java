/*
 * Copyright (c) 2019 ForgeRock. All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 *
 */

package org.forgerock.pebblebank;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.forgerock.android.auth.FRListener;
import org.forgerock.android.auth.FRUser;
import org.forgerock.android.auth.UserInfo;
import org.json.JSONException;

public class UserViewModel extends ViewModel {

    private MutableLiveData<UserInfo> userInfo = new MutableLiveData<>();

    public UserViewModel() {
    }

    public LiveData<UserInfo> getUserInfo() {
        FRUser.getCurrentUser().getUserInfo(new FRListener<UserInfo>() {
            @Override
            public void onSuccess(UserInfo result) {
                userInfo.postValue(result);
            }

            @Override
            public void onException(Exception e) {
                System.out.println(e);
            }
        });
        return userInfo;
    }
}