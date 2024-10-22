/*
 * Copyright (c) 2019 ForgeRock. All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 *
 */

package org.forgerock.pebblebank.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.os.OperationCanceledException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;

import org.forgerock.android.auth.AuthService;
import org.forgerock.android.auth.FRAuth;
import org.forgerock.android.auth.FRListener;
import org.forgerock.android.auth.FROptions;
import org.forgerock.android.auth.FROptionsBuilder;
import org.forgerock.android.auth.FRSession;
import org.forgerock.android.auth.FRUser;
import org.forgerock.android.auth.Logger;
import org.forgerock.android.auth.OkHttpClientProvider;
import org.forgerock.android.auth.PolicyAdvice;
import org.forgerock.android.auth.SecureCookieJar;
import org.forgerock.android.auth.interceptor.AdviceHandler;
import org.forgerock.android.auth.interceptor.IdentityGatewayAdviceInterceptor;
import org.forgerock.android.auth.ui.AdviceDialogHandler;
import org.forgerock.android.auth.ui.RegisterFragment;
import org.forgerock.pebblebank.HomeActivity;
import org.forgerock.pebblebank.R;
import org.jetbrains.annotations.NotNull;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;
import static androidx.core.graphics.drawable.DrawableKt.toBitmap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment implements FRListener<Void> {

    private HomeViewModel mViewModel;
    private View view;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment, container, false);
        CardView signInCardView = view.findViewById(R.id.signIn);
        LinearLayout registerLayout = view.findViewById(R.id.registerLayout);
        //LinearLayout registerContainer = view.findViewById(R.id.registerContainer);
        Button registerButton = view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBrowser();
            }
        });
        /*registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerLayout.setVisibility(GONE);
                registerContainer.setVisibility(VISIBLE);
                signInCardView.setVisibility(GONE);
                RegisterFragment registerFragment = (RegisterFragment) getChildFragmentManager().findFragmentById(R.id.registerFragment);
                if (registerFragment != null) {
                    registerFragment.start();
                }
            }
        });*/

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
    }

    @Override
    public void onSuccess(Void result) {

/*            OkHttpClient.Builder builder = new OkHttpClient.Builder().followRedirects(false);
            //Pre-build interceptor to handle response format from Identity Gateway
            builder.addInterceptor(new IdentityGatewayAdviceInterceptor() {
                @Override
                public AdviceHandler getAdviceHandler(PolicyAdvice advice) {
                    //Pre-build handler to handle Advice, e.g A Dialog to trigger the tree
                    return new AdviceDialogHandler();
                }
            });
            SecureCookieJar secureCookieJar = SecureCookieJar.builder().context(getContext()).build();
            builder.cookieJar(secureCookieJar);

            OkHttpClient client = builder.build();
            //Identity Gateway proxy the request
            Request request = new Request.Builder().url("https://xf-75-forgeops.encore.forgerock.org/ig/riskeval")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    //Handle Failure Scenario
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Intent homeIntent = new Intent(getContext(), HomeActivity.class);
                    startActivity(homeIntent);
                }
            });*/
        Intent homeIntent = new Intent(getContext(), HomeActivity.class);
        startActivity(homeIntent);
    }

    @Override
    public void onException(Exception e) {
        if (e instanceof OperationCanceledException) {
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this);
            ft.attach(this);
            ft.commit();
        } else {
            Logger.error(HomeFragment.class.getSimpleName(), e, e.getMessage());
            Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    private static Bitmap bitmapFromDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof VectorDrawable) {
            return bitmapFromVectorDrawable((VectorDrawable) drawable);
        }
        return ((BitmapDrawable) drawable).getBitmap();
    }

    private static Bitmap bitmapFromVectorDrawable(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }
    public void launchBrowser() {
        FROptions options = FROptionsBuilder.build(frOptionsBuilder -> {
            frOptionsBuilder.server(serverBuilder -> {
                serverBuilder.setUrl("https://brimo.encore.pingidentity.org/am");
                serverBuilder.setRealm("bravo");
                serverBuilder.setCookieName("420ab223210763b");
                return null;
            });
            frOptionsBuilder.oauth(oAuthBuilder -> {
                oAuthBuilder.setOauthClientId("sdk-client");
                oAuthBuilder.setOauthRedirectUri("org.forgerock.demo:/oauth2redirect");
                oAuthBuilder.setOauthScope("openid profile email address phone");
                return null;
            });
            frOptionsBuilder.service(serviceBuilder -> {
                serviceBuilder.setAuthServiceName("Login");
                serviceBuilder.setRegistrationServiceName("Registration");
                return null;
            });
            return null;
        });
        FRAuth.start(getContext(), options);
        FRUser.browser().appAuthConfigurer()
                .authorizationRequest(r -> {
                    Map<String, String> additionalParameters = new HashMap<>();
                    additionalParameters.put("service", getContext().getString(R.string.forgerock_centralize_service));
                    additionalParameters.put("KEY2", "VALUE2");
                    r.setAdditionalParameters(additionalParameters);
                    //r.setLoginHint("login");
                    //r.setPrompt("login");
                })
                .customTabsIntent(it -> {
                    it.setShowTitle(true);
                    it.setUrlBarHidingEnabled(true);
                    it.setStartAnimations(getContext(), R.animator.slide_in_right, R.animator.slide_out_left);
                    it.setExitAnimations(getContext(), android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    it.setColorScheme(CustomTabsIntent.COLOR_SCHEME_DARK);
                    it.setCloseButtonIcon(bitmapFromDrawable(getContext(), R.drawable.ic_arrow_back));
                    it.setShareState(CustomTabsIntent.SHARE_STATE_OFF);
                    it.build().intent
                            .putExtra("org.chromium.chrome.browser.customtabs.EXTRA_DISABLE_DOWNLOAD_BUTTON", true)
                            .putExtra("org.chromium.chrome.browser.customtabs.EXTRA_DISABLE_STAR_BUTTON", true);

                })
                .done()
                .login(this, new FRListener<FRUser>() {
                    @Override
                    public void onSuccess(FRUser result) {
                        Intent homeIntent = new Intent(getContext(), HomeActivity.class);
                        startActivity(homeIntent);
                    }

                    @Override
                    public void onException(Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}
