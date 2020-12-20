package com.baram.lotto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class QRScanActivity extends AppCompatActivity {

    private IntentIntegrator integrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

        integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false); // 방향전환 잠금 해제
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            // 취소 시
            if(result.getContents() == null) {
                finish();
            } else {
                String uri = result.getContents();
                //Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri)); // 웹으로 실행

                // WebView Activity로 전환
                Intent mIntent = new Intent(QRScanActivity.this, WebViewActivity.class);
                mIntent.putExtra("URI", uri);
                startActivity(mIntent);
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}