package com.ff.hermes.response;

import com.ff.hermes.ObjectCenter;
import com.ff.hermes.Request;
import com.ff.hermes.Response;
import com.ff.hermes.request.RequestBean;
import com.ff.hermes.request.RequestParameter;
import com.ff.hermes.util.TypeCenter;
import com.google.gson.Gson;

/**
 * description: 策略模式
 * author: FF
 * time: 2019-07-07 11:50
 */
public abstract class ResponseMake {

    // UserManage的Class
    protected Class<?> resultClass;

    // 参数数组
    protected Object[] mParameters;

    private Gson GSON = new Gson();

    protected TypeCenter typeCenter = TypeCenter.INSTANCE;

    // 存储单例对象
    protected static final ObjectCenter OBJECT_CENTER = ObjectCenter.INSTANCE;

    protected abstract void setMethod(RequestBean requestBean);

    protected abstract Object invokeMethod();

    public Response makeResponse(Request request) {
        // 1.从Request中取出RequestBean
        RequestBean requestBean = GSON.fromJson(request.getData(), RequestBean.class);

        // 2.得到类名
        resultClass = typeCenter.getClassType(requestBean.getResultClassName());

        // 3.得到参数
        RequestParameter[] requestParameters = requestBean.getRequestParameter();
        if (requestParameters != null && requestParameters.length > 0) {
            mParameters = new Object[requestParameters.length];
            for (int i = 0; i < requestParameters.length; i++) {
                RequestParameter requestParameter = requestParameters[i];
                Class<?> clazz = typeCenter.getClassType(requestParameter.getParameterClassName());
                if (clazz == null) {
                    continue;
                }
                mParameters[i] = GSON.fromJson(requestParameter.getParameterValue(), clazz);
            }
        } else {
            mParameters = new Object[0];
        }

        setMethod(requestBean);// 调用子类

        Object resultObject = invokeMethod();// 调用子类

        // 将返回值封装为ResponseBean
        ResponseBean responseBean = new ResponseBean(resultObject);

        // 把得到的结果序列化成字符串
        String data = GSON.toJson(responseBean);
        // 返回Response
        return new Response(data);
    }
}
