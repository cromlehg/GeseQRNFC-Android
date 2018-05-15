package com.gese;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.support.annotation.NonNull;

import static com.gese.HttpUtils.SCHEME_HTTP;
import static com.gese.HttpUtils.SCHEME_HTTPS;

public class ForegroundDispatch {

    public static void setupForegroundDispatch(@NonNull final Activity activity,
                                               @NonNull NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent
                .getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addDataScheme(SCHEME_HTTP);
        filter.addDataScheme(SCHEME_HTTPS);

        adapter.enableForegroundDispatch(activity, pendingIntent,
                new IntentFilter[] {filter}, new String[][] {});
    }

    public static void stopForegroundDispatch(@NonNull final Activity activity,
                                              @NonNull NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }
}
