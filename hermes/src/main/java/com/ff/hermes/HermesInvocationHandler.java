package com.ff.hermes;

import android.text.TextUtils;
import android.util.Log;

import com.ff.hermes.response.ResponseBean;
import com.ff.hermes.service.HermesService;
import com.google.gson.Gson;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * description:
 * author: FF
 * time: 2019-07-08 14:07
 */
public class HermesInvocationHandler<T> implements InvocationHandler {

    private static final String TAG = "HermesInvocationHandler";
    private static final Gson GSON = new Gson();
    private Class<? extends HermesService> hermesService;
    private Class<T> clazz;

    public HermesInvocationHandler(Class<? extends HermesService> service, Class<T> clazz) {
        this.hermesService = service;
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.d(TAG, "invoke: " + method.getName());
        // 发送请求，调用method方法，Response是将method方法的返回值进行封装
        Response response = Hermes.sendRequest(hermesService, clazz, method, args, Hermes.TYPE_NEW);
        if (response != null && !TextUtils.isEmpty(response.getData())) {
            ResponseBean responseBean = GSON.fromJson(response.getData(), ResponseBean.class);
            Object getUserResult = responseBean.getData();
            if (getUserResult != null) {
                String data = GSON.toJson(getUserResult);

                // 得到method方法返回值类型
                Class<?> returnType = method.getReturnType();
                return GSON.fromJson(data, returnType);
            }
        }
        return null;
    }
}
