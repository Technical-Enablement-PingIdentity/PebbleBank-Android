package org.forgerock.pebblebank.ui.transfer;

import android.os.OperationCanceledException;

import androidx.navigation.Navigation;

import org.forgerock.pebblebank.R;
import org.forgerock.pebblebank.ui.main.HomeFragment;

public class LoginFragment extends org.forgerock.android.auth.ui.LoginFragment {

    @Override
    public void cancel(Exception e) {
        if (e instanceof OperationCanceledException) {
            try {
                Navigation.findNavController(this.getView()).navigate(R.id.nav_home);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
