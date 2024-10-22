package org.forgerock.pebblebank.ui.main;

import static android.text.TextUtils.isEmpty;
import static android.view.View.GONE;

import android.os.Bundle;
import android.os.OperationCanceledException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import org.forgerock.android.auth.callback.Callback;
import org.forgerock.android.auth.callback.ConfirmationCallback;
import org.forgerock.android.auth.callback.PasswordCallback;
import org.forgerock.android.auth.ui.CallbackFragmentFactory;
import org.forgerock.android.auth.ui.page.PageFragment;

public class SmsOTPPageFragment extends PageFragment {

    private LinearLayout errorLayout;
    private LinearLayout callbackLayout;
    private Button nextButton;
    private Button cancelButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(org.forgerock.android.auth.ui.R.layout.fragment_callbacks, container, false);
        errorLayout = view.findViewById(org.forgerock.android.auth.ui.R.id.error);
        callbackLayout = view.findViewById(org.forgerock.android.auth.ui.R.id.callbacks);
        nextButton = view.findViewById(org.forgerock.android.auth.ui.R.id.next);
        cancelButton = view.findViewById(org.forgerock.android.auth.ui.R.id.cancel);

        TextView header = view.findViewById(org.forgerock.android.auth.ui.R.id.header);
        if (isEmpty(node.getHeader())) {
            header.setVisibility(GONE);
        } else {
            header.setText(node.getHeader());
        }

        TextView description = view.findViewById(org.forgerock.android.auth.ui.R.id.description);
        if (isEmpty(node.getDescription())) {
            description.setVisibility(GONE);
        } else {
            description.setText(node.getDescription());
        }

        //Add callback to LinearLayout Vertically
        if (savedInstanceState == null) {
            boolean passwordCallbackExist = false;
            boolean confirmationCallbackExist = false;
            for (Callback callback : node.getCallbacks()) {
                if (callback.getType().equals("PasswordCallback")) {
                    passwordCallbackExist = true;
                } else if (callback.getType().equals("ConfirmationCallback")) {
                    confirmationCallbackExist = true;
                } else {
                    Fragment fragment = CallbackFragmentFactory.getInstance().getFragment(node, callback);
                    if (fragment != null) {
                        getChildFragmentManager().beginTransaction()
                                .add(org.forgerock.android.auth.ui.R.id.callbacks, fragment).commit();
                    }
                }
            }
            if (passwordCallbackExist) {
                PasswordCallback callback = node.getCallback(PasswordCallback.class);
                final View view0 = inflater.inflate(org.forgerock.android.auth.ui.R.layout.fragment_password_callback, container, false);
                EditText text = view0.findViewById(org.forgerock.android.auth.ui.R.id.password);
                TextInputLayout textInputLayout = view0.findViewById(org.forgerock.android.auth.ui.R.id.passwordInputLayout);
                textInputLayout.setHint(callback.getPrompt());
                text.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        callback.setPassword(s.toString().toCharArray());
                    }
                });
                callbackLayout.addView(view0);

                if (confirmationCallbackExist) {
                    ConfirmationCallback callback1 = node.getCallback(ConfirmationCallback.class);
                    final View view1 = inflater.inflate(org.forgerock.android.auth.ui.R.layout.fragment_confirmation_callback, container, false);
                    TextView prompt = view1.findViewById(org.forgerock.android.auth.ui.R.id.prompt);
                    prompt.setText(callback1.getPrompt());
                    LinearLayout confirmation = view1.findViewById(org.forgerock.android.auth.ui.R.id.confirmation);
                    for (int i = 1; i < callback1.getOptions().size(); i++) {
                        Button button = new Button(getContext());
                        button.setText(callback1.getOptions().get(i));
                        final int finalI = i;
                        button.setOnClickListener(v -> {
                            callback1.setSelectedIndex(finalI);
                            callback.setPassword("xxx".toCharArray());
                            onDataCollected();
                        });
                        confirmation.addView(button, i - 1);
                    }
                    callbackLayout.addView(view1);
                }
            }
        }

        nextButton.setOnClickListener(v -> {
            errorLayout.setVisibility(View.INVISIBLE);
            onDataCollected();
        });

        //Action to proceed cancel
        cancelButton.setOnClickListener(v ->
                cancel(new OperationCanceledException()));

        return view;
    }

}
