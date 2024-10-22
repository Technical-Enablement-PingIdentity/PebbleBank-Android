package org.forgerock.pebblebank.ui.transactionsigning;

import static org.forgerock.android.auth.Action.START_AUTHENTICATE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.forgerock.android.auth.Action;
import org.forgerock.android.auth.FRListener;
import org.forgerock.android.auth.FRRequestInterceptor;
import org.forgerock.android.auth.FRSession;
import org.forgerock.android.auth.FRUser;
import org.forgerock.android.auth.FRUserKeys;
import org.forgerock.android.auth.Node;
import org.forgerock.android.auth.NodeListener;
import org.forgerock.android.auth.RequestInterceptorRegistry;
import org.forgerock.android.auth.UserInfo;
import org.forgerock.android.auth.callback.DeviceSigningVerifierCallback;
import org.forgerock.android.auth.devicebind.DeviceBindingStatus;
import org.forgerock.android.auth.devicebind.None;
import org.forgerock.android.auth.devicebind.Success;
import org.forgerock.android.auth.devicebind.UserKey;
import org.forgerock.android.auth.devicebind.UserKeyService;
import org.forgerock.pebblebank.controller.AuthorizationPolicyInterceptor;
import org.forgerock.pebblebank.domain.Transaction;

import java.security.PrivateKey;
import java.util.List;

public class TransactionSigningViewModel extends ViewModel {

    public static final String DEVICE_SIGNING = "transactionSigning";
    private MutableLiveData<Transaction> transactionMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> result = new MutableLiveData<>();
    private Node node;
    private DeviceSigningVerifierCallback callback;
    private NodeListener<FRSession> nodeListener;

    private Context context;

    public TransactionSigningViewModel() {
        nodeListener = new NodeListener<FRSession>() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onCallbackReceived(Node node) {
                DeviceSigningVerifierCallback callback = node.getCallback(DeviceSigningVerifierCallback.class);
                TransactionSigningViewModel.this.callback = callback;
                TransactionSigningViewModel.this.node = node;
                Transaction transaction = new Transaction();
                transaction.setPayload(callback.getChallenge());

/*                FRUserKeys keys = new FRUserKeys(getContext());
                List<UserKey> userKeyList = keys.loadAll();
                String theUserID;
                for (UserKey key : userKeyList) {
                    String userId = key.getUserId();
                    theUserID = userId;
                    if (!theUserID.equals("id=5a2466fb-57c8-4172-acb2-9f51157a39b0,ou=user,ou=am-config")) {
                        keys.delete(key, true, new FRListener<Void>() {
                                    @Override
                                    public void onException(@NonNull Exception e) {

                                    }

                                    @Override
                                    public void onSuccess(Void result) {
                                        transactionMutableLiveData.postValue(transaction);
                                    }
                                }
                        );
                    }
                }
                None none = new None();
                DeviceAuthenticatorKt authenticator = new DeviceAuthenticatorKt(none);
                authenticator.initialize("id=5a2466fb-57c8-4172-acb2-9f51157a39b0,ou=user,ou=am-config");
                authenticator.authenticate(getContext(), new FRListener<DeviceBindingStatus>() {
                    @Override
                    public void onSuccess(DeviceBindingStatus result) {
                        PrivateKey privateKey = ((Success) result).getPrivateKey();
                        transactionMutableLiveData.postValue(transaction);
                    }

                    @Override
                    public void onException(@NonNull Exception e) {

                    }
                });*/
                transactionMutableLiveData.postValue(transaction);
            }

            @Override
            public void onSuccess(FRSession session) {
                result.postValue(TRUE);

            }

            @Override
            public void onException(Exception e) {
                result.postValue(FALSE);
            }
        };


    }

    public LiveData<Boolean> sign(Context context) {
        callback.sign(context, new FRListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                node.next(context, nodeListener);
            }

            @Override
            public void onException(Exception e) {
                callback.setClientError("Abort");
                node.next(context, nodeListener);
            }
        });
        return result;
    }

    public LiveData<Boolean> reject(Context context) {
        callback.setClientError("Abort");
        node.next(context, nodeListener);
        return result;
    }

    public LiveData<Transaction> getTransaction(Context context, String transactionId) {
        RequestInterceptorRegistry.getInstance().register(
                (FRRequestInterceptor<Action>) (request, tag) -> {
                    if (tag.getType().equals(START_AUTHENTICATE) &&
                            tag.getPayload().optString("tree", "").equals(DEVICE_SIGNING)) {
                        return request.newBuilder()
                                .url(Uri.parse(request.url().toString())
                                        .buildUpon()
                                        .appendQueryParameter("ForceAuth", "true").toString())
                                .header("transactionId", transactionId)
                                .build();
                    }
                    return request;
                });
        /*RequestInterceptorRegistry.getInstance().register(
                new AuthorizationPolicyInterceptor()
        );*/
        FRSession.authenticate(context, DEVICE_SIGNING, nodeListener);
        return transactionMutableLiveData;

    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}