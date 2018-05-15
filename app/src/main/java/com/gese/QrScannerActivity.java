package com.gese;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.gese.HttpUtils.SCHEME_HTTP;
import static com.gese.HttpUtils.SCHEME_HTTPS;
import static com.gese.HttpUtils.WWW;
import static com.gese.HttpUtils.startBrowser;

public class QrScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(final Result result) {
        final String stringResult = result.getText();
        if (stringResult.startsWith(SCHEME_HTTP)
                || stringResult.startsWith(SCHEME_HTTPS) || stringResult.startsWith(WWW)) {

            startBrowser(this, stringResult);

            finish();
        } else {
            Toast.makeText(this, R.string.qr_no_link, Toast.LENGTH_LONG).show();

            mScannerView.resumeCameraPreview(this);
        }
    }
}
