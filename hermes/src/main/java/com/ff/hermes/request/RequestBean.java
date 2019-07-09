package com.ff.hermes.request;

import java.util.Arrays;

/**
 * description: 请求
 * author: FF
 * time: 2019-07-07 10:45
 */
public class RequestBean {

    // 请求单例的全类名
    private String className;

    // 返回结果类型，类名
    private String resultClassName;

    // 用不到
    private String requestObject;

    // 方法名
    private String methodName;

    // 方法参数
    private RequestParameter[] requestParameter;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getResultClassName() {
        return resultClassName;
    }

    public void setResultClassName(String resultClassName) {
        this.resultClassName = resultClassName;
    }

    public String getRequestObject() {
        return requestObject;
    }

    public void setRequestObject(String requestObject) {
        this.requestObject = requestObject;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public RequestParameter[] getRequestParameter() {
        return requestParameter;
    }

    public void setRequestParameter(RequestParameter[] requestParameter) {
        this.requestParameter = requestParameter;
    }

    @Override
    public String toString() {
        return "RequestBean{" +
                "className='" + className + '\'' +
                ", resultClassName='" + resultClassName + '\'' +
                ", requestObject='" + requestObject + '\'' +
                ", methodName='" + methodName + '\'' +
                ", requestParameter=" + Arrays.toString(requestParameter) +
                '}';
    }
}
