package com.ff.hermes.response;

import android.util.Log;

import com.ff.hermes.request.RequestBean;
import com.ff.hermes.request.RequestParameter;
import com.ff.hermes.util.TypeUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * description: 调用获取单例对象的方法
 * author: FF
 * time: 2019-07-07 12:02
 */
public class InstanceResponseMake extends ResponseMake {

    private static final String TAG = "InstanceResponseMake";

    // 单例的getInstance()方法
    private Method mMethod;

    /**
     * 获取Method，解析参数找到 getInstance()
     *
     * @param requestBean
     */
    @Override
    protected void setMethod(RequestBean requestBean) {

        // 1.获取方法名，可能有重载，还要匹配参数
        String methodName = requestBean.getMethodName();
        Log.d(TAG, "setMethod: methodName = " + methodName);

        // 2.获取方法参数
        RequestParameter[] parameters = requestBean.getRequestParameter();

        Class<?>[] parameterTypes = null;
        if (parameters != null && parameters.length > 0) {
            Log.d(TAG, "setMethod: parameters size = " + parameters.length);
            parameterTypes = new Class<?>[parameters.length];
            for (int i = 0; i < parameters.length; ++i) {
                parameterTypes[i] = typeCenter.getClassType(parameters[i].getParameterClassName());
            }
        } else {
            Log.d(TAG, "setMethod: parameters size = 0");
        }

        // 3.根据跨进程类名、方法名、参数名获取到创建单例方法
        mMethod = TypeUtils.getMethodForGettingInstance(resultClass, methodName, parameterTypes);
    }

    @Override
    protected Object invokeMethod() {

        Object object = null;
        try {
            // 反射调用getInstance，获取单例对象
            object = mMethod.invoke(null, mParameters);
            Log.d(TAG, "invokeMethod: " + object.toString());

            // 将单例对象缓存起来
            OBJECT_CENTER.putObject(object.getClass().getName(), object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return object;
    }
}
