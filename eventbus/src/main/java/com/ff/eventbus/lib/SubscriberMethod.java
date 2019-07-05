package com.ff.eventbus.lib;

import java.lang.reflect.Method;

/**
 * description: 注册类中的注册方法信息
 * author: FF
 * time: 2019-07-05 08:27
 */
public class SubscriberMethod {

    // 注册方法
    final Method method;
    // 线程类型
    final ThreadMode threadMode;
    // 参数类型
    final Class<?> eventType;

    public SubscriberMethod(Method method, Class<?> eventType, ThreadMode threadMode) {
        this.method = method;
        this.threadMode = threadMode;
        this.eventType = eventType;
    }
}
