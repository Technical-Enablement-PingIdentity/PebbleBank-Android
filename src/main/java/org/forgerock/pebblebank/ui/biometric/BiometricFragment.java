/*
 * Copyright (c) 2019 ForgeRock. All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 *
 */

package org.forgerock.pebblebank.ui.biometric;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.forgerock.android.auth.Account;
import org.forgerock.android.auth.FRListener;
import org.forgerock.android.auth.FRUser;
import org.forgerock.android.auth.FRUserKeys;
import org.forgerock.android.auth.UserInfo;
import org.forgerock.android.auth.callback.DeviceBindingAuthenticationType;
import org.forgerock.android.auth.devicebind.UserKey;
import org.forgerock.pebblebank.R;
import org.forgerock.pebblebank.controller.AuthenticatorModel;

import java.util.List;

public class BiometricFragment extends Fragment {

    private BiometricViewModel biometricViewModel;

    private String authMethod = "Biometric";
    //private String authMethod = "mPIN";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        biometricViewModel =
                ViewModelProviders.of(this).get(BiometricViewModel.class);
        View root = inflater.inflate(R.layout.fragment_share, container, false);
        final SwitchCompat biometricSwitch = root.findViewById(R.id.biometricSwitch);
        FRUserKeys keys = new FRUserKeys(getContext());
        List<UserKey> userKeyList = keys.loadAll();
        setBiometricSwitch(biometricSwitch, keys, userKeyList);
        biometricViewModel.getResult().observe(getViewLifecycleOwner(), new Observer<BiometricStatusModel>() {
            @Override
            public void onChanged(BiometricStatusModel biometricStatusModel) {
                Boolean s = biometricStatusModel.getSuccess();
                if (biometricStatusModel.getStatus() == "enable") {
                    if (s != null && s) {
                        Toast.makeText(getContext(), authMethod + " enrolled", Toast.LENGTH_LONG).show();
                    } else {
                        biometricSwitch.setOnCheckedChangeListener(null);
                        setBiometricSwitch(biometricSwitch, keys, userKeyList);
                        Toast.makeText(getContext(), "Failed to enroll " + authMethod, Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (s != null && s) {
                        Toast.makeText(getContext(), authMethod + " disabled", Toast.LENGTH_LONG).show();
                    } else {
                        biometricSwitch.setOnCheckedChangeListener(null);
                        setBiometricSwitch(biometricSwitch, keys, userKeyList);
                        Toast.makeText(getContext(), "Failed to disable " + authMethod, Toast.LENGTH_LONG).show();
                    }
                }
            }

            /*@Override
            public void onChanged(@Nullable Boolean s) {
                if (s != null && s) {
                    Toast.makeText(getContext(), "Biometric enrolled", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Failed to enroll Biometric", Toast.LENGTH_LONG).show();
                }
            }*/
        });

        return root;
    }

    private void setBiometricSwitch(SwitchCompat biometricSwitch, FRUserKeys keys, List<UserKey> userKeyList) {
        biometricSwitch.setClickable(false);
        FRUser.getCurrentUser().getUserInfo(new FRListener<UserInfo>() {
            @Override
            public void onException(@NonNull Exception e) {
                biometricSwitch.setChecked(true);
                biometricSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        biometricViewModel.enable(getContext());
                    } else {
                        biometricViewModel.disable(getContext());
                    }
                });
                biometricSwitch.setClickable(true);
            }

            @Override
            public void onSuccess(UserInfo userinfo) {
                String currUserID = userinfo.getSub();
                boolean isExist = false;
                if (userKeyList.size() > 0) {
                    for (UserKey key : userKeyList) {
                        if (key.getUserId().indexOf("id=" + currUserID + ",") == 0 && key.getAuthType() != DeviceBindingAuthenticationType.NONE) {
                            isExist = true;
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    biometricSwitch.setChecked(true);
                                    biometricSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                        if (isChecked) {
                                            biometricViewModel.enable(getContext());
                                        } else {
                                            biometricViewModel.disable(getContext());
                                        }
                                    });
                                    biometricSwitch.setClickable(true);

                                }
                            });
                            break;
                        }
                    }
                }
                if (!isExist) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            biometricSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                if (isChecked) {
                                    boolean dExist = false;
                                    for (UserKey key : userKeyList) {
                                        if (key.getUserId().indexOf("id=" + currUserID + ",") == 0 && key.getAuthType() == DeviceBindingAuthenticationType.NONE) {
                                            dExist = true;
                                            keys.delete(key, true, new FRListener<Void>() {
                                                @Override
                                                public void onException(@NonNull Exception e) {
                                                    biometricViewModel.enable(getContext());
                                                }

                                                @Override
                                                public void onSuccess(Void result1) {
                                                    biometricViewModel.enable(getContext());
                                                }
                                            });
                                        }
                                    }
                                    if (!dExist) {
                                        biometricViewModel.enable(getContext());
                                    }
                                } else {
                                    biometricViewModel.disable(getContext());
                                }
                            });
                            biometricSwitch.setClickable(true);
                        }
                    });
                }
            }
        });
    }
}