package com.ff.eventbus.lib;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * description: 手写实现EventBus
 * author: FF
 * time: 2019-07-04 22:37
 */
public enum EventBus {

    INSTANCE;

    private static final Map<Object, List<SubscriberMethod>> METHOD_CACHE = new HashMap<>();
    private static final int MODIFIERS_IGNORE = Modifier.ABSTRACT | Modifier.STATIC;

    private Handler mHandler;
    private ExecutorService mExecutorService;

    EventBus() {
        mHandler = new Handler(Looper.getMainLooper());
        mExecutorService = Executors.newCachedThreadPool();
    }

    /**
     * 注册
     */
    public void register(@NonNull Object subscriber) {
        List<SubscriberMethod> subscriberMethods = METHOD_CACHE.get(subscriber);
        Class<?> subscriberClass = subscriber.getClass();
        if (subscriberMethods == null) {
            // 没有缓存时遍历查找
            subscriberMethods = findUsingInfo(subscriberClass);
            if (subscriberMethods.isEmpty()) {
                throw new RuntimeException("Subscriber " + subscriberClass
                        + " and its super classes have no public methods with the @Subscribe annotation");
            } else {
                METHOD_CACHE.put(subscriber, subscriberMethods);
            }
        }
    }

    private List<SubscriberMethod> findUsingInfo(Class<?> subscriberClass) {
        List<SubscriberMethod> subscriberMethods = new ArrayList<>();
        Class<?> clazz = subscriberClass;
        // while目的是，获取父类的注解
        while (clazz != null) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                // 获取方法修饰符
                int modifiers = method.getModifiers();
                if ((modifiers & Modifier.PUBLIC) != 0 && (modifiers & MODIFIERS_IGNORE) == 0) {
                    // 仅支持公共的，非静态，非抽象的方法
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == 1) {
                        // 仅支持一个方法参数
                        Subscribe subscribeAnnotation = method.getAnnotation(Subscribe.class);
                        if (subscribeAnnotation != null) {
                            Class<?> eventType = parameterTypes[0];// 参数类型
                            ThreadMode threadMode = subscribeAnnotation.threadMode();// 线程类型
                            // 封装
                            subscriberMethods.add(new SubscriberMethod(method, eventType, threadMode));
                        }
                    } else if (method.isAnnotationPresent(Subscribe.class)) {
                        String methodName = method.getDeclaringClass().getName() + "." + method.getName();
                        throw new RuntimeException("@Subscribe method " + methodName +
                                "must have exactly 1 parameter but has " + parameterTypes.length);
                    }
                } else if (method.isAnnotationPresent(Subscribe.class)) {
                    String methodName = method.getDeclaringClass().getName() + "." + method.getName();
                    throw new RuntimeException(methodName +
                            " is a illegal @Subscribe method: must be public, non-static, and non-abstract");
                }
            }

            clazz = clazz.getSuperclass();
            if (clazz != null) {
                String clazzName = clazz.getName();
                if (clazzName.startsWith("java.")
                        || clazzName.startsWith("javax.")
                        || clazzName.startsWith("android.")
                        || clazzName.startsWith("androidx.")) {
                    // 跳过系统类
                    clazz = null;
                }
            }
        }
        return subscriberMethods;
    }

    /**
     * 反注册
     */
    public void unregister(@NonNull Object subscriber) {
        mHandler.removeCallbacksAndMessages(null);
        METHOD_CACHE.remove(subscriber);
    }

    public void post(@NonNull final Object event) {

        for (final Map.Entry<Object, List<SubscriberMethod>> entry : METHOD_CACHE.entrySet()) {
            for (final SubscriberMethod subscriberMethod : entry.getValue()) {
                if (subscriberMethod.eventType.isAssignableFrom(event.getClass())) {
                    // 根据方法参数判断这个方法是否应该接收事件
                    switch (subscriberMethod.threadMode) {
                        case MAIN:// 主线程
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                invoke(subscriberMethod, entry.getKey(), event);
                            } else {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscriberMethod, entry.getKey(), event);
                                    }
                                });
                            }
                            break;
                        case BACKGROUND://
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                mExecutorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscriberMethod, entry.getKey(), event);
                                    }
                                });
                            } else {
                                invoke(subscriberMethod, entry.getKey(), event);
                            }
                            break;
                        case ASYNC:// 新开子线程
                            mExecutorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    invoke(subscriberMethod, entry.getKey(), event);
                                }
                            });
                            break;
                        default:
                            invoke(subscriberMethod, entry.getKey(), event);
                            break;

                    }
                }
            }
        }
    }

    private void invoke(SubscriberMethod subscriberMethod, Object key, Object event) {
        try {
            subscriberMethod.method.invoke(key, event);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
