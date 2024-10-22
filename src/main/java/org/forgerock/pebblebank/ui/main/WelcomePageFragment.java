package org.forgerock.pebblebank.ui.main;

import static android.text.TextUtils.isEmpty;
import static android.view.View.GONE;

import android.os.Bundle;
import android.os.OperationCanceledException;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.forgerock.android.auth.callback.Callback;
import org.forgerock.android.auth.ui.CallbackFragmentFactory;
import org.forgerock.android.auth.ui.page.PageFragment;

public class WelcomePageFragment extends PageFragment {

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
            for (Callback callback : node.getCallbacks()) {
                Fragment fragment = CallbackFragmentFactory.getInstance().getFragment(node, callback);
                if (fragment != null) {
                    getChildFragmentManager().beginTransaction()
                            .add(org.forgerock.android.auth.ui.R.id.callbacks, fragment).commit();
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
