/*
 * Copyright (c) 2019 ForgeRock. All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 *
 */

package org.forgerock.pebblebank.ui.logout;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.forgerock.android.auth.FRUser;
import org.forgerock.pebblebank.MainActivity;

public class LogoutFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FRUser.getCurrentUser() != null) {
            FRUser.getCurrentUser().logout();
        }
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }
}