package com.example.oskin.servicestartapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mStartService;
    private Button mSecondActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mSecondActivity = findViewById(R.id.service_start_btn_second);
        mStartService = findViewById(R.id.service_start_activity_btn_start_service);

        mSecondActivity.setOnClickListener(new ButtonClickListenerSecondActivity());
        mStartService.setOnClickListener(new ButtonClickListenerStartService());
    }

    private class ButtonClickListenerStartService implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            startService(MyIntentService.getIntentForSend(MainActivity.this, "Main Activity"));
        }
    }

    private class ButtonClickListenerSecondActivity implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            startActivity(SecondActivity.newIntent(MainActivity.this));
        }
    }
}
