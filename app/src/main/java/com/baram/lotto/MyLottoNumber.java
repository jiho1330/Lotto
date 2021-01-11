package com.baram.lotto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MyLottoNumber extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lotto_number);
    }

    public void OnLottoNumberManagementClick(View v) {
        Intent intent = new Intent(this, LottoNumberManagement.class);
        startActivityForResult(intent, 1);
    }
}