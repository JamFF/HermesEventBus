package com.ff.eventbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ff.eventbus.lib.EventBus;
import com.ff.eventbus.lib.Subscribe;
import com.ff.eventbus.lib.ThreadMode;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.tv);
        findViewById(R.id.bt_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SendActivity.class));
            }
        });

        EventBus.INSTANCE.register(this);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void receive(Person person) {
        final String text = person + "-" + Thread.currentThread().getName();
        // 从Android7.0开始子线程也可以修改
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(text);
            }
        });
        Log.d(TAG, "receive: " + text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onDestroy() {
        EventBus.INSTANCE.unregister(this);
        super.onDestroy();
    }
}
