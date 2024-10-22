/*
 * Copyright (c) 2019 ForgeRock. All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 *
 */

package org.forgerock.pebblebank.ui.transfer;

import static org.forgerock.android.auth.Action.START_AUTHENTICATE;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import org.forgerock.android.auth.Action;
import org.forgerock.android.auth.FRListener;
import org.forgerock.android.auth.FRRequestInterceptor;
import org.forgerock.android.auth.RequestInterceptorRegistry;

import org.forgerock.pebblebank.R;
import org.forgerock.pebblebank.controller.AuthorizationPolicyInterceptor;

public class GalleryFragment extends Fragment implements FRListener<Void>  {

    private GalleryViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RequestInterceptorRegistry.getInstance().register(
                (FRRequestInterceptor<Action>) (request, tag) -> {
                    if (tag != null && tag.getType().equals(START_AUTHENTICATE) &&
                            tag.getPayload().optString("tree", "").equals("moneyTransfer-mobileApp")) {
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
        Intent theIntent = this.getActivity().getIntent();
        theIntent.putExtra(LoginFragment.TREE_NAME, "moneyTransfer-mobileApp");
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_transfer, container, false);
        //final TextView textView = root.findViewById(R.id.text_gallery);
        /*galleryViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }

    @Override
    public void onSuccess(Void result) {
        try {
            Navigation.findNavController(this.getView()).navigate(R.id.nav_home);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onException(@NonNull Exception e) {
        try {
            Navigation.findNavController(this.getView()).navigate(R.id.nav_home);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}