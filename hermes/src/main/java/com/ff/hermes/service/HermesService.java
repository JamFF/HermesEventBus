package com.ff.hermes.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.ff.hermes.Hermes;
import com.ff.hermes.IHermesService;
import com.ff.hermes.Request;
import com.ff.hermes.Response;
import com.ff.hermes.response.InstanceResponseMake;
import com.ff.hermes.response.ObjectResponseMake;
import com.ff.hermes.response.ResponseMake;

/**
 * description: 默认内置服务端（中转站）
 * author: FF
 * time: 2019-07-06 10:57
 */
public class HermesService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private IHermesService.Stub mBinder = new IHermesService.Stub() {
        /**
         * 收到Hermes发送来的Request，对请求参数进行处理
         * @param request 请求参数
         * @return Response 封装为Response进行返回到客户端
         */
        @Override
        public Response send(Request request) throws RemoteException {
            ResponseMake responseMake;
            switch (request.getType()) {
                case Hermes.TYPE_INSTANCE:
                    // 创建单例对象
                    responseMake = new InstanceResponseMake();
                    break;
                case Hermes.TYPE_NEW:
                    // 获取对象
                    responseMake = new ObjectResponseMake();
                    break;
                default:
                    throw new RuntimeException("type " + request.getType() + " not supported");
            }
            return responseMake.makeResponse(request);
        }
    };
}
