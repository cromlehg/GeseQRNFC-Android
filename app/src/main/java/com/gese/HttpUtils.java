package com.gese;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

public class HttpUtils {

    public static final String SCHEME_HTTP = "http";
    public static final String SCHEME_HTTPS = "https";

    public static final String WWW = "www";

    public static void startBrowser(@NonNull Activity activity, @NonNull String uri) {
        startBrowser(activity, Uri.parse(uri));
    }

    public static void startBrowser(@NonNull Activity activity, @NonNull Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);

        activity.startActivity(intent);
    }
}
