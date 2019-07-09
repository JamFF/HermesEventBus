package com.ff.hermes;

import android.content.Context;

import com.ff.hermes.annotion.ClassId;
import com.ff.hermes.request.RequestBean;
import com.ff.hermes.request.RequestParameter;
import com.ff.hermes.service.HermesService;
import com.ff.hermes.util.TypeCenter;
import com.ff.hermes.util.TypeUtils;
import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * description:
 * author: FF
 * time: 2019-07-06 14:18
 */
public enum Hermes {

    INSTANCE;

    private static final String TAG = "Hermes";

    // 得到对象
    public static final int TYPE_NEW = 0;
    // 得到单例
    public static final int TYPE_INSTANCE = 1;

    private static final Channel CHANNEL = Channel.INSTANCE;
    private static final Gson GSON = new Gson();
    private Context mContext;
    private TypeCenter mTypeCenter;

    Hermes() {
        mTypeCenter = TypeCenter.INSTANCE;
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * 注册需要跨进程的类，其实就是为了减少反射，进行缓存
     *
     * @param clazz 需要跨进程的类
     */
    public void register(Class<?> clazz) {
        mTypeCenter.register(clazz);
    }

    /**
     * 同包名（app内部）客户端连接内置HermesService
     */
    public void connect(Context context) {
        connect(context, null, HermesService.class);
    }

    /**
     * 同包名（app内部）客户端连接自定义HermesService
     */
    public void connect(Context context, Class<? extends HermesService> service) {
        connectApp(context, null, service);
    }

    /**
     * 客户端连接服务端
     *
     * @param packageName 不同app通讯，需要传入服务端包名，同一应用包名一致
     * @param service     可自定义HermesService
     */
    public void connect(Context context, String packageName, Class<? extends HermesService> service) {
        connectApp(context, packageName, service);
    }

    public void connectApp(Context context, String packageName) {
        connectApp(context, packageName, HermesService.class);
    }

    private void connectApp(Context context, String packageName, Class<? extends HermesService> service) {
        init(context);
        // bindService
        CHANNEL.bind(context.getApplicationContext(), packageName, service);
    }

    /**
     * 获取单例的代理对象
     */
    public static <T> T getInstance(Class<T> clazz) {
        // 发送请求，发送创建单例对象的消息到服务端
        // 创建单例对象必须命名为getInstance()，为了方便写死在TypeUtils的getMethodForGettingInstance()
        sendRequest(HermesService.class, clazz, null, null, Hermes.TYPE_INSTANCE);

        // 得到代理对象
        return getProxy(HermesService.class, clazz);
    }

    /**
     * 创建动态代理对象
     */
    private static <T> T getProxy(Class<? extends HermesService> service, Class<T> clazz) {
        Object proxy = Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz},
                new HermesInvocationHandler<>(service, clazz));
        return (T) proxy;
    }

    /**
     * 发送Request
     *
     * @param service
     * @param clazz      {@link HermesService}的子类，目的是增加扩展性
     * @param method
     * @param parameters
     * @param <T>
     * @return
     */
    public static <T> Response sendRequest(Class<? extends HermesService> service, Class<T> clazz,
                                           Method method, Object[] parameters, int type) {

        // 1.封装RequestBean
        RequestBean requestBean = new RequestBean();

        // 1.1 设置全类名
        String className;
        ClassId annotation = clazz.getAnnotation(ClassId.class);
        if (annotation == null) {
            // 没有使用注解，从参数获取
            className = clazz.getName();
        } else {
            // 从注解中拿到全类名
            className = annotation.value();
        }
        requestBean.setClassName(className);
        requestBean.setResultClassName(className);

        // 1.2 设置方法
        if (method != null) {
            // 传入："(方法名+参数名)"组合后的参数
            requestBean.setMethodName(TypeUtils.getMethodId(method));
        }

        // 1.3 设置参数
        RequestParameter[] requestParameters = null;
        if (parameters != null && parameters.length > 0) {
            requestParameters = new RequestParameter[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Object parameter = parameters[i];
                String parameterClassName = parameter.getClass().getName();
                String parameterValue = GSON.toJson(parameter);
                // 封装方法参数
                RequestParameter requestParameter = new RequestParameter(parameterClassName, parameterValue);
                requestParameters[i] = requestParameter;
            }
        }

        if (requestParameters != null) {
            requestBean.setRequestParameter(requestParameters);
        }

        // 2.封装到Request中，通过type区分单例、对象
        Request request = new Request(GSON.toJson(requestBean), type);

        // 3. 发送到服务端，也就是HermesService中
        return CHANNEL.request(service, request);
    }

    public void disconnect(Context context) {
        disconnect(context, HermesService.class);
    }

    public void disconnect(Context context, Class<? extends HermesService> service) {
        CHANNEL.unbind(context.getApplicationContext(), service);
    }

    public void setHermesListener(HermesListener listener) {
        CHANNEL.setHermesListener(listener);
    }
}