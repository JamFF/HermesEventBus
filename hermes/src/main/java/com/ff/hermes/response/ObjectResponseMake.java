package com.ff.hermes.response;

import com.ff.hermes.request.RequestBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * description: 调用单例对象中的方法
 * author: FF
 * time: 2019-07-07 15:56
 */
public class ObjectResponseMake extends ResponseMake {

    private Object mObject;

    private Method mMethod;

    @Override
    protected void setMethod(RequestBean requestBean) {
        // 1.得到单例对象
        mObject = OBJECT_CENTER.getObject(resultClass.getName());
        // 2.获取调用的方法
        mMethod = typeCenter.getMethod(mObject.getClass(), requestBean);
    }

    @Override
    protected Object invokeMethod() {
        try {
            // 调用单例对象的方法
            return mMethod.invoke(mObject, mParameters);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
