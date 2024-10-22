package org.forgerock.pebblebank.ui.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.OperationCanceledException;

import java.util.Calendar;

public class RegisterFragment extends org.forgerock.android.auth.ui.RegisterFragment {
    @Override
    public void cancel(Exception e) {
        if (e instanceof OperationCanceledException) {
            restartSelf();
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
}
