package org.forgerock.pebblebank.ui.transactionsigning;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.forgerock.android.auth.FRAListener;
import org.forgerock.android.auth.PolicyAdvice;
import org.forgerock.android.auth.PushNotification;
import org.forgerock.android.auth.callback.DeviceBindingAuthenticationType;
import org.forgerock.android.auth.devicebind.DeviceAuthenticator;
import org.forgerock.android.auth.devicebind.DeviceBindAuthenticatorsKt;
import org.forgerock.pebblebank.BaseNotificationActivity;
import org.forgerock.pebblebank.HomeActivity;
import org.forgerock.pebblebank.PushNotificationActivity;
import org.forgerock.pebblebank.R;
import org.forgerock.pebblebank.TransactionSigningActivity;
import org.forgerock.pebblebank.domain.Transaction;
import org.json.JSONException;
import org.json.JSONObject;

public class TransactionSigningFragment extends DialogFragment {

    public static final String TRANSACTION_ID = "TRANSACTION_ID";
    private TransactionSigningViewModel mViewModel;
    private String transactionId;

    private TextView fromAccount;
    private TextView toAccount;
    private TextView currency;
    private TextView amount;
    private Button approve;
    private Button reject;

    private PushNotification notification;

    public static TransactionSigningFragment newInstance(String transactionId) {
        TransactionSigningFragment fragment = new TransactionSigningFragment();
        Bundle args = new Bundle();
        args.putString(TRANSACTION_ID, transactionId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transactionId = getArguments().getString(TRANSACTION_ID);
        }

        TransactionSigningActivity activity = (TransactionSigningActivity)this.getActivity();
        notification = activity.getPushNotification();
        mViewModel = new ViewModelProvider(this).get(TransactionSigningViewModel.class);
        mViewModel.setContext(getContext());
        mViewModel.getTransaction(getContext(), transactionId).observe(this, transaction -> {
            try {
                JSONObject jsonObject = new JSONObject(transaction.getPayload());
                JSONObject payload = new JSONObject(jsonObject.getString("payload"));
                fromAccount.setText(payload.getString("fromAccount"));
                toAccount.setText(payload.getString("toAccount"));
                currency.setText(payload.getString("currency"));
                amount.setText(payload.getString("amount"));
                approve.setEnabled(true);
                reject.setEnabled(true);

            } catch (JSONException e) {
                //ignore for now
            }
        });


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transaction_signing, container, false);
        fromAccount = v.findViewById(R.id.fromAccount);
        toAccount = v.findViewById(R.id.toAccount);
        currency = v.findViewById(R.id.currency);
        amount = v.findViewById(R.id.amount);
        approve = v.findViewById(R.id.approve);
        reject = v.findViewById(R.id.reject);
        approve.setEnabled(false);
        reject.setEnabled(false);
        approve.setOnClickListener(view -> mViewModel.sign(getContext()).observe(TransactionSigningFragment.this, aBoolean -> {
            approve.setEnabled(false);
            reject.setEnabled(false);
            if (aBoolean != null && aBoolean) {
                Toast.makeText(getContext(), "Transaction Signed.", Toast.LENGTH_LONG).show();
           } else {
                Toast.makeText(getContext(), "Transaction Sign Failed.", Toast.LENGTH_LONG).show();
            }
            notification.accept(new FRAListener<Void>() {
                @Override
                public void onSuccess(Void result) {
                }

                @Override
                public void onException(Exception e) {

                }
            });
            Intent intent = new Intent(getContext(),HomeActivity.class);
            getContext().startActivity(intent);
        }));

        reject.setOnClickListener(view -> mViewModel.reject(getContext()).observe(TransactionSigningFragment.this, aBoolean -> {
            approve.setEnabled(false);
            reject.setEnabled(false);
            Toast.makeText(getContext(), "Transaction Rejected.", Toast.LENGTH_LONG).show();
            notification.accept(new FRAListener<Void>() {
                @Override
                public void onSuccess(Void result) {
                }

                @Override
                public void onException(Exception e) {
                }
            });
            Intent intent = new Intent(getContext(),HomeActivity.class);
            getContext().startActivity(intent);
        }));

        return v;
    }

}