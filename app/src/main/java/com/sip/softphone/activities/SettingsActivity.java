package com.sip.softphone.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sip.softphone.R;

public class SettingsActivity extends Activity implements View.OnClickListener {
    public static String TAG = "Softphone";


    private Button mBtnSaveSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mBtnSaveSettings = (Button) findViewById(R.id.save_settings);
        mBtnSaveSettings.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_settings:
                Intent it = new Intent(SettingsActivity.this, AccountSetting.class);
                startActivity(it);
                finish();
                break;
            default:
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
