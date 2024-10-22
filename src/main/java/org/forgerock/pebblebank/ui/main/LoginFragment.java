package org.forgerock.pebblebank.ui.main;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import static org.forgerock.android.auth.AuthService.SUSPENDED_ID;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.OperationCanceledException;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import org.forgerock.android.auth.Account;
import org.forgerock.android.auth.FRListener;
import org.forgerock.android.auth.FRSession;
import org.forgerock.android.auth.FRUser;
import org.forgerock.android.auth.FRUserKeys;
import org.forgerock.android.auth.Listener;
import org.forgerock.android.auth.Node;
import org.forgerock.android.auth.PingOneProtectInitException;
import org.forgerock.android.auth.PingOneProtectInitializeCallback;
import org.forgerock.android.auth.PingOneProtectInitializeCallbackKt;
import org.forgerock.android.auth.UserInfo;
import org.forgerock.android.auth.callback.DeviceBindingCallback;
import org.forgerock.android.auth.callback.DeviceSigningVerifierCallback;
import org.forgerock.android.auth.callback.PasswordCallback;
import org.forgerock.android.auth.devicebind.UserKey;
import org.forgerock.android.auth.exception.AuthenticationException;
import org.forgerock.android.auth.exception.AuthenticationRequiredException;
import org.forgerock.android.auth.exception.AuthenticationTimeoutException;
import org.forgerock.android.auth.ui.AuthHandler;
import org.forgerock.android.auth.ui.AuthenticationExceptionListener;
import org.forgerock.android.auth.ui.CallbackFragmentFactory;
import org.forgerock.android.auth.ui.FRSessionViewModel;
import org.forgerock.android.auth.ui.FRViewModel;
import org.forgerock.pebblebank.controller.AuthenticatorModel;
import org.forgerock.pebblebank.ui.biometric.BiometricStatusModel;

import java.util.Calendar;
import java.util.List;

public class LoginFragment extends Fragment implements AuthHandler {

    private static final String CURRENT_EMBEDDED_FRAGMENT = "CURRENT_EMBEDDED_FRAGMENT";
    public static final String TREE_NAME = "TREE_NAME";
    private boolean loadOnStartup;
    FRViewModel<FRSession> viewModel;
    ProgressBar progressBar;
    //Listener to listener for Login Event
    private FRListener<Void> listener;

