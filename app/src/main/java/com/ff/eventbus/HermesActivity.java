package com.ff.eventbus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.ff.eventbus.manager.IUserManager;
import com.ff.hermes.Hermes;

/**
 * description: 跨进程的客户端
 * author: FF
 * time: 2019-07-06 16:11
 */
public class HermesActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "HermesActivity";

    // 代理对象
    private IUserManager mIUserManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hermes);

        findViewById(R.id.bt_instance).setOnClickListener(this);
        findViewById(R.id.bt_get).setOnClickListener(this);
        findViewById(R.id.bt_set).setOnClickListener(this);

        // hermes进程客户端调用连接
        Hermes.INSTANCE.connect(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_instance:
                // 创建UserManager动态代理对象
                // 执行完该方法后，会在服务端获取到UserManager单例对象
                if (mIUserManager == null) {
                    mIUserManager = Hermes.getInstance(IUserManager.class);
                }
                break;
            case R.id.bt_get:
                if (mIUserManager == null) {
                    Toast.makeText(this, "先创建代理对象", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 调用动态代理对象的方法，会调用服务端UserManager单例对象的getPerson()并返回
                Person person = mIUserManager.getPerson();
                Toast.makeText(this, person.toString(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_set:
                if (mIUserManager == null) {
                    Toast.makeText(this, "先创建代理对象", Toast.LENGTH_SHORT).show();
                    return;
                }
                mIUserManager.setPerson(new Person("Jam", 22));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        Hermes.INSTANCE.disconnect(this);
        super.onDestroy();
    }
}
