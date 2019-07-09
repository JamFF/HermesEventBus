package com.ff.eventbus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ff.eventbus.lib.EventBus;

public class SendActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        findViewById(R.id.bt_send).setOnClickListener(this);
        findViewById(R.id.bt_thread_send).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_send:
                send();
                break;
            case R.id.bt_thread_send:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        send();
                    }
                }).start();
                break;
        }
    }

    private void send() {
        EventBus.INSTANCE.post(new Person("JamFF", 20));
        finish();
    }
}
