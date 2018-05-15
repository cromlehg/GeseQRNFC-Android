package com.gese;

import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import static com.gese.ForegroundDispatch.setupForegroundDispatch;
import static com.gese.HttpUtils.SCHEME_HTTP;
import static com.gese.HttpUtils.SCHEME_HTTPS;
import static com.gese.HttpUtils.WWW;
import static com.gese.HttpUtils.startBrowser;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CAMERA = 1;

    private AlertDialog mNfcAlertDialog;
    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.qr_button).setOnClickListener(v -> onQrButtonClick());
        findViewById(R.id.nfc_button).setOnClickListener(v -> onNfcButtonClick());

        handleIntent(getIntent());
    }

    @Override
    protected void onPause() {
        stopForegroundDispatch();

        dismissNfcAlert();

        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, QrScannerActivity.class));
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void onQrButtonClick() {
        if (VERSION.SDK_INT >= VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission.CAMERA)) {
                Toast.makeText(this, R.string.permission_camera_required, Toast.LENGTH_LONG).show();
            } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
            }
        } else {
            startActivity(new Intent(this, QrScannerActivity.class));
        }
    }

    private void onNfcButtonClick(){
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            Toast.makeText(this, R.string.nfc_unsupported, Toast.LENGTH_LONG).show();
        } else {
            if (mNfcAdapter.isEnabled()) {
                setupForegroundDispatch(this, mNfcAdapter);

                mNfcAlertDialog = new AlertDialog.Builder(this)
                        .setMessage(R.string.nfc_foreground_enabled)
                        .setNegativeButton(android.R.string.cancel, null)
                        .setOnCancelListener(dialog -> stopForegroundDispatch())
                        .create();
                mNfcAlertDialog.show();
            } else {
                mNfcAlertDialog = new AlertDialog.Builder(this)
                        .setMessage(R.string.enable_nfc)
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok,
                                (dialog, which) -> startNfcSettingsActivity())
                        .create();
                mNfcAlertDialog.show();
            }
        }
    }

    private void handleIntent(@NonNull Intent intent) {
        final String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            final String scheme = intent.getScheme();

            if (scheme != null && (scheme.startsWith(SCHEME_HTTP)
                    || scheme.startsWith(SCHEME_HTTPS) || scheme.startsWith(WWW))) {

                final Uri data = intent.getData();
                if (data != null) {
                    startBrowser(this, data);
                }
            }
        }
    }

    private void startNfcSettingsActivity() {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
        } else {
            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }
    }

    private void stopForegroundDispatch() {
        if (mNfcAdapter != null) {
            ForegroundDispatch.stopForegroundDispatch(this, mNfcAdapter);
        }
    }

    private void dismissNfcAlert() {
        if (mNfcAlertDialog != null && mNfcAlertDialog.isShowing()) {
            mNfcAlertDialog.dismiss();
            mNfcAlertDialog = null;
        }
    }
}
