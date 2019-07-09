package com.ff.hermes.request;

/**
 * description: 方法参数
 * author: FF
 * time: 2019-07-07 10:49
 */
public class RequestParameter {

    // 参数类型
    private String parameterClassName;
    // 参数值
    private String parameterValue;

    public RequestParameter() {
    }

    public RequestParameter(String parameterClassName, String parameterValue) {
        this.parameterClassName = parameterClassName;
        this.parameterValue = parameterValue;
    }

    public String getParameterClassName() {
        return parameterClassName;
    }

    public void setParameterClassName(String parameterClassName) {
        this.parameterClassName = parameterClassName;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    @Override
    public String toString() {
        return "RequestParameter{" +
                "parameterClassName='" + parameterClassName + '\'' +
                ", parameterValue='" + parameterValue + '\'' +
                '}';
    }
}
