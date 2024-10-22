/*
 * Copyright (c) 2019 ForgeRock. All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 *
 */

package org.forgerock.pebblebank;

import static org.forgerock.android.auth.Action.START_AUTHENTICATE;

import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import android.content.DialogInterface;
import android.telephony.TelephonyManager;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;

import org.forgerock.android.auth.Action;
import org.forgerock.android.auth.Config;
import org.forgerock.android.auth.FRAuth;
import org.forgerock.android.auth.FRListenerFuture;
import org.forgerock.android.auth.FROptionsBuilder;
import org.forgerock.android.auth.FRRequestInterceptor;
import org.forgerock.android.auth.Logger;
import org.forgerock.android.auth.NetworkConfig;
import org.forgerock.android.auth.OkHttpClientProvider;
import org.forgerock.android.auth.PingOneProtectEvaluationCallback;
import org.forgerock.android.auth.PingOneProtectInitializeCallback;
import org.forgerock.android.auth.PolicyAdvice;
import org.forgerock.android.auth.RequestInterceptorRegistry;
import org.forgerock.android.auth.SecureCookieJar;
import org.forgerock.android.auth.ServerConfig;
import org.forgerock.android.auth.collector.LocationCollector;
import org.forgerock.android.auth.collector.PlatformCollector;
import org.forgerock.android.auth.detector.FRRootDetector;
import org.forgerock.android.auth.detector.RootDetector;
import org.forgerock.android.auth.interceptor.AccessTokenInterceptor;
import org.forgerock.android.auth.interceptor.AdviceHandler;
import org.forgerock.android.auth.interceptor.IdentityGatewayAdviceInterceptor;
import org.forgerock.android.auth.ui.AdviceDialogHandler;
import org.forgerock.android.auth.ui.CallbackFragmentFactory;
import org.forgerock.pebblebank.controller.AuthorizationPolicyInterceptor;
import org.forgerock.pebblebank.ui.main.LoginFragment;
import org.forgerock.pebblebank.ui.main.PingOneProtectEvalCallbackFragment;
import org.forgerock.pebblebank.ui.main.PingOneProtectInitCallbackFragment;
import org.forgerock.pebblebank.ui.main.SectionsPagerAdapter;
import org.forgerock.pebblebank.ui.main.SmsOTPPageFragment;
import org.forgerock.pebblebank.ui.main.WelcomePageFragment;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RequestInterceptorRegistry.getInstance().register(
                (FRRequestInterceptor<Action>) (request, tag) -> {
                    if (tag != null && tag.getType().equals(START_AUTHENTICATE) &&
                            tag.getPayload().optString("tree", "").equals("BBL-onBoarding")) {
                        return request.newBuilder()
                                .url(Uri.parse(request.url().toString())
                                        .buildUpon()
                                        .appendQueryParameter("ForceAuth", "true").toString())
                                .build();
                    }
                    return request;
                });
        /*RequestInterceptorRegistry.getInstance().register(
                new ForceAuthRequestInterceptor()
        );*/
        /*RequestInterceptorRegistry.getInstance().register(
                new AuthorizationPolicyInterceptor()
        );*/
        CallbackFragmentFactory.getInstance().register(PingOneProtectInitializeCallback.class, PingOneProtectInitCallbackFragment.class);
        CallbackFragmentFactory.getInstance().register(PingOneProtectEvaluationCallback.class, PingOneProtectEvalCallbackFragment.class);

        CallbackFragmentFactory.getInstance().register("welcome", WelcomePageFragment.class);
        CallbackFragmentFactory.getInstance().register("smsOTP", SmsOTPPageFragment.class);

        FRAuth.start(this);
  /*      try {
            NetworkConfig networkConfig = NetworkConfig.networkBuilder()
                    .identifier(Config.getInstance().getIdentifier())
                    .timeout(Config.getInstance().getTimeout())
                    .host(new URL(Config.getInstance().getUrl()).getAuthority())
                    .interceptorSupplier(() -> singletonList(new IdentityGatewayAdviceInterceptor() {
                        @Override
                        public AdviceHandler getAdviceHandler(PolicyAdvice advice) {
                            //Pre-build handler to handle Advice, e.g A Dialog to trigger the tree
                            return new AdviceDialogHandler();
                        }
                    }))
                    .cookieJarSupplier(() -> SecureCookieJar.builder().context(this).build())
                    .build();
            OkHttpClient httpClient = OkHttpClientProvider.getInstance().lookup(networkConfig);
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/
        Logger.set(Logger.Level.DEBUG);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        RootDetector rootDetector = FRRootDetector.builder()
        .detectors(FRRootDetector.DEFAULT_DETECTORS)
       /* .detector(new RootDetector() {
            @Override
            public double isRooted(Context context) {
                //check emulator
                if (Build.PRODUCT.matches(".*_?sdk_?.*")) {
                    return 1;
                }
                else {
                    return 0;
                }
            }
        })*/
        .build();

        if (rootDetector.isRooted(this.getApplicationContext()) > 0) {
            showAlert();
            Intent theIntent = this.getIntent();
            theIntent.putExtra(LoginFragment.TREE_NAME, "Login-1");
        }

        FirebaseApp.initializeApp(this);

   }

   private void showAlert() {
       // Create the object of AlertDialog Builder class
       AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);

       // Set the message show for the Alert time
       builder.setMessage("Your device is tampered, you are not allowed to access PebbleBank app from this device!");

       // Set Alert Title
       builder.setTitle("Alert !");

       // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
       builder.setCancelable(false);

       // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
       builder.setPositiveButton("Exit", (DialogInterface.OnClickListener) (dialog, which) -> {
           // When the user click yes button then app will close
           finish();
       });

       // Create the Alert dialog
       AlertDialog alertDialog = builder.create();
       // Show the Alert Dialog box
       alertDialog.show();
   }
}