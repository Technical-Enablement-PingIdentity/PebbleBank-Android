/*
 * Copyright (c) 2019 ForgeRock. All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 *
 */

package org.forgerock.pebblebank.ui.overview;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import org.forgerock.pebblebank.R;

public class OverviewFragment extends Fragment {

    private OverviewViewModel overviewViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        overviewViewModel =
                ViewModelProviders.of(this).get(OverviewViewModel.class);
        TextView textView = root.findViewById(R.id.userTextView);
        overviewViewModel.getText().observe(this, textView::setText);
        Handler handler = new Handler();
        handler.postDelayed(() -> overviewViewModel.getUserInfo(), 100);
        return root;
    }
}