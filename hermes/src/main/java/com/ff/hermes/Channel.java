package com.ff.hermes;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.ff.hermes.service.HermesService;

import java.util.concurrent.ConcurrentHashMap;

/**
 * description: ServiceConnection管理类
 * author: FF
 * time: 2019-07-06 16:50
 */
public enum Channel {

    INSTANCE;

    private HermesListener mListener = null;

    private final ConcurrentHashMap<Class<? extends HermesService>, IHermesService>
            mHermesServices = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Class<? extends HermesService>, HermesServiceConnection>
            mHermesServiceConnections = new ConcurrentHashMap<>();

    public void bind(Context context, String packageName, Class<? extends HermesService> service) {
        HermesServiceConnection connection;
        synchronized (this) {
            connection = new HermesServiceConnection(service);
            mHermesServiceConnections.put(service, connection);
        }
        Intent intent;
        if (TextUtils.isEmpty(packageName)) {
            // 同一个应用，包名一直的情况下，packageName传null
            intent = new Intent(context, service);
        } else {
            intent = new Intent();
            intent.setClassName(packageName, service.getName());
        }
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void unbind(Context context, Class<? extends HermesService> service) {
        synchronized (this) {
            HermesServiceConnection connection = mHermesServiceConnections.get(service);
            if (connection != null) {
                context.unbindService(connection);
            }
        }
    }

    public Response request(Class<? extends HermesService> service, Request request) {
        // 取出HermesService对应的binder对象
        IHermesService iHermesService = mHermesServices.get(service);
        if (iHermesService != null) {
            try {
                // 发送到HermesService，返回值是序列化的Response
                return iHermesService.send(request);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setHermesListener(HermesListener listener) {
        mListener = listener;
    }

    private class HermesServiceConnection implements ServiceConnection {

        private Class<? extends HermesService> mClass;

        HermesServiceConnection(Class<? extends HermesService> service) {
            mClass = service;
        }

        public void onServiceConnected(ComponentName className, IBinder service) {
            synchronized (Channel.this) {
                IHermesService hermesService = IHermesService.Stub.asInterface(service);
                // 对应的binder对象，保存到HashMap
                mHermesServices.put(mClass, hermesService);
            }
            if (mListener != null) {
                mListener.onHermesConnected(mClass);
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            synchronized (Channel.this) {
                mHermesServices.remove(mClass);
            }
            if (mListener != null) {
                mListener.onHermesDisconnected(mClass);
            }
        }
    }
}
