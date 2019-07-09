package com.ff.hermes;

import com.ff.hermes.service.HermesService;

/**
 * description: 绑定/解绑HermesService的监听
 * author: FF
 * time: 2019-07-09 10:18
 */
public abstract class HermesListener {

    public abstract void onHermesConnected(Class<? extends HermesService> service);

    public void onHermesDisconnected(Class<? extends HermesService> service) {

    }
}
