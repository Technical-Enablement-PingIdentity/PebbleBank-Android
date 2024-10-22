/*
 * Copyright (c) 2019 ForgeRock. All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 *
 */

package org.forgerock.pebblebank.ui.biometric;

import static org.forgerock.android.auth.Action.START_AUTHENTICATE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.forgerock.android.auth.Account;
import org.forgerock.android.auth.Action;
import org.forgerock.android.auth.FRAListener;
import org.forgerock.android.auth.FRListener;
import org.forgerock.android.auth.FRRequestInterceptor;
import org.forgerock.android.auth.FRSession;
import org.forgerock.android.auth.FRUser;
import org.forgerock.android.auth.FRUserKeys;
import org.forgerock.android.auth.Mechanism;
import org.forgerock.android.auth.Node;
import org.forgerock.android.auth.NodeListener;
import org.forgerock.android.auth.Request;
import org.forgerock.android.auth.RequestInterceptorRegistry;
import org.forgerock.android.auth.UserInfo;
import org.forgerock.android.auth.callback.DeviceBindingAuthenticationType;
import org.forgerock.android.auth.callback.DeviceBindingCallback;
import org.forgerock.android.auth.callback.DeviceSigningVerifierCallback;
import org.forgerock.android.auth.callback.HiddenValueCallback;
import org.forgerock.android.auth.callback.PollingWaitCallback;
import org.forgerock.android.auth.devicebind.ApplicationPinDeviceAuthenticator;
import org.forgerock.android.auth.devicebind.Prompt;
import org.forgerock.android.auth.exception.DuplicateMechanismException;
import org.forgerock.pebblebank.controller.AuthenticatorModel;
import org.forgerock.pebblebank.controller.AuthorizationPolicyInterceptor;
import org.forgerock.pebblebank.domain.PushRegistry;
import org.forgerock.pebblebank.service.PushService;
import org.forgerock.android.auth.devicebind.UserKey;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BiometricViewModel extends ViewModel {

    //private MutableLiveData<Boolean> result;
    private MutableLiveData<BiometricStatusModel> result;
    private Context context;

    public BiometricViewModel() {
        result = new MutableLiveData<>();
    }

    //public LiveData<Boolean> getResult() {
    //    return result;
    //}

    public LiveData<BiometricStatusModel> getResult() { return result; }

    public void enable(Context context) {
        RequestInterceptorRegistry.getInstance().register(
                (FRRequestInterceptor<Action>) (request, tag) -> {
                    if (tag != null && tag.getType().equals(START_AUTHENTICATE) &&
                            tag.getPayload().optString("tree", "").equals("deviceBind-1")) {
                        return request.newBuilder()
                                .url(Uri.parse(request.url().toString())
                                        .buildUpon()
                                        .appendQueryParameter("ForceAuth", "true").toString())
                                .build();
                    }
                    return request;
                });
        /*RequestInterceptorRegistry.getInstance().register(
                new AuthorizationPolicyInterceptor()
        );*/
        FRSession.authenticate(context, "deviceBind-1", new NodeListener<FRSession>() {

            @Override
            public void onCallbackReceived(Node node) {
                if (node.getCallback(HiddenValueCallback.class) != null) {
                    HiddenValueCallback callback = node.getCallback(HiddenValueCallback.class);
                    String id = callback.getId();
                    if (id.equals("mfaDeviceRegistration")) {
                        String url = callback.getValue();
                        NodeListener<FRSession> nodeListener = this;
                        AuthenticatorModel.getInstance(context).createMechanismFromUri(url, new FRAListener<Mechanism>() {
                            @Override
                            public void onSuccess(final Mechanism mechanism) {
                                AuthenticatorModel.getInstance(context).notifyDataChanged();
                                node.next(context, nodeListener);
                            }

                            @Override
                            public void onException(final Exception exception) {
                                if (exception instanceof DuplicateMechanismException) {
                                    Mechanism m = ((DuplicateMechanismException)exception).getCausingMechanism();
                                    AuthenticatorModel.getInstance(context).removeMechanism(m);
                                }
                                node.next(context, nodeListener);
                            }
                        });
                    }
                }
                if (node.getCallback(DeviceBindingCallback.class) != null) {
                    DeviceBindingCallback callback = node.getCallback(DeviceBindingCallback.class);
                    NodeListener<FRSession> nodeListener = this;
                    callback.bind(context, deviceBindingAuthenticationType -> {
                        switch (deviceBindingAuthenticationType) {
                            case APPLICATION_PIN: {
                                ApplicationPinDeviceAuthenticator authenticator = new ApplicationPinDeviceAuthenticator();
                                authenticator.prompt(new Prompt("Enter Application PIN", "Cryptography device binding", "Please complete with application pin"));
                                return authenticator;
                            }
                            default:
                                return callback.getDeviceAuthenticator(deviceBindingAuthenticationType);
                        }
                    }, new FRListener<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            node.next(context, nodeListener);
                        }

                        @Override
                        public void onException(Exception e) {
                            //Let the tree make decision
                            node.next(context, nodeListener);
                        }
                    });
                }
            }

            @Override
            public void onSuccess(FRSession session) {
                //result.postValue(TRUE);
                result.postValue(new BiometricStatusModel("enable", TRUE));
 /*               PushService pushService = new PushService();
                pushService.register(new Callback<PushRegistry>() {
                    @Override
                    public void onResponse(Call<PushRegistry> call, Response<PushRegistry> response) {
                        result.postValue(TRUE);
                    }

                    @Override
                    public void onFailure(Call<PushRegistry> call, Throwable t) {
                        result.postValue(FALSE);
                    }
                });*/
            }

            @Override
            public void onException(Exception e) {

                //result.postValue(FALSE);
                result.postValue(new BiometricStatusModel("enable", FALSE));
            }
        });
    }

    public void disable(Context context) {
        RequestInterceptorRegistry.getInstance().register(
                (FRRequestInterceptor<Action>) (request, tag) -> {
                    if (tag != null && tag.getType().equals(START_AUTHENTICATE) &&
                            tag.getPayload().optString("tree", "").equals("unregisterPush")) {
                        return request.newBuilder()
                                .url(Uri.parse(request.url().toString())
                                        .buildUpon()
                                        .appendQueryParameter("ForceAuth", "true").toString())
                                .build();
                    }
                    return request;
                });
        /*RequestInterceptorRegistry.getInstance().register(
                new AuthorizationPolicyInterceptor()
        );*/
        FRSession.authenticate(context, "unregisterPush", new NodeListener<FRSession>() {

            @Override
            public void onCallbackReceived(Node node) {
                if (node.getCallback(DeviceSigningVerifierCallback.class) != null) {
                    DeviceSigningVerifierCallback callback = node.getCallback(DeviceSigningVerifierCallback.class);
                    NodeListener<FRSession> nodeListener = this;
                    callback.sign(context, new FRListener<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            node.next(context, nodeListener);
                        }

                        @Override
                        public void onException(Exception e) {
                            //Let the tree make decision
                            node.next(context, nodeListener);
                        }
                    });
                }
            }

            @Override
            public void onSuccess(FRSession session) {
                FRUser.getCurrentUser().getUserInfo(new FRListener<UserInfo>() {
                    @Override
                    public void onSuccess(UserInfo userinfo) {
                        String currUserID = userinfo.getSub();
                        FRUserKeys keys = new FRUserKeys(context);
                        List<UserKey> userKeyList = keys.loadAll();
                        String theUserID;
                        if (userKeyList.size() > 0) {
                            for (UserKey key : userKeyList) {
                                if (key.getUserId().indexOf("id=" + currUserID + ",") == 0) {
                                    keys.delete(key, true, new FRListener<Void>() {
                                        @Override
                                        public void onException(@NonNull Exception e) {
                                            result.postValue(new BiometricStatusModel("disable", FALSE));
                                        }

                                        @Override
                                        public void onSuccess(Void result1) {
                                            String currUserName = key.getUserName();
                                            List<Account> accountList = AuthenticatorModel.getInstance(context).getAllAccounts();
                                            for (Account account : accountList) {
                                                if (account.getAccountName().equals(currUserName)) {
                                                    AuthenticatorModel.getInstance(context).removeAccount(account);
                                                }
                                            }
                                            result.postValue(new BiometricStatusModel("disable", TRUE));
                                        }
                                    });
                                }
                            }
                        } else {
                            result.postValue(new BiometricStatusModel("disable", TRUE));
                        }
                    }

                    @Override
                    public void onException(Exception e) {
                        result.postValue(new BiometricStatusModel("disable", FALSE));
                    }
                });
            }

            @Override
            public void onException(Exception e) {
                result.postValue(new BiometricStatusModel("disable", FALSE));
            }
        });
    }
}