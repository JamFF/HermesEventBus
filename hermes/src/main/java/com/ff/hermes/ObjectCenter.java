package com.ff.hermes;

import java.util.concurrent.ConcurrentHashMap;

/**
 * description: 保存单例对象的类
 * author: FF
 * time: 2019-07-07 12:08
 */
public enum ObjectCenter {

    INSTANCE;

    private final ConcurrentHashMap<String, Object> mObjects;

    ObjectCenter() {
        mObjects = new ConcurrentHashMap<>();
    }

    public Object getObject(String name) {
        return mObjects.get(name);
    }

    public void putObject(String name, Object object) {
        mObjects.put(name, object);
    }
}

