package com.ff.hermes.util;

import android.text.TextUtils;
import android.util.Log;

import com.ff.hermes.request.RequestBean;
import com.ff.hermes.request.RequestParameter;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description: 数据缓存
 * author: FF
 * time: 2019-07-06 14:40
 */
public enum TypeCenter {

    INSTANCE;

    private static final String TAG = "TypeCenter";

    // 为了减少反射，所以采用HashMap缓存
    private final ConcurrentHashMap<String, Class<?>> mRawClasses;
    private final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, Method>> mRawMethods;

    TypeCenter() {
        mRawClasses = new ConcurrentHashMap<>();
        mRawMethods = new ConcurrentHashMap<>();
    }

    public void register(Class<?> clazz) {
        registerClass(clazz);
        registerMethod(clazz);
    }

    private void registerClass(Class<?> clazz) {
        String className = clazz.getName();
        // putIfAbsent：不存在key对应的value就添加，存在不做任何操作
        mRawClasses.putIfAbsent(className, clazz);
    }

    private void registerMethod(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            mRawMethods.putIfAbsent(clazz, new ConcurrentHashMap<String, Method>());
            ConcurrentHashMap<String, Method> map = mRawMethods.get(clazz);
            String methodId = TypeUtils.getMethodId(method);
            map.putIfAbsent(methodId, method);// map不可能为null
        }
    }

    public Class<?> getClassType(String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        Class<?> clazz = mRawClasses.get(name);
        if (clazz == null) {
            try {
                clazz = Class.forName(name);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return clazz;
    }

    public Method getMethod(Class<?> clazz, RequestBean requestBean) {
        String methodName = requestBean.getMethodName();
        if (methodName != null) {
            Log.d(TAG, "getMethod: methodName = " + methodName);
            mRawMethods.putIfAbsent(clazz, new ConcurrentHashMap<String, Method>());
            ConcurrentHashMap<String, Method> map = mRawMethods.get(clazz);
            Method method = map.get(methodName);// map不可能为null
            if (method != null) {
                Log.d(TAG, "getMethod: 存在缓存 " + method.getName());
                return method;
            }
            int pos = methodName.indexOf('(');

            // 方法参数
            Class[] paramters = null;
            RequestParameter[] requestParameters = requestBean.getRequestParameter();
            if (requestParameters != null && requestParameters.length > 0) {
                paramters = new Class[requestParameters.length];
                for (int i = 0; i < requestParameters.length; i++) {
                    paramters[i] = getClassType(requestParameters[i].getParameterClassName());
                }
            }
            method = TypeUtils.getMethod(clazz, methodName.substring(0, pos), paramters);
            map.putIfAbsent(methodName, method);
            Log.d(TAG, "getMethod: 没有缓存 " + method.getName());
            return method;
        }
        return null;
    }
}
