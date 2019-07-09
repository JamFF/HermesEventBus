package com.ff.eventbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ff.eventbus.lib.EventBus;
import com.ff.eventbus.lib.Subscribe;
import com.ff.eventbus.lib.ThreadMode;
import com.ff.eventbus.manager.UserManager;
import com.ff.hermes.Hermes;
import com.ff.hermes.HermesListener;
import com.ff.hermes.service.HermesService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.tv);
        findViewById(R.id.bt_event).setOnClickListener(this);
        findViewById(R.id.bt_hermes).setOnClickListener(this);
        findViewById(R.id.bt_get).setOnClickListener(this);

        // 注册普通EventBus
        EventBus.INSTANCE.register(this);

        // 注册Hermes
        Hermes.INSTANCE.init(this);
        Hermes.INSTANCE.register(UserManager.class);

        Hermes.INSTANCE.setHermesListener(new HermesListener() {
            @Override
            public void onHermesConnected(Class<? extends HermesService> service) {
                Log.d(TAG, "onHermesConnected: ");
            }

            @Override
            public void onHermesDisconnected(Class<? extends HermesService> service) {
                super.onHermesDisconnected(service);
                Log.d(TAG, "onHermesDisconnected: ");
            }
        });

        // 单例设置对象
        UserManager.getInstance().setPerson(new Person("FF", 18));
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void receive(Person person) {
        final String text = person + "-" + Thread.currentThread().getName();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 从Android 7.0开始，子线程也可以setText
                tv.setText(text);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_event:
                startActivity(new Intent(this, SendActivity.class));
                break;
            case R.id.bt_hermes:
                startActivity(new Intent(this, HermesActivity.class));
                break;
            case R.id.bt_get:
                Toast.makeText(this, UserManager.getInstance().getPerson().toString(),
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.INSTANCE.unregister(this);
        super.onDestroy();
    }
}