    private boolean silentBinding = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListener(getParentFragment());
        viewModel = new ViewModelProvider(this).get(FRSessionViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(org.forgerock.android.auth.ui.R.layout.login_fragment, container, false);
        progressBar = view.findViewById(org.forgerock.android.auth.ui.R.id.progress);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getNodeLiveData().observe(getViewLifecycleOwner(), node -> {
            progressBar.setVisibility(INVISIBLE);
            Node n = node.getValue();
            if (n != null) {
                Fragment callbackFragment = CallbackFragmentFactory.getInstance().getFragment(n);

                if (n.getCallbacks().size() == 1 && (n.getCallbacks().get(0) instanceof DeviceBindingCallback) && !(n.getCallbacks().get(0) instanceof DeviceSigningVerifierCallback)) {
                    silentBinding = true;
                }
                if (n.getCallbacks().size() == 1 && (n.getCallbacks().get(0) instanceof PasswordCallback)) {
                    if (silentBinding) {
 /*                       FRUser.getCurrentUser().getUserInfo(new FRListener<UserInfo>() {
                            @Override
                            public void onSuccess(UserInfo userinfo) {
                                String currUserID = userinfo.getSub();
                                FRUserKeys keys = new FRUserKeys(getContext());
                                List<UserKey> userKeyList = keys.loadAll();
                                String theUserID;
                                if (userKeyList.size() > 0) {
                                    for (UserKey key : userKeyList) {
                                        if (key.getUserId().indexOf("id=" + currUserID + ",") == 0) {
                                            keys.delete(key, true, new FRListener<Void>() {
                                                @Override
                                                public void onException(@NonNull Exception e) {
                                                    getChildFragmentManager().beginTransaction()
                                                            .replace(org.forgerock.android.auth.ui.R.id.container, callbackFragment, CURRENT_EMBEDDED_FRAGMENT).commit();

                                                }

                                                @Override
                                                public void onSuccess(Void result1) {
                                                    getChildFragmentManager().beginTransaction()
                                                            .replace(org.forgerock.android.auth.ui.R.id.container, callbackFragment, CURRENT_EMBEDDED_FRAGMENT).commit();

                                                }
                                            });
                                        }
                                    }
                                } else {
                                    getChildFragmentManager().beginTransaction()
                                            .replace(org.forgerock.android.auth.ui.R.id.container, callbackFragment, CURRENT_EMBEDDED_FRAGMENT).commit();

                                }
                            }

                            @Override
                            public void onException(Exception e) {
                                getChildFragmentManager().beginTransaction()
                                        .replace(org.forgerock.android.auth.ui.R.id.container, callbackFragment, CURRENT_EMBEDDED_FRAGMENT).commit();

                            }
                        });*/
                        getChildFragmentManager().beginTransaction()
                                .replace(org.forgerock.android.auth.ui.R.id.container, callbackFragment, CURRENT_EMBEDDED_FRAGMENT).commit();
                    } else {
                        getChildFragmentManager().beginTransaction()
                                .replace(org.forgerock.android.auth.ui.R.id.container, callbackFragment, CURRENT_EMBEDDED_FRAGMENT).commit();
                    }
               /*} else if (n.getCallbacks().size() == 1 && (n.getCallbacks().get(0) instanceof PingOneProtectInitializeCallback)) {
                    PingOneProtectInitCallbackKt p1pInit = new PingOneProtectInitCallbackKt((PingOneProtectInitializeCallback) n.getCallbacks().get(0));
                    p1pInit.start(getContext(), new FRListener<Void>() {
                        @Override
                        public void onSuccess(Void result) {

                            getChildFragmentManager().beginTransaction()
                                    .replace(org.forgerock.android.auth.ui.R.id.container, callbackFragment, CURRENT_EMBEDDED_FRAGMENT).commit();
                        }
                        @Override
                        public void onException(@NonNull Exception e) {
                            if (e instanceof PingOneProtectInitException) {
                                e.printStackTrace();
                            } else {
                                e.printStackTrace();
                                ((PingOneProtectInitializeCallback) n.getCallbacks().get(0)).setClientError(e.getMessage());
                            }
                            getChildFragmentManager().beginTransaction()
                                    .replace(org.forgerock.android.auth.ui.R.id.container, callbackFragment, CURRENT_EMBEDDED_FRAGMENT).commit();
                        }
                    });*/
                } else {
                    getChildFragmentManager().beginTransaction()
                            .replace(org.forgerock.android.auth.ui.R.id.container, callbackFragment, CURRENT_EMBEDDED_FRAGMENT).commit();
                }
            }
        });

        viewModel.getResultLiveData().observe(getViewLifecycleOwner(), frUser -> {
            progressBar.setVisibility(INVISIBLE);
            Listener.onSuccess(listener, null);
        });

        viewModel.getExceptionLiveData().observe(getViewLifecycleOwner(), e -> {
            progressBar.setVisibility(INVISIBLE);
            Exception exception = e.getValue();
            if (exception != null && !handleException(exception)) {
                cancel(exception);
            }
        });

        if (savedInstanceState == null) {
            if (loadOnStartup) {
                start();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setListener(context);
    }

    private void setListener(Object o) {
        if (o instanceof FRListener) {
            this.listener = (FRListener<Void>) o;
        }
    }

    public void start() {
        progressBar.setVisibility(VISIBLE);
        if (getActivity() != null) {
            Intent intent = getActivity().getIntent();
            Uri data = intent.getData();
            //If the intent contains suspendedId, we resume the flow
            if (data != null && data.getQueryParameter(SUSPENDED_ID) != null) {
                //Resume suspended Tree
                viewModel.authenticate(getContext(), data);
                return;
            }
        }
        viewModel.authenticate(getContext());
    }

    @Override
    public void next(Node current) {
        progressBar.setVisibility(VISIBLE);
        viewModel.next(getContext(), current);
    }

    @Override
    public void cancel(Exception e) {
        if (e instanceof OperationCanceledException) {
            getActivity().finish();
            startActivity(getActivity().getIntent());
            //restartSelf();
        }
    }

    private void restartSelf() {
        AlarmManager am = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 500, // one second
                PendingIntent.getActivity(getActivity(), 0, getActivity().getIntent(), PendingIntent.FLAG_ONE_SHOT
                        | PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE));
        Intent i = getActivity().getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    /**
     * Handle Exception during Intelligent Tree Authentication
     *
     * @param e The Exception
     * @return True if user can continue with the current Node (e.g Invalid password)
     * , False if we cannot continue the flow.
     */
    private boolean handleException(final Exception e) {
        if (e instanceof AuthenticationRequiredException || e instanceof AuthenticationTimeoutException) {
            viewModel.authenticate(getContext());
        } else if (e instanceof AuthenticationException) {
            Fragment fragment = getChildFragmentManager().findFragmentByTag(CURRENT_EMBEDDED_FRAGMENT);
            if (fragment instanceof AuthenticationExceptionListener) {
                ((AuthenticationExceptionListener) fragment).onAuthenticationException((AuthenticationException) e);
            } else {
                cancel(e);
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs, @Nullable Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        //Retrieve fragment configuration from attr.xml
        TypedArray a = context.obtainStyledAttributes(attrs, org.forgerock.android.auth.ui.R.styleable.LoginFragment);
        this.loadOnStartup = a.getBoolean(org.forgerock.android.auth.ui.R.styleable.LoginFragment_loadOnStartup, true);
        a.recycle();
    }
}
