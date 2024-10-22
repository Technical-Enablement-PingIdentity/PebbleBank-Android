package org.forgerock.pebblebank;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.forgerock.android.auth.PushNotification;
import org.forgerock.pebblebank.ui.transactionsigning.TransactionSigningFragment;

public class TransactionSigningActivity extends BaseNotificationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_signing);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container,
                            TransactionSigningFragment.newInstance(
                                    getIntent().getStringExtra("transactionId")))
                    .commitNow();
        }
    }

    public PushNotification getPushNotification() {
        return this.getNotification();
    }
}